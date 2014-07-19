/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.dispenser.game;

import java.io.File;
import java.io.IOException;
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
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
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
    
    /** Singleton instance */
    private static final GameManager instance = new GameManager();
    
    private final GameLoader gameLoader;
    private final Set<Game> games;
    private final Set<GameType> loadedGameTypes;
    private final Map<String, GamePlayer> lookupGamePlayers;
    
    private GameDispenser plugin;
    private File directory;
    
    private GameManager() {
        this.games = Sets.newHashSet();
        this.loadedGameTypes = Sets.newHashSet();
        this.lookupGamePlayers = Maps.newHashMap();
        this.gameLoader = new JavaGameLoader();
    }
    
    public static GameManager getInstance() {
        return instance;
    }
    
    public void setup(GameDispenser plugin, File directory) {
        Validate.notNull(directory, "Directory cannot be null");
        Validate.isTrue(directory.isDirectory(), "Directory must be a directory");
        
        this.plugin = plugin;
        this.directory = directory;
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
        Validate.notNull(type, "Game type cannot be null");
        
        if (games.contains(name)) {
            throw new InvalidGameException("A game with the name '" + name + "' already exists.");
        }
        
        File configFile = new File(directory, name + GAME_CONFIG_EXTENSION);
        try {
            configFile.createNewFile();
        } catch (IOException e) {
            throw new InvalidGameException("Unable to create new config for " + name, e);
        }
        
        FileConfiguration config = getConfig(configFile);
        config.set(GameConfiguration.PATH_GAMETYPE, type.toString());
        
        Game result = gameLoader.loadGame(configFile, config);
        games.add(result);
        saveGame(result);       
        return result;
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
    
    public void saveGames() {
        for (Game game : games) {
            saveGame(game);
        }
    }
    
    public void saveGame(Game game) {
        game.saveConfig();
    }
    
    public Game getGame(String name) {
        // Iterators are fastest for small sets but longest for large sets o.o
        Iterator<Game> it = games.iterator();
        while (it.hasNext()) {
            Game game = it.next();
            if (game.getName().equalsIgnoreCase(name)) {
                return game;
            }
        }
        
        return null;
    }
    
    /**
     * Sets are very slow to iterate 
     * over compared to arrays.
     * @return array of game elements
     */
    public Game[] getGames() {
        return games.toArray(new Game[0]);
    }
    
    /**
     * Sets are very slow to iterate 
     * over compared to arrays.
     * @return array of game types
     */
    public GameType[] getGameTypes() {
        return loadedGameTypes.toArray(new GameType[0]);
    }
    
    public void launchFireworks(Game game) {
		// TODO
    }
    
    public void announceWinner(String winner) {
    	// TODO: Broadcast victory message from file
    }
    
    public FileConfiguration getConfig(File configFile) throws InvalidGameException {
        FileConfiguration config = new GameConfiguration();
        try {
            config.load(configFile);
        } catch(IOException e) {
            throw new InvalidGameException("Cannot load " + configFile, e);
        } catch (InvalidConfigurationException e) {
            throw new InvalidGameException("Cannot load " + configFile, e);
        }
        return config;
    }
}