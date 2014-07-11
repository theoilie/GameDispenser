/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.dispenser.game.java;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.Listener;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.UnknownDependencyException;
import org.yaml.snakeyaml.error.YAMLException;

import com.google.common.collect.Maps;

import net.galaxygaming.dispenser.GameDispenser;
import net.galaxygaming.dispenser.command.Command;
import net.galaxygaming.dispenser.command.CommandManager;
import net.galaxygaming.dispenser.event.EventListener;
import net.galaxygaming.dispenser.event.EventsManager;
import net.galaxygaming.dispenser.game.Game;
import net.galaxygaming.dispenser.game.GameDescriptionFile;
import net.galaxygaming.dispenser.game.GameLoader;
import net.galaxygaming.dispenser.game.GameType;
import net.galaxygaming.dispenser.game.InvalidGameException;
import net.galaxygaming.util.FormatUtil;

/**
 * @author t7seven7t
 */
public class JavaGameLoader implements GameLoader {

    private final Map<String, Class<?>> classes;
    private final Map<String, GameClassLoader> loaders;
    
    public JavaGameLoader() {
        this.classes = Maps.newHashMap();
        this.loaders = Maps.newHashMap();
    }
    
    public Game loadGame(GameType type) throws InvalidGameException {
        GameClassLoader loader = loaders.get(type.toString());
        Validate.notNull(loader, "Game type '" + type.toString() + "' must be loaded first");
        
        return loader.newInstance();
    }
    
    public void loadGameType(File file, GameDescriptionFile description) throws InvalidGameException {
        loadGameType(file, description, false);
    }
    
    public void loadGameType(File file, GameDescriptionFile description, boolean reload) throws InvalidGameException {
        Validate.notNull(file, "File cannot be null");
        Validate.notNull(description, "Description cannot be null");
        if (!reload) {
            Validate.isTrue(!loaders.containsKey(description.getName()), "Game type already loaded");
        } else {
            loaders.remove(description.getName());
        }
        
        if (!file.exists()) {
            throw new InvalidGameException(new FileNotFoundException(file.getPath() + " does not exist"));
        }
        
        for (String game : description.getDepend()) {
            if (loaders == null) {
                throw new UnknownDependencyException(game);
            }
            
            GameClassLoader current = loaders.get(game);
            if (current == null) {
                throw new UnknownDependencyException(game);
            }
        }
        
        final File parentFile = file.getParentFile();
        final File dataFolder = new File(parentFile, description.getName());
        
        if (dataFolder.exists() && !dataFolder.isDirectory()) {
            throw new InvalidGameException(FormatUtil.format(
                    "Projected datafolder: '{0}' for {1} ({2}) exists and is not a directory",
                    dataFolder,
                    description.getFullName(),
                    file
            ));
        }
                
        try {            
            loaders.put(description.getName(), new GameClassLoader(this, file, dataFolder, getClass().getClassLoader(), description));
        } catch (Throwable e) {
            throw new InvalidGameException(e);
        }
    }
    
    public void loadEvents(GameType type) {
        GameClassLoader loader = loaders.get(type.toString());
        Validate.notNull(loader, "Game type '" + type.toString() + "' must be loaded first");
        
        for (String name : loader.getClasses()) {
            try {
                Listener listener = loader.loadInstance(name, EventListener.class);
                if (listener != null) {
                    EventsManager.addListener(listener);
                    continue;
                }
                
                Command command = loader.loadInstance(name, Command.class);
                if (command != null) {
                    CommandManager.addCommand(command);
                }
            } catch (InvalidGameException e) {
                GameDispenser.getInstance().getLogger().log(Level.WARNING,
                        "Event failed to initialize.", e);
            }
        }
    }
    
    public GameDescriptionFile getGameDescription(File file) throws InvalidDescriptionException {
        Validate.notNull(file, "File cannot be null");
        
        JarFile jar = null;
        InputStream stream = null;
        
        try {
            jar = new JarFile(file);
            JarEntry entry = jar.getJarEntry("game.yml");
            
            if (entry == null) {
                throw new InvalidDescriptionException(new FileNotFoundException("Jar does not contain game.yml"));
            }
            
            stream = jar.getInputStream(entry);
            
            return new GameDescriptionFile(stream);
        } catch (IOException e) {
            throw new InvalidDescriptionException(e);
        } catch (YAMLException e) {
            throw new InvalidDescriptionException(e);
        } finally {
            if (jar != null) {
                try {
                    jar.close();
                } catch (IOException e) {}
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {}
            }
        }
    }
    
    Class<?> getClassByName(final String name) {
        Class<?> cachedClass = classes.get(name);
        
        if (cachedClass != null) {
            return cachedClass;
        } else {
            for (String current : loaders.keySet()) {
                GameClassLoader loader = loaders.get(current);
                
                try {
                    cachedClass = loader.findClass(name, false);
                } catch (ClassNotFoundException e) {}
                
                if (cachedClass != null) {
                    return cachedClass;
                }
            }
        }
        
        return null;
    }
    
    void setClass(final String name, final Class<?> clazz) {
        if (!classes.containsKey(name)) {
            classes.put(name, clazz);
            
            if (ConfigurationSerializable.class.isAssignableFrom(clazz)) {
                Class<? extends ConfigurationSerializable> serializable = clazz.asSubclass(ConfigurationSerializable.class);
                ConfigurationSerialization.registerClass(serializable);
            }
        }
    }
    
}
