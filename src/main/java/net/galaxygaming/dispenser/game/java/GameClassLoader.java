/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.dispenser.game.java;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.Validate;

import net.galaxygaming.dispenser.game.Game;
import net.galaxygaming.dispenser.game.GameDescriptionFile;
import net.galaxygaming.dispenser.game.InvalidGameException;

import com.google.common.collect.Maps;

/**
 * @author t7seven7t
 */
public class GameClassLoader extends URLClassLoader {

    private final JavaGameLoader loader;
    private final Map<String, Class<?>> classes;
    private final GameDescriptionFile description;
    private final File file;
    private final File dataFolder;
    private final Class<? extends JavaGame> mainClass;
    
    GameClassLoader(final JavaGameLoader loader, final File file, final File dataFolder, final ClassLoader parent, final GameDescriptionFile description) throws MalformedURLException, InvalidGameException {
        super(new URL[] {file.toURI().toURL()}, parent);
        Validate.notNull(loader, "Loader cannot be null");
        
        this.loader = loader;
        this.classes = Maps.newHashMap();
        this.description = description;
        this.file = file;
        this.dataFolder = dataFolder;
        
        Class<?> jarClass;
        try {
            jarClass = Class.forName(description.getMain(), true, this);
        } catch (ClassNotFoundException e) {
            throw new InvalidGameException("Cannot find main class '" + description.getMain() + "'", e);
        }
        
        try {
            mainClass = jarClass.asSubclass(JavaGame.class);
        } catch (ClassCastException e) {
            throw new InvalidGameException("main class '" +description.getMain() + "' does not extend JavaGame", e);
        }

    }
    
    Game newInstance() throws InvalidGameException {
        try {
            JavaGame result = mainClass.newInstance();
            result.initialize(loader, description, dataFolder, file, this);
            return result;
        } catch (Throwable e) {
            throw new InvalidGameException(e);
        }
    }
    
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return findClass(name, true);
    }
    
    Class<?> findClass(String name, boolean checkGlobal) throws ClassNotFoundException {
        Class<?> result = classes.get(name);
        
        if (result == null) {
            if (checkGlobal) {
                result = loader.getClassByName(name);
            }
            
            if (result == null) {
                result = super.findClass(name);
                
                if (result != null) {
                    loader.setClass(name, result);
                }
            }
            
            classes.put(name, result);
        }
        
        return result;
    }
    
    Set<String> getClasses() {
        return classes.keySet();
    }
    
    <T> T loadInstance(String name, Class<T> superClass) throws InvalidGameException {
        try {
            Class<?> clazz = Class.forName(name, true, this);
            if (clazz.isAssignableFrom(superClass)) {
                Class<? extends T> result = clazz.asSubclass(superClass);
                return result.newInstance();
            } 
        } catch (IllegalAccessException e) {
            throw new InvalidGameException("class '" + name + "' has no public constructor", e);
        } catch (ClassNotFoundException e) {
            // That's stupid.
        } catch (InstantiationException e) {
            throw new InvalidGameException(e);
        }  
        return null;
    }
    
}
