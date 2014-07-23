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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.galaxygaming.dispenser.GameDispenser;
import net.galaxygaming.dispenser.game.GameLoader;

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
    private final Map<String, Game> lookupPlayers;
        
    private GameDispenser plugin;
    private File directory;
    
    private GameManager() {
        this.games = Sets.newHashSet();
        this.loadedGameTypes = Sets.newHashSet();
        this.lookupPlayers = Maps.newHashMap();
        this.gameLoader = new GameLoader();
    }
    
    public static GameManager getInstance() {
        return instance;
    }
    
    public void setup(GameDispenser plugin, File directory) {
        Validate.notNull(directory, "Directory cannot be null");
        Validate.isTrue(directory.isDirectory(), "Directory must be a directory");
        
        this.plugin = plugin;
        this.directory = directory;
        
        new BukkitRunnable() {
            @Override
            public void run() {
                Iterator<Game> it = games.iterator();
                while(it.hasNext()) {
                    Game game = it.next();
                    if (game.getState().ordinal() > GameState.STARTING.ordinal()) {
                        game.tick();
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
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

                try {
                    gameLoader.loadGameType(file, description, true);
                    GameType type = GameType.get(description.getName());
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
    
    boolean isDependLoaded(List<String> depend) {
        for (String game : depend) {
            if (!loadedGameTypes.contains(game)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Loads a game type by name
     * @param name name of game type
     * @return game type object
     * @throws InvalidGameException if could not load
     */
    public GameType loadGameType(String name) throws InvalidGameException {
        File file = new File(directory, name + ".jar");
        if (!file.exists()) {
            throw new InvalidGameException("Game jar does not exist");
        }
        
        GameDescriptionFile description = null;
        try {
            description = gameLoader.getGameDescription(file);
        } catch (InvalidDescriptionException e) {
            throw new InvalidGameException(e);
        }
        
        if (!isDependLoaded(description.getDepend())) {
            throw new InvalidGameException("One or more dependencies are not yet loaded");
        }
        
        gameLoader.loadGameType(file, description, true);
        GameType type = GameType.get(description.getName());
        gameLoader.loadEvents(type);
        loadedGameTypes.add(type);
        return type;
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
     * Loads a game by name
     * @param name name of game
     * @return loaded game
     * @throws InvalidGameException if could not load
     */
    public Game loadGame(String name) throws InvalidGameException {
        File file = new File(directory, name + GAME_CONFIG_EXTENSION);
        if (!file.exists()) {
            throw new InvalidGameException("Game config does not exist");
        }
        
        Game game = gameLoader.loadGame(file);
        games.add(game);
        return game;
    }
    
    /**
     * Unloads all classes of a game type from the server
     * @param type type to unload
     * @throws InvalidGameException if cannot unload
     */
    public void unloadGameType(GameType type) throws InvalidGameException {
        for (GameType other : loadedGameTypes) {
            if (other.getDescription().getDepend().contains(type.toString())) {
                throw new InvalidGameException("Cannot unload game type as another game type depends on it: " + other.toString());
            }
        }
        
        Set<Game> removeGames = Sets.newHashSet();
        for (Game game : games) {
            if (game.getType() == type) {
                removeGames.add(game);
            }
        }
        
        for (Game game : removeGames) {
            unloadGame(game);
        }
        
        gameLoader.unloadGameType(type);
        loadedGameTypes.remove(type);
    }
    
    /**
     * Unloads a game from the server
     * @param game game to unload
     */
    public void unloadGame(Game game) {
        saveGame(game);
        
        if (game.getState().ordinal() > GameState.ACTIVE.ordinal()) {
            game.end();
        }
        
        games.remove(game);
    }
    
    /**
     * Unloads all games and game types
     */
    public void unloadAll() {
        Set<Game> removeGames = Sets.newHashSet(games);
        for (Game game : removeGames) {
            unloadGame(game);
        }
        
        Iterator<GameType> it = loadedGameTypes.iterator();
        while (it.hasNext()) {
            gameLoader.unloadGameType(it.next());
            it.remove();
        }
    }
    
    /**
     * Unloads a game and deletes its config file
     * @param game game to delete
     */
    public void deleteGame(Game game) {
        unloadGame(game);
        
        if (game instanceof GameBase) {
            ((GameBase) game).getConfigFile().delete();
        }
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
        
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        config.set("type", type.toString());
        
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
    
    /**
     * Saves all games
     */
    public void saveGames() {
        for (Game game : games) {
            saveGame(game);
        }
    }
    
    /**
     * Saves a game config
     * @param game game to save
     */
    public void saveGame(Game game) {
        game.saveConfig();
    }
    
    /**
     * Retrieves a game matching the name specified otherwise null
     * @param name name of game
     * @return game
     */
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
    
    /**
     * Adds a player to the game lookup listing
     * @param player
     * @param game
     */
    public void addPlayerToGame(Player player, Game game) {
        lookupPlayers.put(player.getName(), game);
    }
    
    /**
     * Removes a player from the lookup listing
     * @param player
     */
    public void removePlayerFromGame(Player player) {
        lookupPlayers.remove(player.getName());
    }
    
    /**
     * Gives the game a player is currently in otherwise null
     * @param player
     * @return game this player is in, or null if none
     */
    public Game getGameForPlayer(Player player) {
        return lookupPlayers.get(player.getName());
    }
    
    /**
     * Gives the game a player is currently in
     * only if the game class matches the class
     * parameter otherwise null
     * @param player
     * @param clazz
     * @return game the player is in, or null if not
     * an instance of clazz
     */
    @SuppressWarnings("unchecked")
    public <T> T getGameForPlayer(Player player, Class<T> clazz) {
        Game result = getGameForPlayer(player);
        if (result != null && result.getClass().isAssignableFrom(clazz)) {
            return (T) result;
        }
        return null;
    }
    
    @Override
    public GameManager clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}