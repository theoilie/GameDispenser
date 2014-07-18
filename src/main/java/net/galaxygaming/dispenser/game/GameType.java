/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.dispenser.game;

import java.io.File;
import java.util.Map;
import com.google.common.collect.Maps;

/**
 * @author t7seven7t
 */
public class GameType {
    private static final Map<String, GameType> lookup = Maps.newHashMap();
    
    private final String name;
    private final GameDescriptionFile description;
    private final File dataFolder;
    GameType(String name, GameDescriptionFile description, File dataFolder) {
        if (lookup.containsKey(name)) {
            throw new IllegalStateException("A GameType with the name '" + name + "' already exists.");
        }
        
        this.name = name;
        this.description = description;
        this.dataFolder = dataFolder;
        lookup.put(name, this);
    }
    
    /**
     * Retrieves the description file defining this game type
     * @return game description file
     */
    public GameDescriptionFile getDescription() {
        return this.description;
    }
    
    /**
     * Gives a folder for data from this game type. Not guaranteed to exist.
     * @return game type folder
     */
    public File getDataFolder() {
        return this.dataFolder;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        
        if (getClass() == o.getClass()) {            
            if (this.name.equalsIgnoreCase(((GameType) o).name)) {
                return true;
            }
        } else if (o.getClass() == String.class) {
            if (this.name.equalsIgnoreCase((String) o)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }
    
    public static GameType get(String name) {
        GameType result = lookup.get(name);
        
        if (result == null) {
            throw new IllegalStateException("No such GameType exists '" + name + "'");
        }
        
        return result;
    }
    
    static void remove(GameType type) {
        lookup.remove(type.toString());
    }
}