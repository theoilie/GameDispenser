package net.galaxygaming.dispenser.game.component;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;

import net.galaxygaming.dispenser.game.Game;
import net.galaxygaming.util.LocationUtil;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

class Array1DComponent extends ComponentType {

    // No constructor because array has no inbuilt type
    
    @Override
    public Object deserialize(Game game, String path, Field field) {
        List<?> list = game.getConfig().getList(path);
        Class<?> type = field.getType().getComponentType();
        
        try {
            Object[] result = (Object[]) field.get(game);
            Iterator<?> it = list.iterator();
            int i = 0;
            while(it.hasNext()) {
                Object o = it.next();
                if (Location.class.isAssignableFrom(type)) {
                    result[i] = LocationUtil.deserializeLocation((String) o);
                } else {
                    result[i] = o;
                }
                i++;
            }
            return result;
        } catch (IllegalArgumentException | IllegalAccessException e) {
            // Ignore
            return null;
        }
    }

    @Override
    public void serialize(Game game, String path, Field field) throws Exception {
        Object[] array = (Object[]) field.get(game);
        List<Object> result = Lists.newArrayList();
        Class<?> type = field.getType().getComponentType();
        
        for (Object o : array) {
            if (Location.class.isAssignableFrom(type)) {
                result.add(LocationUtil.serializeLocation((Location) o));
            } else {
                result.add(o);
            }
        }
        game.getConfig().set(path, result);
    }
    
    @Override
    public Object getComponent(Game game, Player player, String name, String args, Field field) throws SetComponentException {
        try {
            Class<?> type = field.getType().getComponentType();
            Object[] result = (Object[]) field.get(game);
            
            if (result == null) {
                throw new SetComponentException(game, "component.array.null", field.getName());
            }
            
            if (args.matches("^i$|^info$")) {
                String response = name + " info:\n";
                for (int i = 0; i < result.length; i++) {
                    response += i + ": ";
                    if (result[i] == null) {
                        response += "null";
                    } else {
                        response += ComponentManager.getPrintedValue(type, result[i]);
                    }
                    if (i < result.length - 1) {
                        response += "\n";
                    }
                }
                throw new SetComponentException(response);
            }
            
            int index = 0;
            try {
                index = Integer.valueOf(args);
                index -= 1;
            } catch (NumberFormatException e) {
                throw new SetComponentException(game, "component.array.numberFormat", args);
            }
            
            if (index >= result.length || index < 0) {
                throw new SetComponentException(game, "component.array.indexOutOfBounds", args);
            }
            
            result[index] = ComponentManager.componentFromSetup(game, name, type, player, args);
            return result;
        } catch (IllegalArgumentException | IllegalAccessException e) {
            // Neither of these should occur, ignore
            return null;
        }
    }

    @Override
    public Object componentFromSetup(Game game, String name, Class<?> clazz, Player player, String args) throws SetComponentException {
        return null;
    }
    
    @Override
    public boolean isSetup(Game game, Field field) {
        boolean result = super.isSetup(game, field);
        if (result) {
            try {
                Object[] array = (Object[]) field.get(game);
                for (Object o : array) {
                    if (o == null) {
                        result = false;
                        break;
                    }
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                // Ignore
            }
        }
        return result;
    }
    
    @Override
    public String[] getInfo(Game game, Field field) throws IllegalArgumentException, IllegalAccessException {
        field.setAccessible(true);
        Object[] array = (Object[]) field.get(game);
        Class<?> type = field.getType().getComponentType();
        int setup = 0;
        for (Object o : array) {
            if (o != null) {
                setup++;
            }
        }
        return new String[] {"Array: " + type.getSimpleName(), (!isSetup(game, field) ? "&c" : "&a") + setup + "&7/&6" + array.length};
    }
}