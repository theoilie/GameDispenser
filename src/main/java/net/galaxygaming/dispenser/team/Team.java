package net.galaxygaming.dispenser.team;

import java.util.ArrayList;
import java.util.List;

import net.galaxygaming.util.FormatUtil;

import org.bukkit.entity.Player;

public class Team {
	private final List<Player> members = new ArrayList<Player>();
	private final String name;

	public Team(String name) {
	    this.name = name;
	}
	
	/**
     * Adds a player to this team
     * @param player the player to be added
     */
	public void add(Player player) {
	    members.add(player);
	    player.sendMessage(FormatUtil.format("&eYou are on the &6" + name + "&e team."));
	}
	
	/**
     * Removes a player from this team
     * @param player the player to be removed
     */
	public void remove(Player player) {
	    members.remove(player);
	}
	
	/**
	 * Resets this team to have no members
	 */
	public void reset() {
	    members.clear();
	}
	
	/**
     * Checks whether a player is on this team
     * @param player the player to check for
     * @return true if the player is on this team
     */
	public boolean isOnTeam(Player player) {
		return members.contains(player);
	}
	
	/**
     * Gives the number of players on the team
     * @return team count
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