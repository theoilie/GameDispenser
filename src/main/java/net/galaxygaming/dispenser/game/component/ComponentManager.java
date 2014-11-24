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

public class ComponentManager {

    private static final Set<ComponentType> registeredTypes = Sets.newHashSet();
    
    static void register(ComponentType componentType) {
        registeredTypes.add(componentType);
    }
    
    /**
     * Loads a component from disk
     * @param game game this component belongs to
     * @param field field of the component
     * @param path path in the config for the component to load from
     */
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
    
    /**
     * Saves a component to config/disk using the ComponentType's own methods
     * @param game game the component belongs to
     * @param field field of the component
     * @param path path to save the component to in the config
     */
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
    
    /**
     * Gets the value of the component based on everything the program knows about the user's input
     * <br><br>
     * Note that lists and arrays are not possible inputs from players so this method does not check them.
     * @param game game the component belongs to
     * @param name name of this component
     * @param clazz type of the component
     * @param player player that initiated the set component request
     * @param args any extra input arguments entered into the command
     * @return value of the component that matches type
     * @throws SetComponentException
     */
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
    
    static String getPrintedValue(Class<?> clazz, Object object) {
        for (ComponentType componentType : registeredTypes) {
            for (Class<?> c : componentType.types) {
                if (c.isAssignableFrom(clazz)) {
                    return componentType.getPrintedValue(clazz, object);
                }
            }
        }
        return new PrimitiveComponent().getPrintedValue(Object.class, object);
    }
    
    /**
     * Sets a component's value
     * @param game game the component belongs to
     * @param field field of the component
     * @param player player that initiated the set component request
     * @param name name of this component
     * @param args any extra arguments entered into the command
     * @throws SetComponentException
     */
    public static void setComponent(Game game, Field field, Player player, String name, String args) throws SetComponentException {
        if (name.equalsIgnoreCase("description")) {
            game.getConfig().set("description", args);
        }
        
        field.setAccessible(true);
        
        if (Modifier.isFinal(field.getModifiers())) {
            return;
        }
        
        try {
            ComponentType componentType = getComponentType(game, field);
            
            if (componentType == null) {
                throw new SetComponentException("Component " + name + " for game " + game.getName() + " is unsupported.");
            }
            
            field.set(game, componentType.getComponent(game, player, name, args, field));
        } catch (IllegalArgumentException | IllegalAccessException e) {
            // Neither of these should ever occur...
            game.getLogger().log(Level.WARNING, "Error while changing component value: " + field.getName(), e);
        }
    }
    
    /**
     * Checks if the field is setup for this game
     * @param game game to check if setup for
     * @param field field to check is setup
     * @return true if the component is setup correctly
     */
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
                        
            ComponentType componentType = getComponentType(game, field);
            if (componentType != null) {
                return componentType.isSetup(game, field);
            }
        } catch (Exception e) {
            game.getLogger().log(Level.WARNING, "Error while checking if component is setup: " + field.getName(), e);
        }
        
        return true;
    }
    
    /**
     * Gives an array of information about a component: its type, and whether it is setup.
     * @param game game the component belongs to
     * @param field field of the component
     * @return array of information in case the component is an array object
     */
    public static String[] getComponentInfo(Game game, Field field) {
        ComponentType componentType = getComponentType(game, field);
        
        if (componentType == null) {
            return new String[] { "Component is unsupported." };
        }
        
        try {
            return componentType.getInfo(game, field);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            return new String[] {"The developer has setup this component incorrectly"};
        }
    }
    
    /**
     * Gets the ComponentType matching a field's type
     * @param game game the component belongs to
     * @param field field of the component
     * @return ComponentType of the component
     */
    public static ComponentType getComponentType(Game game, Field field) {
        for (ComponentType componentType : registeredTypes) {
            if (componentType instanceof Array1DComponent && field.getType().isArray()) {
                return componentType;
            }
            
            for (Class<?> clazz : componentType.types) {
                if (clazz.isAssignableFrom(field.getType())) {
                    return componentType;
                }
            }
        }
        
        return null;
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