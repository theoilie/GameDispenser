package net.galaxygaming.dispenser.game.component;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.entity.Player;

import net.galaxygaming.dispenser.game.Game;

import com.google.common.collect.Sets;

/**
 * 
 */
public class ComponentManager {

    private static final Set<ComponentType> registeredTypes = Sets.newHashSet();
    
    static void register(ComponentType componentType) {
        registeredTypes.add(componentType);
    }
    
    public static void loadComponent(Game game, Field field, String path) {
        try {
            field.setAccessible(true);
            
            if (!game.getConfig().contains(path)) {
                return;
            }
            
            if (Modifier.isFinal(field.getModifiers())) {
                return;
            }
            
            for (ComponentType componentType : registeredTypes) {
                for (Class<?> clazz : componentType.types) {
                    if (clazz.isAssignableFrom(field.getType())) {
                        field.set(game, componentType.deserialize(game, path, field));
                        return;
                    }
                }
                
                if (componentType instanceof Array1DComponent && field.getType().isArray()) {
                    field.set(game, componentType.deserialize(game, path, field));
                    return;
                }
            }
        } catch (Exception e) {
            game.getLogger().log(Level.WARNING, "Error while loading component " + field.getName(), e);
        }
    }
    
    public static void saveComponent(Game game, Field field, String path) {
        try {
            field.setAccessible(true);
                        
            for (ComponentType componentType : registeredTypes) {                
                for (Class<?> clazz : componentType.types) {
                    if (clazz.isAssignableFrom(field.getType())) {
                        componentType.serialize(game, path, field);
                        return;
                    }
                }
                
                if (componentType instanceof Array1DComponent && field.getType().isArray()) {
                    componentType.serialize(game, path, field);
                    return;
                }
            }
        } catch (Exception e) {
            game.getLogger().log(Level.WARNING, "Error while saving component " + field.getName(), e);
        }
    }
    
    public static Object componentFromSetup(Game game, String name, Class<?> clazz, Player player, String args) throws SetComponentException {
        for (ComponentType componentType : registeredTypes) {
            for (Class<?> c : componentType.types) {
                if (c.isAssignableFrom(clazz)) {
                    return componentType.componentFromSetup(game, name, clazz, player, args);
                }
            }
        }
        
        // A method for getting this object isn't defined.
        throw new SetComponentException();
    }
    
    public static void setComponent(Game game, Field field, Player player, String name, String args) throws SetComponentException {
        if (name.equalsIgnoreCase("description")) {
            game.getConfig().set("description", args);
        }
        
        field.setAccessible(true);
        
        if (Modifier.isFinal(field.getModifiers())) {
            return;
        }
        
        try {
            for (ComponentType componentType : registeredTypes) {
                for (Class<?> clazz : componentType.types) {
                    if (clazz.isAssignableFrom(field.getType())) {
                        field.set(game, componentType.getComponent(game, player, name, args, field));
                        return;
                    }
                }
                
                if (componentType instanceof Array1DComponent && field.getType().isArray()) {
                    field.set(game, componentType.getComponent(game, player, name, args, field));
                    return;
                }
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            // Neither of these should ever occur...
            game.getLogger().log(Level.WARNING, "Error while changing component value: " + field.getName(), e);
        }
    }
    
    public static boolean isSetup(Game game, Field field) {
        try {
            field.setAccessible(true);
            
            if (field.isAnnotationPresent(Component.class)) {
                Component component = field.getAnnotation(Component.class);
                if (component.ignoreSetup()) {
                    return true;
                }
            }
            
            if (Modifier.isFinal(field.getModifiers())) {
                return true;
            }
                        
            for (ComponentType componentType : registeredTypes) {
                for (Class<?> clazz : componentType.types) {
                    if (clazz.isAssignableFrom(field.getType())) {
                        return componentType.isSetup(game, field);
                    }
                }
                
                if (componentType instanceof Array1DComponent && field.getType().isArray()) {
                    return componentType.isSetup(game, field);
                }
            }
        } catch (Exception e) {
            game.getLogger().log(Level.WARNING, "Error while checking if component is setup: " + field.getName(), e);
        }
        
        return true;
    }
    
    public static Class<?> getParameterizedTypeVariable(Class<?> clazz) {
        // Class has no type information
        if (clazz.getTypeParameters().length == 0) {
            return null;
        }
        
        return (Class<?>) clazz.getTypeParameters()[0].getBounds()[0];
    }
    
    public static Class<?> getParameterizedType(Type type) {
        // Class has no parameter info, can't do anything more for it
        if (!(type instanceof ParameterizedType)) {
            return null;
        }
        
        // Get the first parameter type for the type
        return (Class<?>) ((ParameterizedType) type).getActualTypeArguments()[0];
    }
    
    static {
        new ListComponent();
        new LocationComponent();
        new PrimitiveComponent();
        new RegenComponent();
        new Array1DComponent();
    }
}