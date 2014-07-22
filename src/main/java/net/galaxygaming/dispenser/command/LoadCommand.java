/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.dispenser.command;

import org.bukkit.permissions.Permission;

import net.galaxygaming.dispenser.game.GameManager;
import net.galaxygaming.dispenser.game.InvalidGameException;

/**
 * @author t7seven7t
 */
class LoadCommand extends Command {

    public LoadCommand() {
        this.prefix = "gd";
        this.name = "load";
        this.requiredArgs.add("type/game");
        this.requiredArgs.add("name");
        this.description = "Load a game or game type from file";
        this.permission = new Permission("gamedispenser.command.load");
    }
    
    @Override
    public void perform() {
        try {
            if (this.argMatchesAlias(args[0], "type", "t")) {
                GameManager.getInstance().loadGameType(args[1]);
            } else if (this.argMatchesAlias(args[0], "game", "g")) {
                GameManager.getInstance().loadGame(args[1]);
            } else {
                error("Please specify either 'game' or 'type'");
                return;
            }
        } catch (InvalidGameException e) {
            error(e.getMessage(), e);
        }
    }
}