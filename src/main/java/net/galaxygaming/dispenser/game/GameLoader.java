/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.dispenser.game;

import java.io.File;


import org.bukkit.plugin.InvalidDescriptionException;

/**
 * @author t7seven7t
 */
public interface GameLoader {

    public GameDescriptionFile getGameDescription(File file) throws InvalidDescriptionException;
    
    public void loadEvents(GameType type);
    
    public Game loadGame(File file) throws InvalidGameException;
    
    public void loadGameType(File file, GameDescriptionFile description, boolean reload) throws InvalidGameException;
    
    public void loadGameType(File file, GameDescriptionFile description) throws InvalidGameException;
    
}