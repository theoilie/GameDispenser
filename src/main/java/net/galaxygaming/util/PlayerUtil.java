package net.galaxygaming.util;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PlayerUtil {

	public PlayerUtil() {
		throw new AssertionError("Cannot instantiate utility class.");	
	}
	
	/**
	 * Retrieves a player with a name similar to the string given.
	 * @param string player name
	 * @return player or null if no match
	 */
    public static Player matchPlayer(String string) {
        List<Player> players = Bukkit.matchPlayer(string);
        
        if (players.size() >= 1)
            return players.get(0);
        
        return null;
    }
    
    /**
     * Retrieves an {@link OfflinePlayer} with name
     * matching the string given. <br><br>
     * This method will first attempt to return {@link #matchPlayer(String)}.
     * If no player can be found online with that name this will proceed
     * to search for an offline player with an exact name match.
     * @param string player name
     * @return player or null if no match
     */
    public static OfflinePlayer matchOfflinePlayer(String string) {
        if (matchPlayer(string) != null)
            return matchPlayer(string);
        
        for (OfflinePlayer o : Bukkit.getOfflinePlayers()) {
            if (o.getName().equalsIgnoreCase(string))
                return o;
        }
        return null;
    }   
}