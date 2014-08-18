package net.galaxygaming.util;

import java.text.MessageFormat;

import net.galaxygaming.dispenser.event.EventManager;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;

public class FormatUtil {

	public FormatUtil() {
		throw new AssertionError("Cannot instantiate utility class.");
	}
	
	/**
	 * Formats a message
	 * @param format the message that should be formatted
	 * @param objects what to replace the {0}, {1}, etc... with
	 * @return
	 */
    public static String format(String format, Object... objects) {
        String ret = MessageFormat.format(format, objects);
        ret = WordUtils.capitalize(ret, new char[]{'.'});
        return ChatColor.translateAlternateColorCodes('&', ret);
    }
    
    /**
     * Cloning is not supported.
     */
    @Override
    public EventManager clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}