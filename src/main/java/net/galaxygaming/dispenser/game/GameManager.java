/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.dispenser.game;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.Validate;
import org.bukkit.plugin.InvalidDescriptionException;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.galaxygaming.dispenser.GameDispenser;
import net.galaxygaming.dispenser.entity.GamePlayer;
import net.galaxygaming.dispenser.game.java.JavaGameLoader;

/**
 * @author t7seven7t
 */
public class GameManager {
    
    private final GameDispenser plugin;
    private final GameLoader gameLoader;
    private final File directory;
    private final Set<Game> games;
    private final Set<GameType> loadedGameTypes;
    private final Map<String, GamePlayer> lookupGamePlayers;
    
    public GameManager(GameDispenser plugin, File directory) {
        Validate.notNull(directory, "Directory cannot be null");
        Validate.isTrue(directory.isDirectory(), "Directory must be a directory");
        
        this.plugin = plugin;
        this.gameLoader = new JavaGameLoader();
        this.directory = directory;
        this.games = Sets.newHashSet();
        this.loadedGameTypes = Sets.newHashSet();
        this.lookupGamePlayers = Maps.newHashMap();
    }
    
    public GameType[] loadGameTypes() {
        Map<GameDescriptionFile, File> jars = Maps.newHashMap();
        Pattern filter = Pattern.compile("\\.jar$");
        
        for (File file : directory.listFiles()) {
            Matcher match = filter.matcher(file.getName());
            if (!match.find()) {
                continue;
            }
            
            GameDescriptionFile description = null;
            try {
                description = gameLoader.getGameDescription(file);
            } catch (InvalidDescriptionException e) {
                plugin.getLogger().log(Level.WARNING, 
                        "Could not load '" + file.getPath() 
                        + "' in folder '" + directory.getPath() 
                        + "'", e
                ); 
                continue;
            }
            
            jars.put(description, file);
        }
        
        Set<String> games = Sets.newHashSet();
        for (GameDescriptionFile description : jars.keySet()) {
            games.add(description.getName());
        }
        
        while(!jars.isEmpty()) {
            Iterator<Entry<GameDescriptionFile, File>> it = jars.entrySet().iterator();
            while (it.hasNext()) {
                Entry<GameDescriptionFile, File> entry = it.next();
                
                boolean dependLoaded = true;
                for (String game : entry.getKey().getDepend()) {
                    if (!loadedGameTypes.contains(game)) {
                        if (!games.contains(game)) {
                            it.remove();
                            plugin.getLogger().log(Level.WARNING,
                                    "Dependency '" + game + "' for game '" 
                                    + entry.getKey().getName() + "' does not exist"
                            );
                        }

                        dependLoaded = false;
                        break;
                    }
                }
                
                if (!dependLoaded) {
                    continue;
                }
                
                GameType type = new GameType(entry.getKey().getName());
                
                try {
                    gameLoader.loadGameType(entry.getValue(), entry.getKey(), true);
                    gameLoader.loadEvents(type);
                } catch (InvalidGameException e) {
                    plugin.getLogger().log(Level.WARNING, 
                            "Could not load '" + entry.getValue().getPath() 
                            + "' in folder '" + directory.getPath() 
                            + "'", e
                    );
                }
                
                it.remove();
                loadedGameTypes.add(type);
            }
        }
        
        return loadedGameTypes.toArray(new GameType[0]);
    }

}
