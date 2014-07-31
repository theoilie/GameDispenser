package net.galaxygaming.util;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

public class PermissionUtil {

	public PermissionUtil() {
		throw new AssertionError("Cannot instantiate utility class.");	
	}
	
    public static boolean hasPermission(CommandSender sender, Permission permission) {
        return (permission == null) ? true : hasPermission(sender, permission.getName());
    }
    
    public static boolean hasPermission(CommandSender sender, String permission) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            return (p.hasPermission(permission) || p.isOp());
        }
        
        return true;
    }   
}