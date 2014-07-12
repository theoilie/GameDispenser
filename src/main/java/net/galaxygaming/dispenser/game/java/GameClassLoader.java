/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.dispenser.game.java;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.Validate;

import net.galaxygaming.dispenser.game.Game;
import net.galaxygaming.dispenser.game.GameDescriptionFile;
import net.galaxygaming.dispenser.game.InvalidGameException;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

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
    
    /**
     * Gives a collection of classes that inherit the super class specified
     * @param superClass super class to inherit
     * @return set of classes inheriting superClass
     * @throws InvalidGameException
     */
    <T> Set<T> loadEventClasses(Class<T> superClass) throws InvalidGameException {
        Set<T> result = Sets.newHashSet();
        
        JarFile jar = null;
        Pattern filter = Pattern.compile("\\.class$");
        
        try {
            jar = new JarFile(file);
            for (Enumeration<JarEntry> e = jar.entries(); e.hasMoreElements(); ) {
                JarEntry entry = e.nextElement();
                Matcher match = filter.matcher(entry.getName());
                if (!match.find()) {
                    continue;
                }
                
                String binaryName = entry.getName().replaceAll("/", ".").replace(".class", "");
                Class<?> clazz = Class.forName(binaryName, false, this);
                if (superClass.isAssignableFrom(clazz)) {
                    Class<? extends T> event = clazz.asSubclass(superClass);
                    result.add(event.newInstance());
                }
            }
        } catch (Throwable e) {
            throw new InvalidGameException(e);
        } finally {
            if (jar != null) {
                try {
                    jar.close();
                } catch (IOException e) {}
            }
        }
        
        return result;
    }
    
}
