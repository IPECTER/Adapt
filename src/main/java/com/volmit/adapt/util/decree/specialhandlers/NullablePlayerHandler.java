package com.volmit.adapt.util.decree.specialhandlers;

import com.volmit.adapt.util.decree.exceptions.DecreeParsingException;
import com.volmit.adapt.util.decree.handlers.PlayerHandler;
import org.bukkit.entity.Player;

public class NullablePlayerHandler extends PlayerHandler {

    @Override
    public Player parse(String in, boolean force) throws DecreeParsingException {
        return getPossibilities(in).stream().filter((i) -> toString(i).equalsIgnoreCase(in)).findFirst().orElse(null);
    }
}