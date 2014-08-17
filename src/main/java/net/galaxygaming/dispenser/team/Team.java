package net.galaxygaming.dispenser.team;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;

public class Team {
	private final List<Player> members = new ArrayList<Player>();
	private final String name;

	public Team(String name) {
	    this.name = name;
	}
	
	/**
     * Adds a player to this team
     * @param player to be added
     */
	public void add(Player player) {
	    members.add(player);
	}
	
	/**
     * Removes a player from this team
     * @param player to be removed
     */
	public void remove(Player player) {
	    members.remove(player);
	}
	
	/**
     * Checks whether a player is on this team
     * @param player to check for
     * @return true if player is on the team
     */
	public boolean isOnTeam(Player player) {
		return members.contains(player);
	}
	
	/**
     * Gives the number of players on the team
     * @return team size
     */
	public int getSize() {
		return members.size();
	}
	
	/**
     * Gives the name of this team
     * @return team name
     */
	public String getName() {
	    return this.name;
	}
}