/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.dispenser.command;

import net.galaxygaming.dispenser.game.Game;
import net.galaxygaming.dispenser.game.GameManager;
import net.galaxygaming.dispenser.game.GameType;
import net.galaxygaming.dispenser.game.InvalidGameException;

import org.bukkit.permissions.Permission;

/**
 * @author t7seven7t
 */
class CreateCommand extends Command {

    public CreateCommand() {
        this.prefix = "gd";
        this.name = "create";
        this.requiredArgs.add("type");
        this.optionalArgs.add("name");
        this.description = "Create a game";
        this.permission = new Permission("gamedispenser.command.create");
    }
    
    @Override
    public void perform() {
        GameType type = GameType.get(args[0]);
        if (type == null) {
            error(messages.getMessage(CommandMessage.UNKNOWN_GAME_TYPE), args[0]);
            return;
        }
        
        try {
        		Game game = null;
            if (args.length == 2) {
                game = GameManager.getInstance().newGame(type, args[1]);
            } else {
                game = GameManager.getInstance().newGame(type);
            }
    			sendMessage(messages.getMessage(CommandMessage.GAME_CREATE_SUCCESS), game.getName(), game.getType().toString());
        } catch (InvalidGameException e) {
            error(e.getMessage());
        }
    }
}