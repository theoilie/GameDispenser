package net.galaxygaming.dispenser.game.component;

import java.lang.reflect.Field;

import org.bukkit.entity.Player;
import net.galaxygaming.dispenser.game.Game;

public abstract class ComponentType {

    Class<?>[] types;
    
    protected ComponentType(Class<?>... types) {
        this.types = types;
        ComponentManager.register(this);
    }
        
    public abstract Object deserialize(Game game, String path, Field field);
    
    public abstract void serialize(Game game, String path, Field field) throws Exception;
        
    public abstract Object componentFromSetup(Game game, String name, Class<?> clazz, Player player, String args) throws SetComponentException;
    
    public Object getComponent(Game game, Player player, String name, String args, Field field) throws SetComponentException {
        return componentFromSetup(game, name, field.getType(), player, args);
    }
    
    public boolean isSetup(Game game, Field field) {
        try {
            field.setAccessible(true);
            return field.get(game) != null;
        } catch (IllegalArgumentException | IllegalAccessException e) {
            // Ignore
            return true;
        }
    }
    
    public String getPrintedValue(Class<?> clazz, Object o) {
        return o.toString();
    }
    
    /**
     * Returns two arguments if successful: the type of the component, and its existing value
     */
    public String[] getInfo(Game game, Field field) throws IllegalArgumentException, IllegalAccessException {        
        return new String[] {field.getType().getSimpleName(), (!isSetup(game, field) ? "&cX" : "&a" + field.get(game).toString())};
    }
}