package net.galaxygaming.dispenser.database;

import java.util.UUID;

import net.galaxygaming.dispenser.GameDispenser;

import org.bukkit.Bukkit;

public abstract class Database {
	private static Database currentDatabase;
	
	// TODO: Document.
	// TODO: Make a table that automatically sorts all of the stats.
	
	public double getEXP(String minigame, String playerName) {
		return (double) get(minigame, Bukkit.getOfflinePlayer(playerName).getUniqueId(), "exp", "double", 0);
	}
	
	public void setEXP(String minigame, String playerName, double exp) {
		UUID uuid = Bukkit.getOfflinePlayer(playerName).getUniqueId();
		set(minigame, uuid, playerName, "exp", "double", exp);
		set(uuid, playerName, "exp_total", "double", (double) get(uuid, "exp_total", "double", 0) + exp);
	}
	
	public int getGamesWon(String minigame, String playerName) {
		return (int) get(minigame, Bukkit.getOfflinePlayer(playerName).getUniqueId(), "games won", "int", 0);
	}
	
	public void setGamesWon(String minigame, String playerName, int gamesWon) {
		UUID uuid = Bukkit.getOfflinePlayer(playerName).getUniqueId();
		set(minigame, uuid, playerName, "games won", "int", gamesWon);
		set(minigame, uuid, playerName, "games won_total", "int", (int) get(uuid, "games won_total", "int", 0) + gamesWon);
	}
	
	public int getGamesLost(String minigame, String playerName) {
		return (int) get(minigame, Bukkit.getOfflinePlayer(playerName).getUniqueId(), "games lost", "int", 0);
	}
	
	public void setGamesLost(String minigame, String playerName, int gamesLost) {
		UUID uuid = Bukkit.getOfflinePlayer(playerName).getUniqueId();
		set(minigame, uuid, playerName, "games lost", "int", gamesLost);
		set(minigame, uuid, playerName, "games lost_total", "int", (int) get(uuid, "games lost_total", "int", 0) + gamesLost);
	}
	
	public double getTotalEXP(String playerName) {
		return (double) get(Bukkit.getOfflinePlayer(playerName).getUniqueId(), "exp_total", "double", 0);
	}
	
	public void setTotalEXP(String playerName, double exp) {
		set(Bukkit.getOfflinePlayer(playerName).getUniqueId(), playerName, "exp_total", "double", exp);
	}
	
	public int getTotalGamesWon(String playerName) {
		return (int) get(Bukkit.getOfflinePlayer(playerName).getUniqueId(), "games won_total", "double", 0);
	}
	
	public void setTotalGamesWon(String playerName, int gamesWon) {
		set(Bukkit.getOfflinePlayer(playerName).getUniqueId(), playerName, "games won_total", "int", gamesWon);
	}
	
	public int getTotalGamesLost(String playerName) {
		return (int) get(Bukkit.getOfflinePlayer(playerName).getUniqueId(), "games lost_total", "int", 0);
	}
	
	public void setTotalGamesLost(String playerName, int gamesLost) {
		set(Bukkit.getOfflinePlayer(playerName).getUniqueId(), playerName, "games lost_total", "int", gamesLost);
	}
	
	public double getEXP(String minigame, UUID uuid) {
		return (double) get(minigame, uuid, "exp", "double", 0);
	}
	
	public void setEXP(String minigame, UUID uuid, double exp) {
		String name = Bukkit.getOfflinePlayer(uuid).getName();
		set(minigame, uuid, name, "exp", "double", exp);
		set(uuid, name, "exp_total", "double", (double) get(uuid, "exp_total", "double", 0) + exp);
	}
	
	public int getGamesWon(String minigame, UUID uuid) {
		return (int) get(minigame, uuid, "games won", "int", 0);
	}
	
	public void setGamesWon(String minigame, UUID uuid, int gamesWon) {
		String name = Bukkit.getOfflinePlayer(uuid).getName();
		set(minigame, uuid, name, "games won", "int", gamesWon);
		set(minigame, uuid, name, "games won_total", "int", (int) get(uuid, "games won_total", "int", 0) + gamesWon);
	}
	
	public int getGamesLost(String minigame, UUID uuid) {
		return (int) get(minigame, uuid, "games lost", "int", 0);
	}
	
	public void setGamesLost(String minigame, UUID uuid, int gamesLost) {
		String name = Bukkit.getOfflinePlayer(uuid).getName();
		set(minigame, uuid, name, "games lost", "int", gamesLost);
		set(minigame, uuid, name, "games lost_total", "int", (int) get(uuid, "games lost_total", "int", 0) + gamesLost);
	}
	
	public double getTotalEXP(UUID uuid) {
		return (double) get(uuid, "exp_total", "double", 0);
	}
	
	public void setTotalEXP(UUID uuid, double exp) {
		set(uuid, Bukkit.getOfflinePlayer(uuid).getName(), "exp_total", "double", exp);
	}
	
	public int getTotalGamesWon(UUID uuid) {
		return (int) get(uuid, "games won_total", "int", 0);
	}
	
	public void setTotalGamesWon(UUID uuid, int gamesWon) {
		set(uuid, Bukkit.getOfflinePlayer(uuid).getName(), "games won_total", "int", gamesWon);
	}
	
	public int getTotalGamesLost(UUID uuid) {
		return (int) get(uuid, "games lost_total", "int", 0);
	}
	
	public void setTotalGamesLost(UUID uuid, int gamesLost) {
		set(uuid, Bukkit.getOfflinePlayer(uuid).getName(), "games lost_total", "int", gamesLost);
	}
	
	/**
	 * Gets something from the database
	 * @param minigame the minigame to get from
	 * @param playerUUID the unique id of the player associated with what to get
	 * @param key what to get, such as kills, deaths, wins, etc...
	 * @return the value paired with the key
	 */
	protected abstract Object get(String minigame, UUID playerUUID, String key);
	
	/**
	 * Gets something from the database
	 * @param minigame the minigame to get from
	 * @param playerUUID the unique id of the player associated with what to get
	 * @param key what to get, such as kills, deaths, wins, etc...
	 * @param defaultType the type of the default value
	 * @param defaultValue the default value that the key should be
	 * @return the value paired with the key
	 */
	protected abstract Object get(String minigame, UUID playerUUID, String key, String defaultType, Object defaultValue);
	
	/**
	 * Sets something in the database
	 * @param minigame the minigame to set
	 * @param playerUUID the unique id of the player associated with what to set
	 * @param playerName the name of the player associated with what to set
	 * @param key what to set, such as kills, deaths, wins, etc...
	 * @param value the value that the key will be set to
	 */
	protected abstract void set(String minigame, UUID playerUUID, String playerName, String key, Object value);
	
	/**
	 * Sets something in the database
	 * @param minigame the minigame to set
	 * @param playerUUID the unique id of the player associated with what to set
	 * @param playerName the name of the player associated with what to set
	 * @param key what to set, such as kills, deaths, wins, etc...
	 * @param valueType the type of the value
	 * @param value the value that the key will be set to
	 */
	protected abstract void set(String minigame, UUID playerUUID, String playerName, String key, String valueType, Object value);
	
	/**
	 * Gets something from the database
	 * @param playerUUID the unique id of the player associated with what to get
	 * @param key what to get, such as kills, deaths, wins, etc...
	 * @return the value paired with the key
	 */
	protected abstract Object get(UUID playerUUID, String key);
	
	/**
	 * Gets something from the database
	 * @param playerUUID the unique id of the player associated with what to get
	 * @param key what to get, such as kills, deaths, wins, etc...
	 * @param defaultType the type of the default value
	 * @param defaultValue the default value that the key should be
	 * @return the value paired with the key
	 */
	protected abstract Object get(UUID playerUUID, String key, String defaultType, Object defaultValue);
	
	/**
	 * Sets something in the database
	 * @param playerUUID the unique id of the player associated with what to set
	 * @param playerName the player associated with what to set
	 * @param key what to set, such as kills, deaths, wins, etc...
	 * @param value the value that the key will be set to
	 */
	protected abstract void set(UUID playerUUID, String playerName, String key, Object value);
	
	/**
	 * Sets something in the database
	 * @param playerUUID the unique id of the player associated with what to set
	 * @param playerName the player associated with what to set
	 * @param key what to set, such as kills, deaths, wins, etc...
	 * @param valueType the type of the value
	 * @param value the value that the key will be set to
	 */
	protected abstract void set(UUID playerUUID, String playerName, String key, String valueType, Object value);
	
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