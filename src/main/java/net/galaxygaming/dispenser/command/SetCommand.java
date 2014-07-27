/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.dispenser.command;

import java.util.Arrays;

import org.bukkit.permissions.Permission;

import net.galaxygaming.dispenser.game.Game;
import net.galaxygaming.dispenser.game.GameManager;
import net.galaxygaming.selection.Selection;
import net.galaxygaming.util.SelectionUtil;

/**
 * @author t7seven7t
 */
class SetCommand extends Command {

    public SetCommand() {
        this.prefix = "gd";
        this.name = "set";
        this.requiredArgs.add("name");
        this.requiredArgs.add("component");
        this.requiredArgs.add("location/selection/other");
        this.mustBePlayer = true;
        this.description = "Set game components such as spawns, boundaries or other configurations";
        this.permission = new Permission("gamedispenser.command.set");
    }
    
    @Override
    public void perform() {
        Game game = GameManager.getInstance().getGame(args[0]);
        if (game == null) {
            error(messages.getMessage(CommandMessage.UNKNOWN_GAME), args[0]);
            return;
        }

        boolean result = false;
        
        if (this.argMatchesAlias(args[2], "l", "location")) {
            result = game.setComponent(args[1], player.getLocation());
        } else if (this.argMatchesAlias(args[2], "s", "selection")) {
            Selection selection = SelectionUtil.getInstance().getSelection(player);
            if (selection == null) {
                error(messages.getMessage(CommandMessage.NO_SELECTION));
                return;
            }
            if (!selection.arePointsSet()) {
                error(messages.getMessage("selection.noSelection"));
                return;
            }
            if (!selection.arePointsInSameWorld()) {
                error(messages.getMessage("selection.pointsDifferentWorlds"));
        		return;
            }
            
            result = game.setComponent(args[1], selection.clone());
        } else {
            if (args[1].equalsIgnoreCase("description")) {
                 result = true;
                 game.getConfig().set("description", Arrays.copyOfRange(args, 2, args.length));
            } else {
                result = game.setComponent(args[1], Arrays.copyOfRange(args, 2, args.length));
            }
        }
        
        if (result)
        		sendMessage(messages.getMessage(CommandMessage.SET_COMPONENT_SUCCESS), args[1], args[0]);
        else
            error(messages.getMessage(CommandMessage.NO_COMPONENT));
    }
}