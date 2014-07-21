/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.metadata;

import org.bukkit.metadata.MetadataValue;

import net.galaxygaming.dispenser.game.Game;

/**
 * @author t7seven7t
 */
public interface GameMetadata extends MetadataValue {

    public Game getGame();
    
}
