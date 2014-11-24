package net.galaxygaming.dispenser.game;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.galaxygaming.dispenser.GameDispenser;
import net.galaxygaming.dispenser.game.component.Component;
import net.galaxygaming.dispenser.game.component.ComponentManager;
import net.galaxygaming.dispenser.game.component.SetComponentException;
import net.galaxygaming.dispenser.kit.Kit;
import net.galaxygaming.dispenser.task.GameRunnable;
import net.galaxygaming.util.FormatUtil;

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
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public abstract class GameBase implements Game {

    /** The current game state */
    protected GameState state = GameState.INACTIVE;
    
    /** Name of this game instance */
    private String name;
    
    /** List of players in this game */
    private List<Player> players;
    
    /** Set of component for this game */
    private Map<String, Field> components;
        
    /** 
     * The maximum number of players the game can have. 
     * A value of 0 will be interpreted as no maximum
     */
    @Component(name = "maximum players")
    protected int maximumPlayers = 0;
    
    /** The minimum number of players before the game can start */
    @Component(name = "minimum players")
    protected int minimumPlayers = 2;
    
    /** The length of the countdown period in seconds */
    @Component(name = "countdown duration")
    protected int countdownDuration = 30;
    
    /** The length of the game in seconds, if -1 the game will never end */
    @Component(name = "game time")
    protected int gameTime = -1;
    
    /** The option to use a built-in scoreboard */
    @Component(name = "use scoreboard players")
    protected boolean useScoreboardPlayers = false;
    @Component(name = "use scoreboard time")
    protected boolean useScoreboardTime = false;
    
    /** Spawn player back at spawn or last location */
    @Component(name = "spawn at last location")
    protected boolean useLastLocation = false;
    
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
    @Component(name = "grace duration")
    protected int graceDuration = 5;
    
    private GameType type;
    private GameLoader loader;
    private FileConfiguration config;
    private File configFile;
    private ClassLoader classLoader;
    private Logger logger;
    private GameDispenser plugin;
    Plugin fakePlugin;
    
    @Component(ignoreSetup = true)
    private List<Location> signs;
    
    private int counter;
    private int tick;
    
    private ArrayList<Kit> kits = Lists.newArrayList();
    
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
    
    public final List<Location> getSigns() {
        return Collections.unmodifiableList(signs);
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
    
    @Override
    public final Set<String> getComponents() {
        return components.keySet();
    }
    
    @Override
    public final String[] getComponentInfo(String componentName) {
        Field f = components.get(componentName);
        if (f == null) {
            return new String[] {componentName, "is not a", "component"};
        }
        
        String[] info = ComponentManager.getComponentInfo(this, f);
        return new String[] {componentName, info[0], info.length > 1 ? info[1] : ""};
    }
    
    @Override
    public final Object[] getComponentsInfo() {
        Set<String> resultComponents = Sets.newTreeSet(String.CASE_INSENSITIVE_ORDER);
        int setupComponents = 0;
        
        for (String componentName : getComponents()) {
            if (!components.get(componentName).getAnnotation(Component.class).ignoreSetup()) {
                resultComponents.add(componentName);
                if (ComponentManager.isSetup(this, components.get(componentName))) {
                    setupComponents++;
                }
            }
        }        
        
        String[][] result = new String[resultComponents.size()][3];
        Iterator<String> it = resultComponents.iterator();
        for (int i = 0; i < resultComponents.size(); i++) {
            result[i] = getComponentInfo(it.next());
        }
        
        return new Object[] {new int[] {setupComponents, resultComponents.size()}, result};
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
        for (Entry<String, Field> entry : components.entrySet()) {
            ComponentManager.saveComponent(this, entry.getValue(), entry.getKey());
        }
        
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
				if (counter % 60 == 0 || (counter < 60 && counter % 30 == 0) || (counter <= 5 && counter > 0)) {
					if (getState().ordinal() == GameState.STARTING.ordinal())
						broadcast(type.getMessages().getMessage("game.countdown.start"), counter);
					else if (getState().ordinal() > GameState.GRACE.ordinal())
						broadcast(type.getMessages().getMessage("game.countdown.end"), counter);
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
        GameManager.getGameManager().addPlayerToGame(player, this);
        
        if (players.size() >= minimumPlayers) {
            startCountdown();
        }
        
        broadcast(type.getMessages().getMessage("game.broadcastPlayerJoin"), player.getName(), players.size(), maximumPlayers > 0 ? maximumPlayers : "\u221e");
        
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
        if (broadcast)
            broadcast(type.getMessages().getMessage("game.broadcastPlayerLeave"), player.getName(), players.size(), maximumPlayers > 0 ? maximumPlayers : "\u221e");

        GameManager.getGameManager().removePlayerFromGame(player);
        players.remove(player);
        
        if (!player.isDead()) {
            if (useLastLocation && getMetadata(player, "gameLastLocation") != null) {
                player.teleport((Location) getMetadata(player, "gameLastLocation").value());
            } else {
                player.teleport(player.getWorld().getSpawnLocation());
            }
        }
        
        removeMetadata(player, "gameLastLocation");
        
        onPlayerLeave(player);
        
        updateSigns();
        updateScoreboard();
    }
    
    @Override
    public final Player[] getPlayers() {
        return players.toArray(new Player[players.size()]);
    }
    
    @Override
    public boolean isSetup() {
        for (Field f : components.values()) {
            if (!ComponentManager.isSetup(this, f)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean isSetup(String componentName) {
        Field f = components.get(componentName);
        return f == null ? false : ComponentManager.isSetup(this, f);
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
    public void setComponent(String componentName, Player player, String args) throws SetComponentException {
        for (String name : components.keySet()) {
            if (name.equalsIgnoreCase(componentName)) {
                ComponentManager.setComponent(this, components.get(name), player, componentName, args);
                return;
            }
        }
        
        throw new SetComponentException(this, "component.notmatched", this.getName(), componentName);
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
            if (this.name.equalsIgnoreCase(((GameBase) o).name))
                return true;
        } else if (o.getClass() == String.class) {
            if (this.name.equalsIgnoreCase((String) o))
                return true;
        }
        return false;
    }
    
    @Override
    public final int hashCode() {
        return name.toLowerCase().hashCode();
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
        this.components = Maps.newHashMap();
        this.lastPlayerCount = getPlayers().length;
        this.signs = Lists.newArrayList();

        board = Bukkit.getScoreboardManager().getNewScoreboard();
        objective = board.registerNewObjective(ChatColor.translateAlternateColorCodes('&', "&6&l" + getType().toString()), "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        
        onLoad();
        
        updateScoreboard();
        
        /*
         * Find all fields which are components
         */
        Class<?> current = this.getClass();
        while(current.getSuperclass() != null) {
            for (Field field : current.getDeclaredFields()) {
                Component c = field.getAnnotation(Component.class);
                if (c != null) {
                    components.put(c.name().isEmpty() ? field.getName() : c.name().replaceAll(" ", "_"), field);
                }
            }
            
            current = current.getSuperclass();
        }
        
        // TODO: add regen selection interval stuff if any components are regen selection
        
        /*
         * Attempt to load components in this next loop
         */
        for (Entry<String, Field> entry : components.entrySet()) {            
            ComponentManager.loadComponent(this, entry.getValue(), entry.getKey());
        }
        
        save();
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
    
    @Override
    public ArrayList<Kit> getKits() {
    		return kits;
    }
    
    @Override
    public void addKit(Kit kit) {
    		kits.add(kit);
    }
    
    @Override
    public void removeKit(Kit kit) {
    		if (kits.contains(kit))
    			kits.remove(kit);
    }
}