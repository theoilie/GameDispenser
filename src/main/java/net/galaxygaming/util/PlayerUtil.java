package net.galaxygaming.util;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PlayerUtil {

	public PlayerUtil() {
		throw new AssertionError("Cannot instantiate utility class.");	
	}
	
    public static Player matchPlayer(String p) {
        List<Player> players = Bukkit.matchPlayer(p);
        
        if (players.size() >= 1)
            return players.get(0);
        
        return null;
    }
    
    public static OfflinePlayer matchOfflinePlayer(String p) {
        if (matchPlayer(p) != null)
            return matchPlayer(p);
        
        for (OfflinePlayer o : Bukkit.getOfflinePlayers()) {
            if (o.getName().equalsIgnoreCase(p))
                return o;
        }
        return null;
    }   
}