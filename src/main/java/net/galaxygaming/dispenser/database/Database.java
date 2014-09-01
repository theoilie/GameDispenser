package net.galaxygaming.dispenser.database;

import net.galaxygaming.dispenser.GameDispenser;

public abstract class Database {
	private static Database currentDatabase;
	
	public int getEXP(String minigame, String player) {
		return (int) get(minigame, player, "exp");
	}
	
	public void setEXP(String minigame, String player, int exp) {
		set(minigame, player, "exp", exp);
	}
	
	public int getGamesWon(String minigame, String player) {
		return (int) get(minigame, player, "games won");
	}
	
	public void setGamesWon(String minigame, String player, int gamesWon) {
		set(minigame, player, "games won", gamesWon);
	}
	
	public int getGamesLost(String minigame, String player) {
		return (int) get(minigame, player, "games lost");
	}
	
	public void setGamesLost(String minigame, String player, int gamesLost) {
		set(minigame, player, "games lost", gamesLost);
	}
	
	public int getTotalEXP(String player) {
		// TODO: Calculate total exp.
		return 0;
	}
	
	public void setTotalEXP(String player, int exp) {
		
	}
	
	public int getTotalGamesWon(String player) {
		// TODO: Calculate total games won.
		return 0;
	}
	
	public void setTotalGamesWon(String player, int gamesWon) {
		
	}
	
	public int getTotalGamesLost(String player) {
		// TODO: Calculate total games lost
		return 0;
	}
	
	public void setTotalGamesLost(String player, int gamesLost) {
		
	}
	
	/**
	 * Gets something from the database
	 * @param minigame the minigame to get from
	 * @param player the player associated with what to get
	 * @param key what to get, such as kills, deaths, wins, etc...
	 * @return the value paired with the key
	 */
	protected abstract Object get(String minigame, String player, String key);
	
	/**
	 * Sets something in the database
	 * @param minigame the minigame to set
	 * @param player the player associated with what to set
	 * @param key what to set, such as kills, deaths, wins, etc...
	 * @param value the value that the key will be set to
	 */
	protected abstract void set(String minigame, String player, String key, Object value);
	
	/**
	 * Executes any tasks required to unload the database
	 */
	public abstract void unload();
	
	/**
	 * Gets the database type that is being used, as set in the config.
	 * @return the current database being used
	 */
	public static Database getDatabase() {
		switch (GameDispenser.getInstance().getConfig().getString("database").toLowerCase().replaceAll(" ", "")) {
		case "mysql":
		case "sql":
			if (!(currentDatabase instanceof MySQL))
				currentDatabase = new MySQL();
		default:
			if (!(currentDatabase instanceof YAML))
				currentDatabase = new YAML();
		}
		return currentDatabase;
	}
}