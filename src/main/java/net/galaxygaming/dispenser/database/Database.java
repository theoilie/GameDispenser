package net.galaxygaming.dispenser.database;

import java.util.UUID;

import net.galaxygaming.dispenser.GameDispenser;
import net.galaxygaming.dispenser.game.Game;

import org.bukkit.entity.Player;

public abstract class Database {
	private static Database currentDatabase;
	
	/**
	 * Gets an object from the database based on the key. <br>
	 * Example: get("lactem", "survivalgames", "donor") could return
	 * whether or not lactem is a donor in Survival Games.
	 * @param key they key of what to get from the database
	 * @param args any arguments of what to get such as which player,
	 * which game, or other parameters specific to the key
	 * @return the value associated with the key from the database
	 */
	public abstract Object get(String key, String... args);
	
	/**
	 * Sets 'key' in the database. <br>
	 * Example: set("lactem", "developer") could set the key
	 * 'lactem' to 'developer' in whatever current database
	 * is being used.
	 * @param key the key to set
	 * @param args any arguments of what to set such as which player,
	 * which game, what to set key to, or any other parameters specific to the key
	 */
	public abstract void set(String key, String... args);
	
	/**
	 * Gets object from database based on key and player object. <br>
	 * Example: get("points", player) could get the amount
	 * of points the player has.
	 * @param key they key of what to get from the database
	 * @param player player associated with the key
	 * @return the value associated with key and player
	 */
	public Object get(String key, Player player) {
		return get(key, player.getUniqueId());
	}
	
	/**
	 * Gets object from database based on key and player uuid. <br>
	 * Example: get("points", uuid) could get the amount
	 * of points the uuid of a player has.
	 * @param key key of what to get from the database
	 * @param uuid the uuid of the player associated with the key
	 * @return the value associated with key and player
	 */
	public Object get(String key, UUID uuid) {
		return get(key, uuid.toString());
	}
	
	/**
	 * Gets object from database based on key and game. <br>
	 * Example: get("games played", survivalgames) could
	 * get how many times the game Survival Games has been played.
	 * @param key the key of what to get from the database
	 * @param game the game associated with the key
	 * @return the value associated with the key and game
	 */
	public Object get(String key, Game game) {
		return get(key, game.getName());
	}
	
	/**
	 * Gets object from database based on key, game, and player. <br>
	 * Example: get("games played", survivalgames, player) could
	 * get how many times the player lactem has player Survival Games.
	 * @param key key of what to get from the database
	 * @param game the game associated with the key
	 * @param player the player associated with the game and key
	 * @return the value associated with the key, game, and player
	 */
	public Object get(String key, Game game, Player player) {
		return get(key, game, player.getUniqueId());
	}
	
	/**
	 * Gets object from database based on key, game, and player uuid. <br>
	 * Example: get("games played", survivalgames, uuid) could
	 * get how many times the player lactem has player Survival Games.
	 * @param key key of what to get from the database
	 * @param game the game associated with the key
	 * @param uuid the uuid of the player associated with the game and key
	 * @return the value associated with the key, game, and player
	 */
	public Object get(String key, Game game, UUID uuid) {
		return get(key, game.getName(), uuid.toString());
	}
	
	/**
	 * Gets object from database based on key, game, and player. <br>
	 * Example: get("games played", survivalgames, uuid) could
	 * get how many times the player lactem has player Survival Games.
	 * @param key key of what to get from the database
	 * @param game the name of the game associated with the key
	 * @param player the uuid of the player associated with the game and key
	 * @return the value associated with the key, game, and player
	 */
	public Object get(String key, String game, Player player) {
		return get(key, game, player.getUniqueId());
	}
	
	/**
	 * Gets object from database based on key, game, and player uuid. <br>
	 * Example: get("games played", survivalgames, uuid) could
	 * get how many times the player lactem has player Survival Games.
	 * @param key key of what to get from the database
	 * @param game the name of the game associated with the key
	 * @param uuid the uuid of the player associated with the game and key
	 * @return the value associated with the key, game, and player
	 */
	public Object get(String key, String game, UUID uuid) {
		return get(key, game, uuid.toString());
	}
	
	/**
	 * Sets the value of key to value for the player in the database. <br>
	 * Example: set("points", player, "10") could set the amount
	 * of points for a player to 10.
	 * @param key the key to be set
	 * @param player the player associated with the key and value
	 * @param value the value to set key to
	 */
	public void set(String key, Player player, String value) {
		set(key, player.getUniqueId().toString(), value);
	}
	
	/**
	 * Sets the value of key to value for the player in the database. <br>
	 * Example: set("points", uuid, "10") could set the amount
	 * of points for a player to 10.
	 * @param key the key to be set
	 * @param uuid the uuid of the player associated with the key and value
	 * @param value the value to set key to
	 */
	public void set(String key, UUID uuid, String value) {
		set(key, uuid.toString(), value);
	}
	
	/**
	 * Sets the value of key to value for a game in the database. <br>
	 * Example: set("games played", game, "10") could set the amount
	 * of times a game has been played to 10.
	 * @param key the key to be set
	 * @param game the game associated with the key and value
	 * @param value the value to set key to
	 */
	public void set(String key, Game game, String value) {
		set(key, game.getName(), value);
	}
	
	/**
	 * Sets the value of key to value player in a specific game in the database. <br>
	 * Example: set("wins", game, player, "10") could set the amount
	 * of times a player has won in a game to 10.
	 * @param key the key to be set
	 * @param game the game associated with the key and value
	 * @param player the player associated with the key, game, and value
	 * @param value the value to set key to
	 */
	public void set(String key, Game game, Player player, String value) {
		set(key, game.getName(), player.getUniqueId(), value);
	}
	
	/**
	 * Sets the value of key to value player in a specific game in the database. <br>
	 * Example: set("wins", game, uuid, "10") could set the amount
	 * of times a player has won in a game to 10.
	 * @param key the key to be set
	 * @param game the game associated with the key and value
	 * @param uuid the uuid of the player associated with the key, game, and value
	 * @param value the value to set key to
	 */
	public void set(String key, Game game, UUID uuid, String value) {
		set(key, game.getName(), uuid.toString(), value);
	}
	
	/**
	 * Sets the value of key to value player in a specific game in the database. <br>
	 * Example: set("wins", "survivalgames", player, "10") could set the amount
	 * of times a player has won Survival Games to 10.
	 * @param key the key to be set
	 * @param game the name of the game associated with the key and value
	 * @param player the player associated with the key, game, and value
	 * @param value the value to set key to
	 */
	public void set(String key, String game, Player player, String value) {
		set(key, game, player.getUniqueId(), value);
	}
	
	/**
	 * Sets the value of key to value player in a specific game in the database. <br>
	 * Example: set("wins", "survivalgames", uuid, "10") could set the amount
	 * of times a player has won Survival Games to 10.
	 * @param key the key to be set
	 * @param game the name of the game associated with the key and value
	 * @param uuid the uuid of the player associated with the key, game, and value
	 * @param value the value to set key to
	 */
	public void set(String key, String game, UUID uuid, String value) {
		set(key, game, uuid.toString(), value);
	}
	
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
		default:
			if (currentDatabase == null)
				currentDatabase = new YAML();
		}
		return currentDatabase;
	}
}