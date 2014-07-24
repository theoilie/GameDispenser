/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.dispenser.game;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.galaxygaming.dispenser.GameDispenser;
import net.galaxygaming.dispenser.task.CountdownTask;
import net.galaxygaming.selection.Selection;
import net.galaxygaming.util.FormatUtil;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.google.common.collect.Lists;

/**
 * @author t7seven7t
 */
public abstract class GameBase implements Game {

    /** The current game state */
    protected GameState state = GameState.INACTIVE;
    
    /** Name of this game instance */
    private String name;
    
    /** List of players in this game */
    private List<Player> players;
    
    /** List of component names for this game */
    private List<String> components;
        
    /** 
     * The maximum number of players the game can have. 
     * A value of 0 will be interpreted as the maximum
     * that a list is capable of holding.
     */
    protected int maximumPlayers;
    
    /** The minimum number of players before the game can start */
    protected int minimumPlayers;
    
    /** The length of the countdown period in seconds */
    protected int countdownDuration;
    
    /** The length of the game in seconds, if -1 the game will never end */
    protected int gameTime;
    
    private GameType type;
    private GameLoader loader;
    private FileConfiguration config;
    private File configFile;
    private ClassLoader classLoader;
    private Logger logger;
    private GameDispenser plugin;
    
    private Scoreboard scoreboard;
    private Objective objective;
    
    private boolean sb_enabled;
    
    Plugin fakePlugin;
    
    protected final void addComponent(String componentName) {
        components.add(componentName);
    }
    
    @Override
    public final List<String> getComponents() {
        return Lists.newArrayList(components);
    }
    
    @Override
    public void broadcast(String message, Object... objects) {
        String formatted = FormatUtil.format("&6" + message, objects);
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
    public final Scoreboard getScoreboard() {
    	return this.scoreboard;
    }
    
    @Override
    public final Objective getObjective() {
    	return this.objective;
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
        for (MetadataValue v : object.getMetadata(key)) {
            if (v instanceof GameMetadata) {
                GameMetadata data = (GameMetadata) v;
                if (data.getOwningPlugin() == fakePlugin) {
                    return data;
                }
            }
        }
        return null;
    }
    
    @Override
    public final void removeMetadata(Metadatable object, String key) {
        object.removeMetadata(key, fakePlugin);
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
    public void setScoreboardEnabled(boolean b)
    {
    	this.sb_enabled = b;
    }
    
    @Override
    public final void startCountdown() {
        if (getState().ordinal() >= GameState.STARTING.ordinal()) {
            return;
        }
        
        setState(GameState.STARTING);
        // TODO: countdown task is a poor way to do this, create a new method for this perhaps on tick()
        new CountdownTask(this, countdownDuration,
                type.getMessages().getMessage("game.countdown.start")) {
            @Override
            public void done() {
                start();
            }
        };
    }
    
    @Override
    public final void start() {
        if (!isSetup()) {
            return;
        }
        
        setState(GameState.ACTIVE);
       
        if(this.sb_enabled)
        {
            for(Player p : this.getPlayers())
            {
            	p.setScoreboard(this.getScoreboard());
            }
        }
        
        onStart();
        
        if (gameTime > 0) {
            new CountdownTask(this, gameTime,
                    type.getMessages().getMessage("game.countdown.end")) {
                @Override
                public void done() {
                    end();
                }
            };
        }
    }
    
    @Override
    public final void tick() {
        if (isFinished()) {
            end();
            return;
        }
        
        onTick();
    }
    
    @Override
    public final void end() {
        onEnd();
        setState(GameState.INACTIVE);
        
        for (Player player : Lists.newArrayList(players.iterator())) {
            removePlayer(player);
        }
    }
    
    @Override
    public final boolean addPlayer(Player player) {
        if (players.size() >= maximumPlayers && maximumPlayers > 0) {
            return false;
        }
        
        if (getState().ordinal() < GameState.STARTING.ordinal()) {
            setState(GameState.LOBBY);
        }
        
        players.add(player);        
        GameManager.getInstance().addPlayerToGame(player, this);
        player.setMetadata("gameLastLocation", new GameFixedMetadata(this, player.getLocation().clone()));
        
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
        player.teleport((Location) getMetadata(player, "gameLastLocation").value());
        removeMetadata(player, "gameLastLocation");
    }
    
    @Override
    public final Player[] getPlayers() {
        return players.toArray(new Player[players.size()]);
    }
    
    /* Override the following methods and let
     * devs choose whether to use them
     */
    @Override
    public void onLoad() {}
    
    @Override
    public void onStart() {}
    
    @Override
    public void onTick() {}
    
    @Override
    public void onEnd() {}
    
    @Override
    public void onPlayerJoin(Player player) {}
    
    @Override
    public void onPlayerLeave(Player player) {}
    
    @Override
    public boolean isFinished() {
        return false;
    }
    
    @Override
    public boolean setComponent(String componentName, Location location) {
        return false;
    }
    
    @Override
    public boolean setComponent(String componentName, Selection selection) {
        return false;
    }
    
    @Override
    public boolean setComponent(String componentName, String[] args) {
        return false;
    }
    
    @Override
    public final boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        
        if (getClass() != o.getClass()) {
            return false;
        }
        
        if (getClass() == o.getClass()) {            
            if (this.name.equalsIgnoreCase(((GameBase) o).name)) {
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
    
    public GameBase() {
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
        this.fakePlugin = new FakePlugin();
        this.type = GameType.get(config.getString("type"));
        this.components = Lists.newArrayList();
        
        this.minimumPlayers = getConfig().getInt("minimumPlayers", 2);
        this.maximumPlayers = getConfig().getInt("maximumPlayers", 0);
        this.countdownDuration = getConfig().getInt("countdownDuration", 30);
        this.gameTime = getConfig().getInt("gameTime", -1);
        
        if(getConfig().isBoolean("scoreboardEnabled"))
        {
        	this.sb_enabled = false;
        	getConfig().set("scoreboardEnabled", this.sb_enabled);
        }
        else this.sb_enabled = getConfig().getBoolean("scoreboardEnabled");
        
        if(this.sb_enabled)
        {
    		ScoreboardManager m = Bukkit.getScoreboardManager();
    		
    		this.scoreboard = m.getNewScoreboard();
    		this.objective =  scoreboard.registerNewObjective("gameboard", "dummy");
    		
    		this.objective.setDisplayName(ChatColor.YELLOW + "-=[" + ChatColor.AQUA + name + ChatColor.YELLOW + "]=-");
    		this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }
        
        onLoad();
    }
}
