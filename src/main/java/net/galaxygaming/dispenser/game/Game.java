/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.dispenser.game;

import java.util.List;
import java.util.logging.Logger;

import net.galaxygaming.dispenser.GameDispenser;
import net.galaxygaming.selection.Selection;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.metadata.Metadatable;

/**
 * @author t7seven7t
 */
public interface Game {
    
     /**
      * Sends a message to every player in the game
      * @param message
      */
    public void broadcast(String message, Object... objects);
    
    /**
     * Retrieves a config file unique to this game instance.
     * @return config
     */
    public FileConfiguration getConfig();
    
    /**
     * Saves this game's configurations to its config file
     */
    public void saveConfig();
    
    public GameLoader getGameLoader();
    
    /**
     * Gives a logger unique to this game instance
     * @return logger
     */
    public Logger getLogger();
    
    /**
     * Retrieves the metadata for a metadatable object using key
     * and this game instance to ensure correct selection
     * @param object metadatable object
     * @param key metadata key
     * @return game metadata
     */
    public GameMetadata getMetadata(Metadatable object, String key);
    
    /**
     * Removes metadata from an object
     * @param object object to remove metadata for
     * @param key key to metadata
     */
    public void removeMetadata(Metadatable object, String key);
    
    /**
     * Gives the singleton instance of the GameDispenser plugin
     * @return GameDispenser plugin
     */
    public GameDispenser getPlugin();
    
    /**
     * Gets the current GameState of this game
     * @return GameState
     */
    public GameState getState();
    
    /**
     * Sets the current GameState of this game
     * @param state
     */
    public void setState(GameState state);
    
    /**
     * Gets the GameType of this game
     * @return GameType
     */
    public GameType getType(); 
    
    /**
     * Gives the name of this game instance
     * @return name
     */
    public String getName();
    
    /**
     * Sets the name of this game instance
     * @param name
     */
    public void setName(String name);
    
    /**
     * Adds a player to this game
     * @param player
     * @return false if cannot add player
     */
    public boolean addPlayer(Player player);
    
    /**
     * Removes a player from the game
     * @param player
     */
    public void removePlayer(Player player);
    
    /**
     * Returns a list of all players in this game
     * @return
     */
    public Player[] getPlayers();

    /**
     * Do stuff when this game is first initialized
     */
    public void onLoad();
    
    /**
     * Do stuff when game starts
     */
    public void onStart();
    
    /**
     * Do stuff when game ends
     */
    public void onEnd();
    
    /**
     * Do stuff every minecraft game tick when
     * the game state ordinal is greater than
     * GameState.STARTING ordinal
     */
    public void onTick();
    
    /**
     * Do stuff whenever a player joins
     * @param player
     */
    public void onPlayerJoin(Player player);
    
    /**
     * Do stuff whenever a player leaves
     * @param player
     */
    public void onPlayerLeave(Player player);
    
    /**
     * Sets a component in this game with a {@link Location}
     * @param componentName
     * @param location
     */
    public boolean setComponent(String componentName, Location location);
    
    /**
     * Sets a component in this game with a {@link Selection}
     * @param componentName
     * @param selection
     */
    public boolean setComponent(String componentName, Selection selection);
    
    /**
     * Sets a component in this game with a String array
     * @param componentName
     * @param args
     */
    public boolean setComponent(String componentName, String[] args);
    
    /**
     * Gives a list of components that have been registered for this game
     * @return list of component names
     */
    public List<String> getComponents();
    
    /**
     * Begins the countdown process before this game starts
     */
    public void startCountdown();
    
    /**
     * Start the game
     */
    public void start(); // After countdown is over
    
    /**
     * Ticks the game, do not call this method.
     */
    public void tick();
    
    /**
     * End the game,
     * removes all players from the game
     */
    public void end(); // After someone wins
    
    /**
     * Override this method to check for ending conditions
     * Every tick the game will check this method and end
     * if it returns true. Alternatively just call 
     * {@link #end()} yourself when ready.
     * @return
     */
    public boolean isFinished();
    
    /**
     * @return true if the game is setup correctly
     */
    public boolean isSetup();
}