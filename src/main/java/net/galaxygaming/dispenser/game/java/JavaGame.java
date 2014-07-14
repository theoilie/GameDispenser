/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.dispenser.game.java;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.net.URL;

import org.apache.commons.lang.Validate;

import net.galaxygaming.dispenser.game.Game;
import net.galaxygaming.dispenser.game.GameDescriptionFile;
import net.galaxygaming.dispenser.game.GameLoader;
import net.galaxygaming.dispenser.game.GameState;

/**
 * @author t7seven7t
 */
public abstract class JavaGame implements Game {

    /** The current game state */
    protected GameState state = GameState.INACTIVE;
    
    private GameLoader loader;
    private GameDescriptionFile description;
    private File dataFolder;
    private File file;
    private ClassLoader classLoader;
    
    protected final ClassLoader getClassLoader() {
        return classLoader;
    }
    
    @Override
    public final File getDataFolder() {
        return this.dataFolder;
    }
    
    @Override
    public final GameDescriptionFile getDescription() {
        return this.description;
    }
    
    protected final File getFile() {
        return this.file;
    }
    
    @Override
    public final GameLoader getGameLoader() {
        return this.loader;
    }
    
    @Override
    public final GameState getState() {
        return this.state;
    }
    
    public final InputStream getResource(String fileName) {
        Validate.notNull(fileName, "Filename cannot be null");
        
        try {
            URL url = getClassLoader().getResource(fileName);
            
            if (url == null) {
                return null;
            }
            
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException e) {
            return null;
        }
    }
    
    JavaGame() {
        final ClassLoader classLoader = this.getClass().getClassLoader();
        if (!(classLoader instanceof GameClassLoader)) {
            throw new IllegalStateException("JavaGame requires " + GameClassLoader.class.getName());
        }
    }
    
    final void initialize(GameLoader loader, GameDescriptionFile description, File dataFolder, File file, ClassLoader classLoader) {
        this.loader = loader;
        this.description = description;
        this.dataFolder = dataFolder;
        this.file = file;
        this.classLoader = classLoader;
        
        onLoad();
    }
    
}
