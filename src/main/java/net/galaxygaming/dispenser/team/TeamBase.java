package net.galaxygaming.dispenser.team;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;

public class TeamBase implements Team {
	private final List<Player> members = new ArrayList<Player>();
	private final String name;

	public TeamBase(String name) {
	    this.name = name;
	}
	
	@Override
	public void add(Player player) {
		add(player);
	}

	/*
	 * Checking for contains before removal is unnecessary
	 */
	@Override
	public void remove(Player player) {
		remove(player);
	}
	
	@Override
	public boolean isOnTeam(Player player) {
		return members.contains(player);
	}
	
	@Override
	public int getSize() {
		return members.size();
	}
	
	@Override
	public String getName() {
	    return this.name;
	}
}