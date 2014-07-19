/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.dispenser.game;

import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * @author t7seven7t
 */
public interface Game {
    
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
    
    public Logger getLogger();
    
    public GameState getState();
    
    public GameType getType(); 
    
    public String getName();
    
    public void setName(String name);
    
    public void onLoad();
    	
    public void onTick();
    
    public void startCountdown(); // After enough players have joined
    
    public void startGame(); // After countdown is over
    
    public void endGame(); // After someone wins
    
    public void returnToLobby(); // After ending state is over and fireworks finish
}