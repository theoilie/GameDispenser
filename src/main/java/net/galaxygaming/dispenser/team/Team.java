package net.galaxygaming.dispenser.team;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.entity.Player;

public abstract class Team implements BaseTeam {
	private ArrayList<UUID> members = new ArrayList<UUID>();

	@Override
	public void add(Player player) {
		add(player.getUniqueId());
	}

	@Override
	public void add(UUID uuid) {
		members.add(uuid);
	}

	@Override
	public void remove(Player player) {
		remove(player.getUniqueId());
	}

	@Override
	public void remove(UUID uuid) {
		if (members.contains(uuid))
			members.remove(uuid);
	}

	@Override
	public boolean isOnTeam(Player player) {
		return isOnTeam(player.getUniqueId());
	}

	@Override
	public boolean isOnTeam(UUID uuid) {
		return members.contains(uuid);
	}
	
	@Override
	public int getSize() {
		return members.size();
	}
}