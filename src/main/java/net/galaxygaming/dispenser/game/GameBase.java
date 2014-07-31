package net.galaxygaming.dispenser.game;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.galaxygaming.dispenser.GameDispenser;
import net.galaxygaming.dispenser.task.GameRunnable;
import net.galaxygaming.selection.Selection;
import net.galaxygaming.util.FormatUtil;
import net.galaxygaming.util.LocationUtil;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import net.galaxygaming.dispenser.game.GameLoader;

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
    
    /** The option to use a built-in scoreboard */
    protected boolean useScoreboardPlayers, useScoreboardTime;
    
    /** The game's scoreboard */
    protected Scoreboard board;
    
    /** The board's objective */
    protected Objective objective;
    
    /** The scores to be set for player. Set to less than 1 to leave out of scoreboard */
    protected int playerTagScore, playerCounterScore = 0;
    
    /** The scores to be set for the time remaining. Set to less than 1 to leave out of scoreboard */
    protected int timeTagScore, timeCounterScore = 0;
    
    /** The last recorded amount of players in the game */
    protected int lastPlayerCount;
    
    /** The last recorded time remaining */
    protected int lastTimeRemaining;
    
    /** The length of the grace period in seconds */
    protected int graceDuration;
    
    private GameType type;
    private GameLoader loader;
    private FileConfiguration config;
    private File configFile;
    private ClassLoader classLoader;
    private Logger logger;
    private GameDispenser plugin;
    Plugin fakePlugin;
    
    private Set<Location> signs;
    
    private int counter;
    private int tick;
    
    public final void addSign(Location location) {
        Validate.notNull(location, "Location cannot be null");
        signs.add(location);
        
        new GameRunnable() {
            @Override
            public void run() {
                updateSigns();
            }
        }.runTask();
    }
    
    public final void removeSign(Location location) {
        signs.remove(location);
    }
    
    public final Set<Location> getSigns() {
        return Collections.unmodifiableSet(signs);
    }
    
    public final void updateSigns() {
        Iterator<Location> it = signs.iterator();
        
        while (it.hasNext()) {
            Location loc = it.next();
            BlockState state = loc.getBlock().getState();
            
            if (!(state instanceof Sign)) {
                it.remove();
                continue;
            }
            
            Sign sign = (Sign) state;
            sign.setLine(0, "[" + getType().toString() + "]");
            sign.setLine(1, getName());
            sign.setLine(2, getState().getFancyName());
            sign.setLine(3, FormatUtil.format("{2}{0}/{1}", getPlayers().length, maximumPlayers > 0 ? maximumPlayers : "\u221e", ChatColor.YELLOW));
            sign.update(false, false);
        }
    }
    
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
    public final void save() {
        List<String> signLocations = Lists.newArrayList();
        for (Location location : signs) {
            signLocations.add(LocationUtil.serializeLocation(location));
        }
        config.set("signs", signLocations);
        
        onSave();
        
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
    public final void startCountdown() {
        if (getState().ordinal() >= GameState.STARTING.ordinal()) {
            return;
        }
        
        setState(GameState.STARTING);
        updateSigns();
        counter = countdownDuration;
    }
    
    @Override
    public final void start() {
        if (!isSetup()) {
            return;
        }
        
        if (graceDuration > 0) {
            counter = graceDuration;
            setState(GameState.GRACE);
        } else {
            counter = gameTime;
            setState(GameState.ACTIVE);
        }
        
        for (Player player : players) {
            player.setMetadata("gameLastLocation", new GameFixedMetadata(this, player.getLocation().clone()));
        }
        
        onStart();
        updateSigns();
    }
    
    @Override
    public final void tick() {
        if (getState().ordinal() > GameState.STARTING.ordinal() && isFinished()) {
            end();
            return;
        }
		if (tick % 20 == 0) {
			onSecond();
			if (counter > 0) {
				updateScoreboard();
				if (counter % 60 == 0 || (counter < 60 && counter % 30 == 0)
						|| (counter <= 5 && counter > 0)) {
					if (getState().ordinal() == GameState.STARTING.ordinal()) {
						broadcast(
								type.getMessages().getMessage(
										"game.countdown.start"), counter);
					} else if (getState().ordinal() > GameState.GRACE.ordinal()) {
						broadcast(
								type.getMessages().getMessage(
										"game.countdown.end"), counter);
					}
				}

				counter--;

				if (counter <= 0) {
					if (getState().ordinal() == GameState.STARTING.ordinal()) {
						start();
					} else if (getState().ordinal() == GameState.GRACE
							.ordinal()) {
						setState(GameState.ACTIVE);
						counter = gameTime;
					} else if (getState().ordinal() > GameState.GRACE.ordinal()) {
						end();
					}
				}

				if (players.size() == 0) {
					end();
					counter = 0;
				}
			}
		}
        
        tick++;
        if (tick >= 20)
            tick = 0;
        
        onTick();
    }
    
    @Override
    public final void end() {
        setState(GameState.INACTIVE);
        onEnd();

        for (Player player : Lists.newArrayList(players.iterator())) {
            removePlayer(player);
        }
        updateSigns();
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
        
        if (players.size() >= minimumPlayers) {
            startCountdown();
        }
        
        broadcast(type.getMessages().getMessage("game.broadcastPlayerJoin"),
                player.getName(), players.size(), 
                maximumPlayers > 0 ? maximumPlayers : "\u221e");
        
        onPlayerJoin(player);
        updateSigns();
        updateScoreboard();
        return true;
    }
    
    @Override
    public final void removePlayer(Player player) {
        removePlayer(player, false);
    }
    
    @Override
    public final void removePlayer(Player player, boolean broadcast) {
        if (broadcast) {
            broadcast(type.getMessages().getMessage("game.broadcastPlayerLeave"),
                    player.getName(), players.size(), 
                    maximumPlayers > 0 ? maximumPlayers : "\u221e");
        }

        GameManager.getInstance().removePlayerFromGame(player);
        players.remove(player);
        if (getMetadata(player, "gameLastLocation") != null) {
            Location loc = (Location) getMetadata(player, "gameLastLocation").value();
            if (!player.isDead()) {
                player.teleport(loc);
                removeMetadata(player, "gameLastLocation");
            }
        }
        updateSigns();
        updateScoreboard();
    }
    
    @Override
    public final Player[] getPlayers() {
        return players.toArray(new Player[players.size()]);
    }
    
    /* Override the following methods and let
     * devs choose whether to use them or not
     */
    @Override
    public void onSave() {}
    
    @Override
    public void onLoad() {}
    
    @Override
    public void onStart() {}
    
    @Override
    public void onTick() {}
    
    @Override
    public void onSecond() {}
    
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
        this.lastPlayerCount = getPlayers().length;
        this.signs = Sets.newHashSet();
        
        getConfig().addDefault("minimum players", 2);
        getConfig().addDefault("maximum players", 0);
        getConfig().addDefault("countdown duration", 30);
        getConfig().addDefault("game time", -1);
        getConfig().addDefault("use scoreboard players", false);
        getConfig().addDefault("use scoreboard time", false);
        getConfig().addDefault("grace duration", 5);
                		
        if (getConfig().isList("signs")) {
            for (String location : getConfig().getStringList("signs")) {
                signs.add(LocationUtil.deserializeLocation(location));
            }
        }

        onLoad();
        
        minimumPlayers = getConfig().getInt("minimum players");
        maximumPlayers = getConfig().getInt("maximum players");
        countdownDuration = getConfig().getInt("countdown duration");
        gameTime = getConfig().getInt("game time");
        useScoreboardPlayers = getConfig().getBoolean("use scoreboard players");
        useScoreboardTime = getConfig().getBoolean("use scoreboard time");
        graceDuration = getConfig().getInt("grace duration");
        
        		board = Bukkit.getScoreboardManager().getNewScoreboard();
        		objective = board.registerNewObjective
        			(ChatColor.translateAlternateColorCodes('&', "&6&l" + getType().toString()), "dummy");
        		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        		updateScoreboard();

        for (String key : getConfig().getDefaults().getKeys(false)) {
            if (getConfig().get(key, null) == null) {
                getConfig().set(key, getConfig().getDefaults().get(key));
            }
        }
    }
    
	protected void updateScoreboard() {
		if (useScoreboardPlayers)
			updatePlayerBoard();
		
		if (useScoreboardTime)
			updateTimeBoard();
	}
    
	/** 
	 * Counts players for scoreboard. Override to count differently. 
	 */
	protected void updatePlayerBoard() {
		if (playerTagScore > 0) {
			Score score = objective.getScore(ChatColor
					.translateAlternateColorCodes('&', "&6&lPlayers"));
			if (score.getScore() != playerTagScore)
				score.setScore(playerTagScore);
		}

		if (playerCounterScore > 0) {
			board.resetScores(lastPlayerCount + "");
			lastPlayerCount = getPlayers().length;
			objective.getScore(lastPlayerCount + "").setScore(
					playerCounterScore);
		}
	}
	
	/**
	 *  Decrements time. Override to change up the countdown.
	 */
	protected void updateTimeBoard() {
		if (this.timeTagScore > 0) {
			Score score = objective.getScore(ChatColor
					.translateAlternateColorCodes('&', "&6&lTime"));
			if (score.getScore() != timeTagScore)
				score.setScore(timeTagScore);
		}
		if (timeCounterScore > 0) {
			board.resetScores(lastTimeRemaining + "");
			lastTimeRemaining = counter;
			objective.getScore(lastTimeRemaining + "").setScore(
					timeCounterScore);
		}
	}
	
	/**
	 * Sets the scoreboard for all players
	 */
    protected void setBoardForAll() {
		for (Player player : getPlayers()) {
			if (player.getScoreboard() != board)
				player.setScoreboard(board);
		}
    }
}