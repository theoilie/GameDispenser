/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.dispenser.game.java;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;

import com.google.common.collect.Lists;

import net.galaxygaming.dispenser.GameDispenser;
import net.galaxygaming.dispenser.game.Game;
import net.galaxygaming.dispenser.game.GameLoader;
import net.galaxygaming.dispenser.game.GameLogger;
import net.galaxygaming.dispenser.game.GameManager;
import net.galaxygaming.dispenser.game.GameState;
import net.galaxygaming.dispenser.game.GameType;
import net.galaxygaming.dispenser.task.GameRunnable;
import net.galaxygaming.metadata.GameMetadata;
import net.galaxygaming.util.FormatUtil;

/**
 * @author t7seven7t
 */
public abstract class JavaGame implements Game {

    /** The current game state */
    protected GameState state = GameState.INACTIVE;
    
    /** Name of this game instance */
    private String name;
    
    /** List of players in this game */
    private List<Player> players;
    
    /** Field used privately for counting ticks */
    private int counter;
    
    /** 
     * The maximum number of players the game can have. 
     * A value of 0 will be interpreted as the maximum
     * that a list is capable of holding.
     */
    protected int maximumPlayers;
    
    /** The minimum number of players before the game can start */
    protected int minimumPlayers;
    
    /** The length of the countdown period */
    protected int countdownDuration;
    
    private GameType type;
    private GameLoader loader;
    private FileConfiguration config;
    private File configFile;
    private ClassLoader classLoader;
    private Logger logger;
    private GameDispenser plugin;
    
    public void broadcast(String message, Object... objects) {
        String formatted = FormatUtil.format(message, objects);
        for (Player player : players) {
            player.sendMessage(formatted);
        }
    }
    
    protected final ClassLoader getClassLoader() {
        return classLoader;
    }
    
    @Override
    public final FileConfiguration getConfig() {
        return this.config;
    }
    
    @Override
    public final void saveConfig() {
        try {
            this.config.save(configFile);
        } catch (IOException e) {
            getLogger().log(Level.WARNING, "Error ocurred while saving config", e);
        }
    }
    
    /**
     * Gives the file associated with this game's config
     * @return config file
     */
    protected final File getConfigFile() {
        return this.configFile;
    }
    
    @Override
    public final GameLoader getGameLoader() {
        return this.loader;
    }
    
    @Override
    public final Logger getLogger() {
        return this.logger;
    }
    
    @Override
    public final GameMetadata getMetadata(Metadatable object, String key) {
        List<MetadataValue> listMetadata = object.getMetadata(key);
        for (MetadataValue v : listMetadata) {
            if (v instanceof GameMetadata) {
                GameMetadata data = (GameMetadata) v;
                if (data.getGame() == this) {
                    return data;
                }
            }
        }
        return null;
    }
    
    @Override
    public final GameDispenser getPlugin() {
        return this.plugin;
    }
    
    @Override
    public final GameState getState() {
        return this.state;
    }
    
    @Override
    public final void setState(GameState state) {
        this.state = state;
    }
    
    @Override
    public final GameType getType() {
        return this.type;
    }
    
    @Override
    public final String getName() {
        return this.name;
    }
    
    @Override
    public final void setName(String name) {
        this.name = name;
    }
    
    @Override
    public final void startCountdown() {
        if (getState() == GameState.STARTING) {
            return;
        }
        
        setState(GameState.STARTING);
        new GameRunnable() {
            int countdown = countdownDuration;
            @Override
            public void run() {
                if (countdown % 5 == 0 || countdown < 5) {
                    broadcast("&6Game starts in {0}", countdown);
                }
                
                countdown--;
                
                if (countdown <= 0) {
                    new GameRunnable() {
                        @Override
                        public void run() {
                            start();
                        }
                    }.runTaskLater(20L);
                    this.cancel();
                }
            }
        }.runTaskTimer(0L, 20L);
    }
    
    @Override
    public final void start() {
        setState(GameState.ACTIVE);
        onStart();
    }
    
    @Override
    public final void tick() {
        onTick();
    }
    
    @Override
    public final void end() {
        onEnd();
        setState(GameState.INACTIVE);
        
        Iterator<Player> it = players.iterator();
        while (it.hasNext()) {
            Player player = it.next();
            GameManager.getInstance().removePlayerFromGame(player);
            it.remove();
        }
    }
    
    @Override
    public final boolean addPlayer(Player player) {
        return addPlayer(player, false);
    }
    
    @Override
    public final boolean addPlayer(Player player, boolean bypassRestrictions) {
        if (getState().ordinal() < GameState.INACTIVE.ordinal() 
                && getState().ordinal() >= GameState.ACTIVE.ordinal()
                && !bypassRestrictions) {
            return false;
        }
        
        if (players.size() >= maximumPlayers && maximumPlayers > 0 && !bypassRestrictions) {
            return false;
        }
        
        setState(GameState.LOBBY);
        
        players.add(player);
        GameManager.getInstance().addPlayerToGame(player, this);
        
        if (players.size() >= minimumPlayers) {
            startCountdown();
        }
        
        onPlayerJoin(player);
        
        return true;
    }
    
    @Override
    public final void removePlayer(Player player) {
        GameManager.getInstance().removePlayerFromGame(player);
        players.remove(player);
    }
    
    @Override
    public final List<Player> getPlayers() {
        return players;
    }
    
    /* Override the following methods and let
     * devs choose whether to use them
     */
    @Override
    public void onLoad() {}
    
    @Override
    public void onStart() {}
    
    @Override
    public void onEnd() {}
    
    @Override
    public void onPlayerJoin(Player player) {}
    
    @Override
    public void onPlayerLeave(Player player) {}
    
    @Override
    public final boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        
        if (getClass() != o.getClass()) {
            return false;
        }
        
        if (getClass() == o.getClass()) {            
            if (this.name.equalsIgnoreCase(((JavaGame) o).name)) {
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
    public final int hashCode() {
        return name.hashCode();
    }
    
    public final InputStream getResource(String fileName) {
        Validate.notNull(fileName, "Filename cannot be null");
        
        try {
            URL url = getClassLoader().getResource(fileName);
            
            if (url == null) {
                return null;
            }
            
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException e) {
            return null;
        }
    }
    
    public JavaGame() {
        final ClassLoader classLoader = this.getClass().getClassLoader();
        if (!(classLoader instanceof GameClassLoader)) {
            throw new IllegalStateException("JavaGame requires " + GameClassLoader.class.getName());
        }
    }
    
    final void initialize(String name, FileConfiguration config, GameLoader loader, File file, ClassLoader classLoader) {
        this.name = name;
        this.config = config;
        this.loader = loader;
        this.configFile = file;
        this.classLoader = classLoader;
        this.logger = new GameLogger(this, GameDispenser.getInstance());
        this.players = Lists.newArrayList();
        this.plugin = GameDispenser.getInstance();
        
        this.minimumPlayers = getConfig().getInt("minimumPlayers", 2);
        this.maximumPlayers = getConfig().getInt("maximumPlayers", 0);
        this.countdownDuration = getConfig().getInt("countdownDuration", 30);
        
        onLoad();
    }
}
