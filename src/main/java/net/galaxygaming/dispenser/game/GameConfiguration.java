/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.dispenser.game;

import org.bukkit.configuration.file.YamlConfiguration;

/**
 * @author t7seven7t
 */
public class GameConfiguration extends YamlConfiguration {
    @Override
    public Object getDefault(String path) {
        Object result = super.getDefault(path);
        
        if (result == null) {
            throw new IllegalStateException("Game author has set no default for '" + path + "'");
        }

        return result;
    }
}