package net.galaxygaming.dispenser.storage;

import java.util.UUID;

import org.bukkit.entity.Player;

public class Stats extends Storage {

	public int getKills(Player player) {
		return getKills(player.getUniqueId());
	}
	
	public int getKills(UUID uuid) {
		return (int) getDatabase().get("kills", uuid.toString());
	}
	
	@Override
	public String getName() {
		return "stats";
	}

	@Override
	public String serialize() {
		// TODO
		return null;
	}

	@Override
	public void deserialize(String storage) {
		// TODO
	}
}