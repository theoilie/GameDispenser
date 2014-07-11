/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.dispenser.game.java;

import java.io.File;

import net.galaxygaming.dispenser.game.Game;
import net.galaxygaming.dispenser.game.GameDescriptionFile;
import net.galaxygaming.dispenser.game.GameLoader;

/**
 * @author t7seven7t
 */
public abstract class JavaGame implements Game {

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

    final void initialize(GameLoader loader, GameDescriptionFile description, File dataFolder, File file, ClassLoader classLoader) {
        this.loader = loader;
        this.description = description;
        this.dataFolder = dataFolder;
        this.file = file;
        this.classLoader = classLoader;
    }
    
}
