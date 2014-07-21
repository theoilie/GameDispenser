/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.metadata;

import net.galaxygaming.dispenser.GameDispenser;
import net.galaxygaming.dispenser.game.Game;

import org.bukkit.metadata.FixedMetadataValue;

/**
 * @author t7seven7t
 */
public class GameFixedMetadata extends FixedMetadataValue implements GameMetadata {

    private final Game game;
    
    public GameFixedMetadata(Game game, Object value) {
        super(GameDispenser.getInstance(), value);
        this.game = game;
    }

    @Override
    public Game getGame() {
        return game;
    }

}
