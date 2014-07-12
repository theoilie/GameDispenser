/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.dispenser.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.galaxygaming.dispenser.GameDispenser;
import net.galaxygaming.dispenser.game.GameType;

import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author t7seven7t
 */
public class EventManager {

    private static EventManager instance;
    private static final Map<GameType, Set<Listener>> listeners = Maps.newHashMap();

    private final GameDispenser plugin;
    
    public EventManager(GameDispenser plugin) {
        EventManager.instance = this;
        this.plugin = plugin;
    }
    
    public static void registerListener(Listener listener, GameType type) {
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
            instance.plugin.getLogger().severe(type.toString() + " has failed to register events for " + listener.getClass() + " because " + e.getMessage() + " does not exist.");
            return;
        }
        
        for (final Method method : methods) {
            final EventHandler eh = method.getAnnotation(EventHandler.class);
            if (eh == null) continue;
            final Class<?> checkClass;
            if (method.getParameterTypes().length != 1 || !Event.class.isAssignableFrom(checkClass = method.getParameterTypes()[0])) {
                instance.plugin.getLogger().severe(type.toString() + " attempted to register an invalid EventHandler method signature '" + method.toGenericString() + "' in " + listener.getClass());
                continue;
            }
            
            final Class<? extends Event> eventClass = checkClass.asSubclass(Event.class);
            method.setAccessible(true);
            
            EventExecutor executor = new EventExecutor() {
                public void execute(Listener listener, Event event) throws EventException {
                    try {
                        if (!eventClass.isAssignableFrom(event.getClass())) {
                            return;
                        }
                        method.invoke(listener, event);
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
            instance.plugin.getServer().getPluginManager().registerEvent(eventClass, listener, eh.priority(), executor, instance.plugin, eh.ignoreCancelled());
        }          
    }
    
    public static void unregisterListener(Listener listener) {
        HandlerList.unregisterAll(listener);
        for (Entry<GameType, Set<Listener>> entry : listeners.entrySet()) {
            if (entry.getValue() != null) {
                entry.getValue().remove(listener);
            }
        }
    }
    
    public static void unregisterListeners(GameType type) {
        Set<Listener> listenerSet = listeners.get(type);
        if (listenerSet != null) {
            for (Listener listener : listenerSet) {
                HandlerList.unregisterAll(listener);
            }
        }
        listeners.remove(type);
    }
    
}
