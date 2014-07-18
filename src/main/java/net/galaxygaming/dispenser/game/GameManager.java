/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.dispenser.game;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.Validate;
import org.bukkit.plugin.InvalidDescriptionException;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.galaxygaming.dispenser.GameDispenser;
import net.galaxygaming.dispenser.entity.GamePlayer;
import net.galaxygaming.dispenser.game.java.JavaGameLoader;
import net.galaxygaming.util.FormatUtil;

/**
 * @author t7seven7t
 */
public class GameManager {
    
    public static final String GAME_CONFIG_EXTENSION = ".game";
    
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
                
                final File file = entry.getValue();
                final GameDescriptionFile description = entry.getKey();

                boolean dependLoaded = true;
                for (String game : description.getDepend()) {
                    if (!loadedGameTypes.contains(game)) {
                        if (!games.contains(game)) {
                            it.remove();
                            plugin.getLogger().log(Level.WARNING,
                                    "Dependency '" + game + "' for game '" 
                                    + description.getName() + "' does not exist"
                            );
                        }

                        dependLoaded = false;
                        break;
                    }
                }
                
                if (!dependLoaded) {
                    continue;
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
                    
                    gameLoader.loadGameType(file, description, true);
                    GameType type = new GameType(description.getName(), description, dataFolder);
                    gameLoader.loadEvents(type);
                    loadedGameTypes.add(type);
                } catch (InvalidGameException e) {
                    plugin.getLogger().log(Level.WARNING, 
                            "Could not load '" + entry.getValue().getPath() 
                            + "' in folder '" + directory.getPath() 
                            + "'", e
                    );
                }
                
                it.remove();
            }
        }
        
        return loadedGameTypes.toArray(new GameType[0]);
    }
    
    public Game[] loadGames() {
        Pattern filter = Pattern.compile("\\" + GAME_CONFIG_EXTENSION + "$");
        
        for (File file : directory.listFiles()) {
            Matcher match = filter.matcher(file.getName());
            if (!match.find()) {
                continue;
            }
            
            try {
                games.add(gameLoader.loadGame(file));
            } catch (InvalidGameException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not load '" + file.getPath() + "' in folder '" + directory.getPath() + "'", e);
            }
        }
        
        return games.toArray(new Game[0]);
    }
    
    /**
     * Returns a new game instance of the type given.
     * @param type type of game
     * @param name name of the instance
     * @return a new game instance
     * @throws InvalidGameException
     */
    public Game newGame(GameType type, String name) throws InvalidGameException {
        return null;
    }
    
    /**
     * Returns a new game instance of the type given. A name
     * will automatically be assigned to this instance.
     * @param type type of game
     * @return a new game instance
     * @throws InvalidGameException
     */
    public Game newGame(GameType type) throws InvalidGameException {
        List<Integer> ids = Lists.newArrayList();
        Pattern filter = Pattern.compile("^" + type.toString());

        for (Game game : games) {
            Matcher match = filter.matcher(game.getName());
            if (!match.find()) {
                continue;
            }
            
            String suffix = game.getName().replaceAll(filter.pattern(), "");
            
            try {
                int id = Integer.valueOf(suffix);
                ids.add(id);
            } catch (NumberFormatException e) {
                // expected
            }
        }
        
        Collections.sort(ids);
        int result = 0;
        
        for (int id : ids) {
            if (id == result) {
                result = id + 1;
            }
        }
                
        return newGame(type, type.toString() + result);
    }
    
    public Game[] saveGames(File directory) {
        Validate.notNull(directory, "Directory cannot be null");
        return null;
    }
    
    void saveGame(File file, Game game) {
        
    }
    
    Game getGame(String name) {
        return null;
    }
    
    Game[] getGames() {
        return null;
    }
    
    GameType[] getGameTypes() {
        return null;
    }
    
    public void launchFireworks(Game game) {
		// TODO
    }
    
    public void announceWinner(String winner) {
    	// TODO: Broadcast victory message from file
    }
}