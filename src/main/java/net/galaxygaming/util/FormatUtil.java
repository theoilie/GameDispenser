/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.util;

import java.text.MessageFormat;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;

/**
 * @author t7seven7t
 */
public class FormatUtil {

	public FormatUtil() {
		throw new AssertionError("Cannot instantiate utility class.");
	}
	
    public static String format(String format, Object... objects) {
        String ret = MessageFormat.format(format, objects);
        ret = WordUtils.capitalize(ret, new char[]{'.'});
        return ChatColor.translateAlternateColorCodes('&', ret);
    }   
}