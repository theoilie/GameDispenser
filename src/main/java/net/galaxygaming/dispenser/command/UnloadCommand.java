/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.dispenser.command;

import org.bukkit.permissions.Permission;

import net.galaxygaming.dispenser.game.Game;
import net.galaxygaming.dispenser.game.GameManager;
import net.galaxygaming.dispenser.game.GameType;
import net.galaxygaming.dispenser.game.InvalidGameException;

/**
 * @author t7seven7t
 */
class UnloadCommand extends Command {

    public UnloadCommand() {
        this.prefix = "gd";
        this.name = "unload";
        this.requiredArgs.add("type/game");
        this.requiredArgs.add("name");
        this.description = "Unload a game or game type from the server";
        this.permission = new Permission("gamedispenser.command.unload");
    }
    
    @Override
    public void perform() {
        try {
            if (this.argMatchesAlias(args[0], "type", "t")) {
                GameType type = GameType.get(args[1]);
                if (type == null) {
                    error(messages.getMessage(CommandMessage.UNKNOWN_GAME_TYPE), args[1]);
                    return;
                }
                GameManager.getInstance().unloadGameType(type);
            } else if (this.argMatchesAlias(args[0], "game", "g")) {
                Game game = GameManager.getInstance().getGame(args[1]);
                if (game == null) {
                    error(messages.getMessage(CommandMessage.UNKNOWN_GAME), args[1]);
                    return;
                }
                GameManager.getInstance().unloadGame(game);
            } else {
                error("Please specify either 'game' or 'type'");
                return;
            }
        } catch (InvalidGameException e) {
            error(e.getMessage(), e);
        }
    }
}