package net.galaxygaming.util;

import java.text.MessageFormat;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;

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