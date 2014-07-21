/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.dispenser.game;

import org.bukkit.metadata.FixedMetadataValue;

/**
 * @author t7seven7t
 */
public class GameFixedMetadata extends FixedMetadataValue implements GameMetadata {
    
    public GameFixedMetadata(GameBase game, Object value) {
        super(game.fakePlugin, value);
    }

}
