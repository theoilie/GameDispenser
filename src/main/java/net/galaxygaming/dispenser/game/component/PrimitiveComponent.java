package net.galaxygaming.dispenser.game.component;

import java.lang.reflect.Field;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.galaxygaming.dispenser.game.Game;
import net.galaxygaming.selection.Selection;
import net.galaxygaming.util.SelectionUtil;

class PrimitiveComponent extends ComponentType {

    public PrimitiveComponent() {
        super(  Integer.class,
                Integer.TYPE,
                String.class,
                Float.class,
                Float.TYPE,
                Boolean.class,
                Boolean.TYPE,
                Double.class,
                Double.TYPE,
                Long.class,
                Long.TYPE,
                Short.class,
                Short.TYPE,
                Byte.class,
                Byte.TYPE,
                Character.class,
                Character.TYPE,
                ConfigurationSerializable.class);
    }

    @Override
    public Object deserialize(Game game, String path, Field field) {
        return game.getConfig().get(path);
    }

    @Override
    public void serialize(Game game, String path, Field field) throws Exception {
        game.getConfig().set(path, field.get(game));
    }

    @Override
    public Object componentFromSetup(Game game, String name, Class<?> clazz, Player player, String args) throws SetComponentException {        
        try {
            if (clazz == Integer.TYPE || clazz == Integer.class) {
                return Integer.valueOf(args);
            } else if (clazz == String.class) {
                return args;
            } else if (clazz == Byte.class || clazz == Byte.TYPE) {
                return Byte.valueOf(args);
            } else if (clazz == Boolean.class || clazz == Boolean.TYPE) {
                return Boolean.valueOf(args);
            } else if (clazz == Short.class || clazz == Short.TYPE) {
                return Short.valueOf(args);
            } else if (clazz == Long.class || clazz == Long.TYPE) {
                return Long.valueOf(args);
            } else if (clazz == Float.class || clazz == Float.TYPE) {
                return Float.valueOf(args);
            } else if (clazz == Character.class || clazz == Character.TYPE) {
                return args.charAt(0);
            } else if (clazz == Double.class || clazz == Double.TYPE) {
                return Double.valueOf(args);
            } else if (ItemStack.class.isAssignableFrom(clazz)) {
                return player.getItemInHand();
            } else if (Selection.class.isAssignableFrom(clazz)) {
                Selection selection = SelectionUtil.getInstance().getSelection(player);
                if (selection == null) {
                    throw new SetComponentException(game, "selection.noSelection");
                } else if (!selection.arePointsSet()) {
                    throw new SetComponentException(game, "selection.noSelection");
                } else if (!selection.arePointsInSameWorld()) {
                    throw new SetComponentException(game, "selection.pointsDifferentWorlds");
                }
                return selection.clone();
            }
        } catch (NumberFormatException e) {
            throw new SetComponentException(game, "commands.notANumber", args);
        }
        
        return null;
    }
    
//    @Override
//    public String getPrintedValue(Class<?> clazz, Object o) {
//        if (Selection.class.isAssignableFrom(clazz)) {
//            return ((Selection) o).toString();
//        } else if (ItemStack.class.isAssignableFrom(clazz)) {
//            return ((ItemStack) o).toString();
//        }
//        
//        return super.getPrintedValue(clazz, o);
//    }
}