package net.galaxygaming.dispenser.game.component;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import com.google.common.collect.Lists;

import net.galaxygaming.dispenser.game.Game;
import net.galaxygaming.util.LocationUtil;

class ListComponent extends ComponentType {

    public ListComponent() {
        super(List.class);
    }
    
    public Object deserialize(Game game, String path, Field field) {
        List<?> list = game.getConfig().getList(path);
        Class<?> type = ComponentManager.getParameterizedType(field.getGenericType());
        
        if (type == null) {
            return list;
        }
        
        List<Object> result = Lists.newArrayList();        
        Iterator<?> it = list.iterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (Location.class.isAssignableFrom(type)) {
                result.add(LocationUtil.deserializeLocation((String) o));
            } else {
                result.add(o);
            }
        }
        
        return result;
    }

    @Override
    public void serialize(Game game, String path, Field field) throws Exception {
        List<Object> result = Lists.newArrayList();
        Class<?> type = ComponentManager.getParameterizedType(field.getGenericType());
        List<?> list = (List<?>) field.get(game);
        
        if (type == null) {
            game.getConfig().set(path, list);
        } else {
            Iterator<?> it = list.iterator();
            while(it.hasNext()) {
                Object o = it.next();
                if (Location.class.isAssignableFrom(type)) {
                    result.add(LocationUtil.serializeLocation((Location) o));
                } else {
                    result.add(o);
                }
            }
            game.getConfig().set(path, result);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object getComponent(Game game, Player player, String name, String args, Field field) throws SetComponentException {
        try {
            Class<?> type = ComponentManager.getParameterizedType(field.getGenericType());
            List<Object> result = (List<Object>) field.get(game);
            
            if (result == null) {
                result = Lists.newArrayList();
            }
            
            args = args.replaceAll(" ", "");
            if (args.matches("^rm$|^remove$|^r$")) {
                if (result.isEmpty()) {
                    throw new SetComponentException(game, "component.list.end");
                } else {
                    result.remove(result.size() - 1);
                    throw new SetComponentException(game, "component.list.removed", result.size());
                }
            } else if (args.matches("^clear$|^c$|^cl$")) {
                result.clear();
                throw new SetComponentException(game, "component.list.cleared");
            }
            
            result.add(ComponentManager.componentFromSetup(game, name, type, player, args));
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
                List<?> list = (List<?>) field.get(game);
                if (list.isEmpty()) {
                    result = false;
                } else {
                    for (Object o : list) {
                        if (o == null) {
                            result = false;
                            break;
                        }
                    }
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                // Ignore
            }
        }
        return result;
    }
    
    public String[] getInfo(Game game, Field field) throws IllegalArgumentException, IllegalAccessException {    
        field.setAccessible(true);
        List<?> list = (List<?>) field.get(game);
        Class<?> type = ComponentManager.getParameterizedType(field.getGenericType());
        return new String[] {"List: " + type.getSimpleName(), (!isSetup(game, field) ? "&c" : "&a") + list.size() + " values"};
    }
}