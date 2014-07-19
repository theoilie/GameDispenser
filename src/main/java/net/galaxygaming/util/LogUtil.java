/**
 * Copyright (C) 2012 t7seven7t
 */
package net.galaxygaming.util;

import java.util.logging.Level;

import org.bukkit.plugin.java.JavaPlugin;


/**
 * @author t7seven7t
 */
public class LogUtil {
    
	/** Singleton instance */
	private static final LogUtil instance = new LogUtil();
	
    private JavaPlugin plugin;
    
    private LogUtil() {
        
    }
    
    public void setup(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    
    public static LogUtil getInstance() {
        return instance;
    }

	public final void log(Level level, String msg, Object... objects) {
		plugin.getLogger().log(level, FormatUtil.format(msg, objects));		
	}

	public final void log(String msg, Object... objects) {
		log(Level.INFO, msg, objects);
	}
}