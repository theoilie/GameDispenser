package net.galaxygaming.dispenser.game;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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
import net.galaxygaming.dispenser.event.EventManager;
import net.galaxygaming.util.FormatUtil;

class GameLoader {

    private final Map<String, Class<?>> classes;
    private final Map<String, GameClassLoader> loaders;
    
    public GameLoader() {
        this.classes = Maps.newHashMap();
        this.loaders = Maps.newHashMap();
    }
    
    public Game loadGame(File configFile) throws InvalidGameException {
        return loadGame(configFile, null);
    }
    
    public Game loadGame(File configFile, GameType type) throws InvalidGameException {
        return loadGame(configFile, YamlConfiguration.loadConfiguration(configFile), type);
    }
    
    public Game loadGame(File configFile, FileConfiguration config, GameType type) throws InvalidGameException {
        Validate.notNull(configFile, "File cannot be null");
        Validate.notNull(config, "Config cannot be null");
        
        String name = configFile.getName().replaceAll(
                "\\" 
                + GameManager.GAME_CONFIG_EXTENSION 
                + "$", ""
        );
        
        if (type == null) {
            type = GameType.get(config.getString("type"));        
        } else if (type != GameType.get(config.getString("type"))) {
            return null;
        }
        
        GameClassLoader loader = loaders.get(type.toString());
        if (loader == null) {
            throw new InvalidGameException("Game type '" + type.toString() + "' must be loaded first");
        }

        return loader.newInstance(name, config, configFile);
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
            GameType.remove(GameType.get(description.getName()));
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
        
        final File dataFolder = new File(file.getParentFile(), description.getName());
        
        try {
            if (dataFolder.exists() && !dataFolder.isDirectory()) {
                throw new InvalidGameException(FormatUtil.format(
                        "Projected datafolder: '{0}' for {1} ({2}) exists and is not a directory",
                        dataFolder,
                        description.getFullName(),
                        file
                ));
            }
            
            GameClassLoader loader = new GameClassLoader(this, file, getClass().getClassLoader(), description);
            new GameType(description.getName(), description, dataFolder, loader);
            loaders.put(description.getName(), loader);
        } catch (Throwable e) {
            throw new InvalidGameException(e);
        }
    }
    
    public void unloadGameType(GameType type) throws InvalidGameException {
        GameClassLoader loader = loaders.remove(type.toString());
        if (loader != null) {
            loader.unloadJar();
        }
        GameType.remove(type);
    }
    
    public void loadEvents(GameType type) {
        GameClassLoader loader = loaders.get(type.toString());
        Validate.notNull(loader, "Game type '" + type.toString() + "' must be loaded first");
        
        try {            
            for (Listener listener : loader.loadEventClasses(Listener.class)) {
                EventManager.getInstance().registerListener(listener, type);
            }
            
            for (Command command : loader.loadEventClasses(Command.class)) {
                CommandManager.getInstance().registerCommand(command, type);
            }
        } catch (InvalidGameException e) {
            GameDispenser.getInstance().getLogger().log(Level.WARNING,
                    "Failed to register events for " + type.toString(), e);
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