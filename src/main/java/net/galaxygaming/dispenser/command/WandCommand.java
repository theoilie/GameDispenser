package net.galaxygaming.dispenser.command;

import net.galaxygaming.util.SelectionUtil;

import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;

/**
 * 
 */
class WandCommand extends Command {

    public WandCommand() {
        this.name = "wand";
        this.prefix = "gd";
        this.mustBePlayer = true;
        this.permission = new Permission("gamedispenser.command.wand");
    }
    
    @Override
    public void perform() {
        player.getInventory().addItem(new ItemStack(SelectionUtil.getInstance().getWand(), 1));
    }
}