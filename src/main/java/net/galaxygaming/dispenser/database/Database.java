package net.galaxygaming.dispenser.database;

import java.util.UUID;

import net.galaxygaming.dispenser.GameDispenser;

import org.bukkit.Bukkit;

public abstract class Database {
	private static Database currentDatabase;
	
	// TODO: Make a table that automatically sorts all of the stats.
	
	/**
	 * Gets a player's experience points
	 * @param minigame the minigame to get the points from (@see {@link #getTotalEXP(String)} for getting a player's total exp)
	 * @param playerName the name of the player to get the exp from
	 * @return the player's experience points
	 * @deprecated use {@link #getEXP(String, UUID)}
	 */
	public double getEXP(String minigame, String playerName) {
		return (double) get(minigame, Bukkit.getOfflinePlayer(playerName).getUniqueId(), "exp", "double", 0);
	}
	
	/**
	 * Sets the amount of experience points a player has
	 * @param minigame the minigame that the player has the points in (@see {@link #setTotalEXP(String, double)} to set the player's total exp)
	 * @param playerName the name of the player to set the experience points of
	 * @param exp the experience points the player will now have
	 * @deprecated use {@link #setEXP(String, UUID, double)}
	 */
	public void setEXP(String minigame, String playerName, double exp) {
		UUID uuid = Bukkit.getOfflinePlayer(playerName).getUniqueId();
		set(minigame, uuid, playerName, "exp", "double", exp);
		set(uuid, playerName, "exp_total", "double", (double) get(uuid, "exp_total", "double", 0) + exp);
	}
	
	/**
	 * Gets the amount of times a player has won a minigame
	 * @param minigame the minigame the player has won (@see {@link #getTotalGamesWon(String)} for getting a player's total wins)
	 * @param playerName the name of the player to get the win count of
	 * @return how many times the player has won the minigame given
	 * @deprecated use {@link #getGamesWon(String, UUID)}
	 */
	public int getGamesWon(String minigame, String playerName) {
		return (int) get(minigame, Bukkit.getOfflinePlayer(playerName).getUniqueId(), "games won", "int", 0);
	}
	
	/**
	 * Sets how many times a player has won a game
	 * @param minigame the game that the player has won
	 * @param playerName the name of the player who won the game
	 * @param gamesWon the amount of games the player has won
	 * @deprecated use {@link #setGamesWon(String, UUID, int)}
	 */
	public void setGamesWon(String minigame, String playerName, int gamesWon) {
		UUID uuid = Bukkit.getOfflinePlayer(playerName).getUniqueId();
		set(minigame, uuid, playerName, "games won", "int", gamesWon);
		set(minigame, uuid, playerName, "games won_total", "int", (int) get(uuid, "games won_total", "int", 0) + gamesWon);
	}
	
	/**
	 * Gets the amount of times a player has lost a minigame
	 * @param minigame the minigame the player has lost (@see {@link #getTotalGamesLost(String)} for getting a player's total losses)
	 * @param playerName the name of the player to get the loss count of
	 * @return how many times the player has lost the minigame given
	 * @deprecated use {@link #getGamesLost(String, UUID)}
	 */
	public int getGamesLost(String minigame, String playerName) {
		return (int) get(minigame, Bukkit.getOfflinePlayer(playerName).getUniqueId(), "games lost", "int", 0);
	}
	
	/**
	 * Sets how many times a player has lost a game
	 * @param minigame the minigame the player has lost
	 * @param playerName the name of the player to set the games lost of
	 * @param gamesLost the amount of times the player has lost the game
	 * @deprecated use {@link #setGamesLost(String, UUID, int)}
	 */
	public void setGamesLost(String minigame, String playerName, int gamesLost) {
		UUID uuid = Bukkit.getOfflinePlayer(playerName).getUniqueId();
		set(minigame, uuid, playerName, "games lost", "int", gamesLost);
		set(minigame, uuid, playerName, "games lost_total", "int", (int) get(uuid, "games lost_total", "int", 0) + gamesLost);
	}
	
	/**
	 * Gets the total amount of experience points a player has
	 * @param playerName the name of the player to get the experience points of
	 * @return the total experience points for all games combined of a player
	 * @deprecated use {@link #getTotalEXP(UUID)}
	 */
	public double getTotalEXP(String playerName) {
		return (double) get(Bukkit.getOfflinePlayer(playerName).getUniqueId(), "exp_total", "double", 0);
	}
	
	/**
	 * Sets the total amount of experience points a player has
	 * @param playerName the name of the player to set the total experience points of
	 * @param exp the amount of experience points the player has
	 * @deprecated use {@link #setTotalEXP(UUID, double)}
	 */
	public void setTotalEXP(String playerName, double exp) {
		set(Bukkit.getOfflinePlayer(playerName).getUniqueId(), playerName, "exp_total", "double", exp);
	}
	
	/**
	 * Gets the total amount of times a player has won a game
	 * @param playerName the name of the player to get the total win amount of
	 * @return the total amount of times the player has won a game
	 * @deprecated use {@link #getTotalGamesWon(UUID)}
	 */
	public int getTotalGamesWon(String playerName) {
		return (int) get(Bukkit.getOfflinePlayer(playerName).getUniqueId(), "games won_total", "double", 0);
	}
	
	/**
	 * Sets the total amount of times a player has won a game
	 * @param playerName the name of the player to set the total wins amount of
	 * @param gamesWon the total amount of times the player has won a game
	 * @deprecated use {@link #setTotalGamesWon(UUID, double)}
	 */
	public void setTotalGamesWon(String playerName, int gamesWon) {
		set(Bukkit.getOfflinePlayer(playerName).getUniqueId(), playerName, "games won_total", "int", gamesWon);
	}
	
	/**
	 * Gets the total amount of times a player has lost a game.
	 * @param playerName the name of the player to get the total loss amount of
	 * @return the total amount of times the player has lost a game
	 * @deprecated use {@link #getTotalGamesLost(UUID)}
	 */
	public int getTotalGamesLost(String playerName) {
		return (int) get(Bukkit.getOfflinePlayer(playerName).getUniqueId(), "games lost_total", "int", 0);
	}
	
	/**
	 * Sets the total amount of times a player has lost a game
	 * @param playerName the name of the player to set the total losses amount of
	 * @param gamesLost the total amount of times the player has lost a game
	 * @deprecated use {@link #setTotalGamesLost(UUID, double)}
	 */
	public void setTotalGamesLost(String playerName, int gamesLost) {
		set(Bukkit.getOfflinePlayer(playerName).getUniqueId(), playerName, "games lost_total", "int", gamesLost);
	}
	
	/**
	 * Gets a player's experience points
	 * @param minigame the minigame to get the points from (@see {@link #getTotalEXP(UUID)} for getting a player's total exp)
	 * @param uuid the unique id of the player to get the exp of
	 * @return the player's experience points
	 */
	public double getEXP(String minigame, UUID uuid) {
		return (double) get(minigame, uuid, "exp", "double", 0);
	}
	
	/**
	 * Sets the amount of experience points a player has
	 * @param minigame the minigame that the player has the experience points in (@see {@link #setTotalEXP(UUID, double)} to set the player's total exp)
	 * @param uuid the unique id of the player to set the experience points of
	 * @param exp the experience points the player will now have
	 */
	public void setEXP(String minigame, UUID uuid, double exp) {
		String name = Bukkit.getOfflinePlayer(uuid).getName();
		set(minigame, uuid, name, "exp", "double", exp);
		set(uuid, name, "exp_total", "double", (double) get(uuid, "exp_total", "double", 0) + exp);
	}
	
	/**
	 * Gets the amount of times a player has won a minigame
	 * @param minigame the minigame the player has won (@see {@link #getTotalGamesWon(UUID)} for getting a player's total wins)
	 * @param uuid the unique id of the player to get the win count of
	 * @return how many times the player has won the minigame given
	 */
	public int getGamesWon(String minigame, UUID uuid) {
		return (int) get(minigame, uuid, "games won", "int", 0);
	}
	
	/**
	 * Sets how many times a player has won a game
	 * @param minigame the game that the player has won
	 * @param uuid the unique id of the player who won the game
	 * @param gamesWon the amount of games the player has won
	 */
	public void setGamesWon(String minigame, UUID uuid, int gamesWon) {
		String name = Bukkit.getOfflinePlayer(uuid).getName();
		set(minigame, uuid, name, "games won", "int", gamesWon);
		set(minigame, uuid, name, "games won_total", "int", (int) get(uuid, "games won_total", "int", 0) + gamesWon);
	}
	
	/**
	 * Gets the amount of times a player has lost a minigame
	 * @param minigame the minigame the player has lost (@see {@link #getTotalGamesLost(UUID)} for getting a player's total wins)
	 * @param uuid the unique id of the player to get the loss count of
	 * @return how many times the player has lost the minigame given
	 */
	public int getGamesLost(String minigame, UUID uuid) {
		return (int) get(minigame, uuid, "games lost", "int", 0);
	}
	
	/**
	 * Sets how many times a player has lost a game
	 * @param minigame the minigame the player has lost
	 * @param uuid the unique id of the player to set the games lost of
	 * @param gamesLost the amount of times the player has lost the game
	 */
	public void setGamesLost(String minigame, UUID uuid, int gamesLost) {
		String name = Bukkit.getOfflinePlayer(uuid).getName();
		set(minigame, uuid, name, "games lost", "int", gamesLost);
		set(minigame, uuid, name, "games lost_total", "int", (int) get(uuid, "games lost_total", "int", 0) + gamesLost);
	}
	
	/**
	 * Gets the total amount of experience points a player has
	 * @param uuid the unique id of the player to get the experience points of
	 * @return the total experience points for all games combined of a player
	 */
	public double getTotalEXP(UUID uuid) {
		return (double) get(uuid, "exp_total", "double", 0);
	}
	
	/**
	 * Sets the total amount of experience points a player has
	 * @param uuid the unique id of the player to set the experience points of
	 * @param exp the amount of experience points the player has
	 */
	public void setTotalEXP(UUID uuid, double exp) {
		set(uuid, Bukkit.getOfflinePlayer(uuid).getName(), "exp_total", "double", exp);
	}
	
	/**
	 * Gets the total amount of times a player has won a game
	 * @param uuid the unique id of the player to get the total win amount of
	 * @return the total amount of times the player has won a game
	 */
	public int getTotalGamesWon(UUID uuid) {
		return (int) get(uuid, "games won_total", "int", 0);
	}
	
	/**
	 * Sets the total amount of times a player has lost a game
	 * @param uuid the unique id of the player to get the total win amount of
	 * @param gamesWon the total amount of times the player has lost a game
	 */
	public void setTotalGamesWon(UUID uuid, int gamesWon) {
		set(uuid, Bukkit.getOfflinePlayer(uuid).getName(), "games won_total", "int", gamesWon);
	}
	
	/**
	 * Gets the total amount of times a player has lost a game
	 * @param uuid the unique id of the player to get the total loss amount of
	 * @return the total amount of times the player has lost a game
	 */
	public int getTotalGamesLost(UUID uuid) {
		return (int) get(uuid, "games lost_total", "int", 0);
	}
	
	/**
	 * Sets the total amount of times a player has lost a game
	 * @param uuid the unique id of the player to get the total win amount of
	 * @param gamesLost the total amount of times the player has lost a game
	 */
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