package net.galaxygaming.dispenser.team;

import org.bukkit.entity.Player;

public interface Team {

    /**
     * Gives the name of this team
     * @return team name
     */
	public String getName();
	
	/**
	 * Adds a player to this team
	 * @param player to be added
	 */
	public void add(Player player);
		
	/**
	 * Removes a player from this team
	 * @param player to be removed
	 */
	public void remove(Player player);
		
	/**
	 * Checks whether a player is on this team
	 * @param player to check for
	 * @return true if player is on the team
	 */
	public boolean isOnTeam(Player player);
		
	/**
	 * Gives the number of players on the team
	 * @return team size
	 */
	public int getSize();
}