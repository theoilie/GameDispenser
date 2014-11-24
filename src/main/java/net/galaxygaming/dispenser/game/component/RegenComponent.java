package net.galaxygaming.dispenser.game.component;

import java.lang.reflect.Field;

import org.bukkit.entity.Player;

import net.galaxygaming.dispenser.game.Game;
import net.galaxygaming.selection.RegenableSelection;
import net.galaxygaming.selection.Selection;
import net.galaxygaming.util.SelectionUtil;

class RegenComponent extends ComponentType {

    public RegenComponent() {
        super(RegenableSelection.class);
    }
    
    @Override
    public Object deserialize(Game game, String path, Field field) {
        return RegenableSelection.load(game, path);
    }

    @Override
    public void serialize(Game game, String path, Field field) throws Exception {
        ((RegenableSelection) field.get(game)).save();
    }

    @Override
    public Object componentFromSetup(Game game, String name, Class<?> clazz, Player player, String args) throws SetComponentException {
        Selection selection = SelectionUtil.getInstance().getSelection(player);
        if (selection == null) {
            throw new SetComponentException(game, "selection.noSelection");
        } else if (!selection.arePointsSet()) {
            throw new SetComponentException(game, "selection.noSelection");
        } else if (!selection.arePointsInSameWorld()) {
            throw new SetComponentException(game, "selection.pointsDifferentWorlds");
        }
        return new RegenableSelection(game, name, selection);
    }
    
    @Override
    public String[] getInfo(Game game, Field field) {        
        return new String[] { "Selection", !isSetup(game, field) ? "&cX" : "&asetup"};
    }
    
    @Override
    public String getPrintedValue(Class<?> clazz, Object o) {
        return ((RegenableSelection) o).getSelection().toString();
    }
}