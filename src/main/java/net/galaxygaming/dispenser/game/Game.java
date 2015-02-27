package net.galaxygaming.dispenser.game;

import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Logger;

import net.galaxygaming.dispenser.GameDispenser;
import net.galaxygaming.dispenser.game.component.Component;
import net.galaxygaming.dispenser.game.component.SetComponentException;
import net.galaxygaming.dispenser.kit.Kit;
import net.galaxygaming.selection.Selection;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.metadata.Metadatable;

public interface Game {
    
    /**
     * Adds a sign showing information about this game.
     * @param location the location of the sign
     */
    public void addSign(Location location);
    
    /**
     * Updates all signs belonging to this game
     */
    public void updateSigns();
    
    /**
     * Removes a sign to stop the game from monitoring this location
     * @param location the location of the sign
     */
    public void removeSign(Location location);
    
    /**
     * Gives a set of sign locations for this game
     * @return the signs
     */
    public Set<Location> getSigns();
    
     /**
      * Sends a message to every player in the game
      * @param message the message to broadcast
      */
    public void broadcast(String message, Object... objects);
    
    /**
     * Retrieves a configuration file unique to this game instance
     * @return configuration file
     */
    public FileConfiguration getConfig();
    
    /**
     * Saves this game's configurations to its config file
     */
    public void save();
    
    /**
     * Gets the class that loads this game
     * @return the GameLoader class associated with this Game instance
     */
    public GameLoader getGameLoader();
    
    /**
     * Gives a logger unique to this game instance
     * @return this game's logger
     */
    public Logger getLogger();
    
    /**
     * Retrieves the metadata for a metadatable object using key
     * and this game instance to ensure correct selection
     * @param object a metadatable object
     * @param key metadata key
     * @return game metadata
     */
    public GameMetadata getMetadata(Metadatable object, String key);
    
    /**
     * Removes metadata from an object
     * @param object the object to remove metadata from
     * @param key the key to the metadata
     */
    public void removeMetadata(Metadatable object, String key);
    
    /**
     * Gives the singleton instance of the GameDispenser plugin
     * @return GameDispenser plugin
     */
    public GameDispenser getPlugin();
    
    /**
     * Gets the current GameState of this game (idle, starting, playing, etc...)
     * @return state of this game
     */
    public GameState getState();
    
    /**
     * Sets the current GameState of this game
     * @param state the GameState to set
     */
    public void setState(GameState state);
    
    /**
     * Gets the GameType of this game
     * @return GameType of the game
     */
    public GameType getType(); 
    
    /**
     * Gives the name of this game instance
     * @return name of the game
     */
    public String getName();
    
    /**
     * Sets the name of this game instance
     * @param name the name of this game instance
     */
    public void setName(String name);
    
    /**
     * Adds a player to this game
     * @param player the player to be added
     * @return false if the player can't be added
     */
    public boolean addPlayer(Player player);
    
    /**
     * Removes a player from the game without broadcasting.
     * Same as calling {@link #removePlayer(Player, false)}
     * @param player the player to be removed
     */
    public void removePlayer(Player player);
    
    /**
     * Removes a player with the option to broadcast
     * @param player the player to remove
     * @param broadcast whether to broadcast the player's departure
     */
    public void removePlayer(Player player, boolean broadcast);
    
    /**
     * Returns a list of all players in this game
     * @return players in the game
     */
    public Player[] getPlayers();

    /**
     * Do stuff when save() is called
     */
    public void onSave();
    
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
     * Do stuff every 20 ticks (1 second)
     */
    public void onSecond();
    
    /**
     * Do stuff whenever a player joins
     * @param player the player who joined
     */
    public void onPlayerJoin(Player player);
    
    /**
     * Do stuff whenever a player leaves
     * @param player the player who left
     */
    public void onPlayerLeave(Player player);
    
    /**
     * Sets a component in this game
     * @param componentName
     * @param player
     * @param args as a single string
     * @return
     */
    public void setComponent(String componentName, Player player, String args) throws SetComponentException;
    
    /**
     * Gives a list of components that have been registered for this game
     * @return list of component names
     */
    public Set<String> getComponents();
    
    /**
     * Begins the countdown process before this game starts
     */
    public void startCountdown();
    
    /**
     * Start the game after the countdown ends
     */
    public void start();
    
    /**
     * Ticks the game, do not call this method.
     */
    public void tick();
    
    /**
     * End the game after someone wins,
     * removes all players from the game
     */
    public void end(); // After someone wins
    
    /**
     * Override this method to check for ending conditions.
     * Every tick the game will check this method and end
     * if it returns true. Alternatively just call 
     * {@link #end()} yourself when ready.
     * @return true if the game is finished
     */
    public boolean isFinished();
    
    /**
     * Override this method to determine if the game is setup.
     * @return true if the game is setup correctly
     * @deprecated Don't override this method in new 
     * component system @see {@link Component}
     */
    public boolean isSetup();
    
    /**
     * Gets a games kits, if applicable
     * @return a list of kits available in this game
     */
    public ArrayList<Kit> getKits();
    
    /**
     * Adds a kit to the game
     * @param kit the kit to add
     */
    public void addKit(Kit kit);

    /**
     * Removes a kit from the game
     * @param kit the kit to remove
     */
    public void removeKit(Kit kit);
}