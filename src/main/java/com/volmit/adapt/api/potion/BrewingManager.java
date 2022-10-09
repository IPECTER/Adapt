package com.volmit.adapt.api.potion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.volmit.adapt.Adapt;
import com.volmit.adapt.api.world.AdaptPlayer;
import com.volmit.adapt.util.J;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;

import java.util.List;
import java.util.Map;

public class BrewingManager implements Listener {

    private static final Map<BrewingRecipe, List<String>> recipes = Maps.newHashMap();
    private static final Map<Location, BrewingTask> activeTasks = Maps.newHashMap();

    public BrewingManager() {
        registerRecipe("test-potion", BrewingRecipe.builder()
                .ingredient(new ItemStack(Material.GOLDEN_CARROT))
                .basePotion(PotionBuilder.vanilla(PotionBuilder.Type.REGULAR, PotionType.WATER, false, false))
                .result(PotionBuilder.of(PotionBuilder.Type.REGULAR).setName("Test Potion").setColor(Color.BLACK).build())
                .brewingTime(60).fuelCost(1).build());
    }

    public static void registerRecipe(String adaptation, BrewingRecipe recipe) {
        recipes.putIfAbsent(recipe, Lists.newArrayList(adaptation));
        recipes.computeIfPresent(recipe, (k, v) -> {
            if (!v.contains(adaptation))
                v.add(adaptation);
            return v;
        });
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null || e.getClickedInventory().getType() != InventoryType.BREWING || e.getClickedInventory().getHolder() == null)
            return;

        J.s(() -> {
            BrewerInventory inv = (BrewerInventory) e.getClickedInventory();
            BrewingStand stand = inv.getHolder();
            AdaptPlayer p = Adapt.instance.getAdaptServer().getPlayer((Player) e.getWhoClicked());
            recipes.keySet().stream().filter(r -> BrewingTask.isValid(r, stand)).findFirst().ifPresent(r -> {
                if (activeTasks.containsKey(stand.getLocation()))
                    activeTasks.remove(stand.getLocation()).cancel();
                if (recipes.get(r).stream().noneMatch(p::hasAdaptation))
                    return;
                activeTasks.put(stand.getLocation(), new BrewingTask(r, inv, stand));
            });
        });
    }
}