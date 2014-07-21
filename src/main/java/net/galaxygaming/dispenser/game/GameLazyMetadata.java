/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.dispenser.game;

import java.util.concurrent.Callable;

import org.bukkit.metadata.LazyMetadataValue;

/**
 * @author t7seven7t
 */
public class GameLazyMetadata extends LazyMetadataValue implements GameMetadata {
    
    public GameLazyMetadata(GameBase game, Callable<Object> lazyValue) {
        super(game.fakePlugin, lazyValue);
    }
    
    public GameLazyMetadata(GameBase game, LazyMetadataValue.CacheStrategy cacheStrategy, Callable<Object> lazyValue) {
        super(game.fakePlugin, cacheStrategy, lazyValue);
    }

}
