package net.galaxygaming.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationUtil {

	public LocationUtil() {
		throw new AssertionError("Cannot instantiate utility class.");
	}
	
	/**
	 * Converts a Location to a String.
	 * @param location - The Location to be converted.
	 * @return The String form of the location parameter.
	 */
	public static String serializeLocation(Location location) {
		StringBuilder builder = new StringBuilder();
		builder.append(location.getWorld().getName());
		builder.append(',');
		builder.append(location.getBlockX());
		builder.append(',');
		builder.append(location.getBlockY());
		builder.append(',');
		builder.append(location.getBlockZ());
		return builder.toString();
	}
	
	/**
	 * Converts a String to a Location
	 * @param string - The String to be converted.
	 * @return The Location form of the string parameter.
	 */
	public static Location deserializeLocation(String string) {
	    if (string == null) {
	        return null;
	    }
	    
		String[] args = string.split(",");
		
		if (args.length < 4)
			throw new RuntimeException("Location string was invalid: " + string);
		
		int x, y, z;
		
		try {
			x = Integer.valueOf(args[1]);
			y = Integer.valueOf(args[2]);
			z = Integer.valueOf(args[3]);
		} catch (NumberFormatException e) {
			throw new RuntimeException("Location string was invalid: " + string);
		}
		
		World world = Bukkit.getWorld(args[0]);
		
		if (world == null)
			throw new RuntimeException("Location string was invalid: " + string);
		
		return new Location(world, x, y, z);
	}	
}