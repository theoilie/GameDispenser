/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.dispenser.command;

import org.bukkit.permissions.Permission;

import net.galaxygaming.dispenser.game.Game;
import net.galaxygaming.dispenser.game.GameManager;

/**
 * @author t7seven7t
 * 
 * Extend this class to setup a join command
 * No permission set for the command by default
 */
class JoinCommand extends Command {

    public JoinCommand() {
        this.name = "join";
        this.requiredArgs.add("name");
        this.mustBePlayer = true;
        this.prefix = "gd";
        this.description = "Join a game";
        this.permission = new Permission("gamedispenser.command.join");
    }
    
    @Override
    public void perform() {
        if (GameManager.getInstance().getGameForPlayer(player) != null) {
            error(messages.getMessage(CommandMessage.ALREADY_IN_GAME));
            return;
        }
        
        Game game = GameManager.getInstance().getGame(args[0]);
        if (game == null) {
            error(messages.getMessage(CommandMessage.UNKNOWN_GAME), args[0]);
            return;
        }
        
        game.addPlayer(player);
    }
}