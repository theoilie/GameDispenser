package net.galaxygaming.util;

import java.text.MessageFormat;
import java.util.Map;
import java.util.HashMap;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;

public class FormatUtil {

	public FormatUtil() {
		throw new AssertionError("Cannot instantiate utility class.");
	}
	
	private static char SECTION_SYMBOL = '\u00a7';
	
    @SuppressWarnings("serial")
    private static Map<Integer, String> charList = new HashMap<Integer, String>() {{
        put(-6, Character.toString(SECTION_SYMBOL));
        put(2, "!.,:;i|");
        put(3, "'`l");
        put(4, " I[]t");
        put(5, "\"()*<>fk{}");
        put(7, "@~");
    }};
	
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
    public FormatUtil clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
    
    public static Object[] pxSubstring(String str, int maxLength, boolean isConsole) {
        int totalPixelLength = 0, pixelLength = 0, substringIndex = 0;
        for (char ch : str.toCharArray()) {
            pixelLength += pxLen(ch, isConsole);
            if (pixelLength > maxLength) break;
            substringIndex++;
            totalPixelLength = pixelLength;
        }
        return new Object[]{str.substring(0, substringIndex), totalPixelLength};
    }
    
    public static int pxLen(char ch, boolean isConsole) {
        if (isConsole) return (ch == SECTION_SYMBOL) ? -1 : 1;
        
        int l = 6;
        for (int px : charList.keySet()) {
            if (charList.get(px).indexOf(ch) >= 0) {
                l = px;
                break;
            }
        }
        return l;
    }
}