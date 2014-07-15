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
    
	private final JavaPlugin plugin;
	private static boolean initialized;
	
	public LogUtil(JavaPlugin plugin) {
		if (initialized)
			throw new AssertionError("Cannot instantiate utility class.");
		else
			this.plugin = plugin;
		initialized = true;
	}

	public final void log(Level level, String msg, Object... objects) {
		plugin.getLogger().log(level, FormatUtil.format(msg, objects));		
	}

	public final void log(String msg, Object... objects) {
		log(Level.INFO, msg, objects);
	}
}