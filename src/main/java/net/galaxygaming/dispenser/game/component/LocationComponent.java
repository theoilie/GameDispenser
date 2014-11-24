package net.galaxygaming.dispenser.game.component;

import java.lang.reflect.Field;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.galaxygaming.dispenser.game.Game;
import net.galaxygaming.util.LocationUtil;

class LocationComponent extends ComponentType {

    public LocationComponent() {
        super(Location.class);
    }
    
    @Override
    public Object deserialize(Game game, String path, Field field) {
        return LocationUtil.deserializeLocation(game.getConfig().getString(path));
    }

    @Override
    public void serialize(Game game, String path, Field field) throws Exception {
        game.getConfig().set(path, LocationUtil.serializeLocation((Location) field.get(game)));
    }

    @Override
    public Object componentFromSetup(Game game, String name, Class<?> clazz, Player player, String args) throws SetComponentException {
        return player.getLocation();
    }
    
    @Override
    public String[] getInfo(Game game, Field field) throws IllegalArgumentException, IllegalAccessException {        
        return new String[] {field.getType().getSimpleName(), (!isSetup(game, field) ? "&cX": "&a" + LocationUtil.serializeLocationShort((Location) field.get(game)))};
    }
    
    @Override
    public String getPrintedValue(Class<?> clazz, Object o) {
        return LocationUtil.serializeLocationShort((Location) o);
    }
}