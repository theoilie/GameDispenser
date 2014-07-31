/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.dispenser.game;

import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.bukkit.plugin.Plugin;

/**
 * @author t7seven7t
 */
class GameLogger extends Logger {
    private String gameName;
    private String pluginName;
    
    public GameLogger(Game game, Plugin plugin) {
        super(game.getClass().getCanonicalName(), null);
        String prefix = plugin.getDescription().getPrefix();
        pluginName = prefix != null ? new StringBuilder().append("[").append(prefix).append(": ").toString() : "[" + plugin.getDescription().getName() + ": ";
        gameName = game.getName() + "] ";
    }
    
    @Override
    public void log(LogRecord logRecord) {
        logRecord.setMessage(pluginName + gameName + logRecord.getMessage());
        super.log(logRecord);
    }
}
