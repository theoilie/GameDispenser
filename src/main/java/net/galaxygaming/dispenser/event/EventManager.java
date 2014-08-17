package net.galaxygaming.dispenser.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.galaxygaming.dispenser.GameDispenser;
import net.galaxygaming.dispenser.game.Game;
import net.galaxygaming.dispenser.game.GameManager;
import net.galaxygaming.dispenser.game.GameType;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.plugin.EventExecutor;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class EventManager {
    
    /** Singleton instance */
    private static final EventManager instance = new EventManager();
    
    private final Map<GameType, Set<Listener>> listeners;
    private GameDispenser plugin;

    private EventManager() {
        listeners = Maps.newHashMap();
    }
    
    /**
     * Sets up the manager
     * @param plugin the GameDispenser singleton instance
     */
    public void setup(GameDispenser plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(new Events(), plugin);
    }
    
    /**
     * Cloning is not supported.
     */
    @Override
    public EventManager clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
    
    /**
     * Registers a listener for a GameType
     * @param listener the listener to be registered
     * @param type the type of game to register the listener for
     */
    public void registerListener(Listener listener, GameType type) {
        Set<Method> methods;
        try {
            Method[] publicMethods = listener.getClass().getMethods();
            methods = new HashSet<Method>(publicMethods.length, Float.MAX_VALUE);
            for (Method method : publicMethods) {
                methods.add(method);
            }
            
            for (Method method : listener.getClass().getDeclaredMethods()) {
                methods.add(method);
            }
        } catch (NoClassDefFoundError e) {
            plugin.getLogger().severe(type.toString() + " has failed to register events for " + listener.getClass() + " because " + e.getMessage() + " does not exist.");
            return;
        }
        
        for (final Method method : methods) {
            final EventHandler eh = method.getAnnotation(EventHandler.class);
            if (eh == null) continue;
            final Class<?> checkClass = method.getParameterTypes()[0];
            final Class<? extends Game> gameClass;
            if (method.getParameterTypes().length == 2 && Game.class.isAssignableFrom(method.getParameterTypes()[1]) 
                    && (EntityEvent.class.isAssignableFrom(checkClass) || PlayerEvent.class.isAssignableFrom(checkClass)
                            || BlockBreakEvent.class.isAssignableFrom(checkClass) || BlockPlaceEvent.class.isAssignableFrom(checkClass)
                            || BlockDamageEvent.class.isAssignableFrom(checkClass) || SignChangeEvent.class.isAssignableFrom(checkClass))) {
                gameClass = method.getParameterTypes()[1].asSubclass(Game.class);
            } else if (method.getParameterTypes().length != 1 || !Event.class.isAssignableFrom(checkClass)) {
                plugin.getLogger().severe(type.toString() + " attempted to register an invalid EventHandler method signature '" + method.toGenericString() + "' in " + listener.getClass());
                continue;
            } else {
                gameClass = null;
            }

            final Class<? extends Event> eventClass = checkClass.asSubclass(Event.class);
            method.setAccessible(true);
            
            EventExecutor executor = new EventExecutor() {
                public void execute(Listener listener, Event event) throws EventException {
                    try {
                        if (!eventClass.isAssignableFrom(event.getClass())) {
                            return;
                        }
                        
                        if (gameClass != null) {
                            Player player = null;
                            if (event instanceof PlayerEvent) {
                                player = ((PlayerEvent) event).getPlayer();
                            } else if (event instanceof EntityEvent) {
                                Entity entity = ((EntityEvent) event).getEntity();
                                if (entity instanceof Player) {
                                    player = (Player) entity;
                                }
                            } else if (event instanceof BlockBreakEvent) {
                                player = ((BlockBreakEvent) event).getPlayer();
                            } else if (event instanceof BlockPlaceEvent) {
                                player = ((BlockPlaceEvent) event).getPlayer();
                            } else if (event instanceof BlockDamageEvent) {
                                player = ((BlockDamageEvent) event).getPlayer();
                            } else if (event instanceof SignChangeEvent) {
                                player = ((SignChangeEvent) event).getPlayer();
                            }
                            
                            if (player == null) {
                                return;
                            }
                            
                            Game game = GameManager.getInstance().getGameForPlayer(player, gameClass);
                            if (game == null)
                                return;
                            
                            method.invoke(listener, event, gameClass.cast(game));
                        } else {
                            method.invoke(listener, event);
                        }
                    } catch (InvocationTargetException e) {
                        throw new EventException(e.getCause());
                    } catch (Throwable e) {
                        throw new EventException(e);
                    }
                }
            };
            
            Set<Listener> listenerSet = listeners.get(type);
            if (listenerSet == null) {
                listenerSet = Sets.newHashSet();
                listeners.put(type, listenerSet);
            }
            
            listenerSet.add(listener);
            plugin.getServer().getPluginManager().registerEvent(eventClass, listener, eh.priority(), executor, plugin, eh.ignoreCancelled());
        }          
    }
    
    /**
     * Unregisters a listener
     * @param listener the listener to unregister
     */
    public void unregisterListener(Listener listener) {
        HandlerList.unregisterAll(listener);
        for (Entry<GameType, Set<Listener>> entry : listeners.entrySet()) {
            if (entry.getValue() != null) {
                entry.getValue().remove(listener);
            }
        }
    }
    
    /**
     * Unregisters listeners for a GameType
     * @param type the type of game to unregister listeners for
     */
    public void unregisterListeners(GameType type) {
        Set<Listener> listenerSet = listeners.get(type);
        if (listenerSet != null) {
            for (Listener listener : listenerSet) {
                HandlerList.unregisterAll(listener);
            }
        }
        listeners.remove(type);
    }
    
    /**
     * Gets the singleton instance of this clas
     * @return singleton instance of EventManager
     */
    public static EventManager getInstance() {
        return instance;
    }   
}