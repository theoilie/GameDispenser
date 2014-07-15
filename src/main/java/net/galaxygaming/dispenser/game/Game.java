/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.dispenser.game;

import java.io.File;

/**
 * @author t7seven7t
 */
public interface Game {
    
    public File getDataFolder();
    
    public GameDescriptionFile getDescription();
            
    public GameLoader getGameLoader();
    
    public GameState getState();
    
    public GameType getType(); 
    
    public GameIdentifier getUniqueIdentifier();
    
    public void onLoad();
    	
    public void onTick();
    
    public void startCountdown(); // After enough players have joined
    
    public void startGame(); // After countdown is over
    
    public void endGame(); // After someone wins
    
    public void returnToLobby(); // After ending state is over and fireworks finish
}