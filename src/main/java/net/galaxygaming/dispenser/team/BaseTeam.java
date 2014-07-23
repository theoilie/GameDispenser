package net.galaxygaming.dispenser.team;

import java.util.UUID;

import org.bukkit.entity.Player;

public interface BaseTeam {

	public String getName();
	
	public void add(Player player);
	
	public void add(UUID uuid);
	
	public void remove(Player player);
	
	public void remove(UUID uuid);
	
	public boolean isOnTeam(Player player);
	
	public boolean isOnTeam(UUID uuid);
	
	public int getSize();
}