/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.dispenser.game;

import java.io.File;


import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.InvalidDescriptionException;

/**
 * @author t7seven7t
 */
public interface GameLoader {

    public GameDescriptionFile getGameDescription(File file) throws InvalidDescriptionException;
    
    public void loadEvents(GameType type);
    
    /**
     * Gives a game loaded from the specified config
     * @param configFile 
     * @param config
     * @return game instance
     * @throws InvalidGameException
     */
    public Game loadGame(File configFile, FileConfiguration config) throws InvalidGameException;
    
    /**
     * Alternate method for loading a game provided only the file
     * if the config is not yet loaded
     * @param configFile
     * @return game instance
     * @throws InvalidGameException
     */
    public Game loadGame(File configFile) throws InvalidGameException;
    
    public void loadGameType(File file, GameDescriptionFile description, boolean reload) throws InvalidGameException;
    
    public void loadGameType(File file, GameDescriptionFile description) throws InvalidGameException;
    
}