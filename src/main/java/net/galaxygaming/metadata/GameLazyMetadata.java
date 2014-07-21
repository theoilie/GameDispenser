/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.metadata;

import java.util.concurrent.Callable;

import net.galaxygaming.dispenser.GameDispenser;
import net.galaxygaming.dispenser.game.Game;

import org.bukkit.metadata.LazyMetadataValue;

/**
 * @author t7seven7t
 */
public class GameLazyMetadata extends LazyMetadataValue implements GameMetadata {

    private final Game game;
    
    public GameLazyMetadata(Game game, Callable<Object> lazyValue) {
        super(GameDispenser.getInstance(), lazyValue);
        this.game = game;
    }
    
    public GameLazyMetadata(Game game, LazyMetadataValue.CacheStrategy cacheStrategy, Callable<Object> lazyValue) {
        super(GameDispenser.getInstance(), cacheStrategy, lazyValue);
        this.game = game;
    }

    @Override
    public Game getGame() {
        return game;
    }

}
