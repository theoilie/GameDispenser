package net.galaxygaming.util;

import net.galaxygaming.dispenser.event.EventManager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationUtil {

	public LocationUtil() {
		throw new AssertionError("Cannot instantiate utility class.");
	}
	
	/**
	 * Converts a Location to a String
	 * @param location the Location to be converted
	 * @return the String form of the location parameter
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
	 * Converts a Location to a String leaving off the world name at the beginning
     * @param location the Location to be converted
     * @return the String form of the location parameter
	 */
	public static String serializeLocationShort(Location location) {
	    StringBuilder builder = new StringBuilder();
        builder.append(location.getBlockX());
        builder.append(',');
        builder.append(location.getBlockY());
        builder.append(',');
        builder.append(location.getBlockZ());
        return builder.toString();
	}
	
	/**
	 * Converts a String to a Location
	 * @param string the String to be converted
	 * @return the Location form of the string parameter
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
	
    /**
     * Cloning is not supported.
     */
    @Override
    public EventManager clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}