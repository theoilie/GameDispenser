package net.galaxygaming.dispenser.command;

import org.bukkit.permissions.Permission;

import net.galaxygaming.dispenser.game.Game;
import net.galaxygaming.dispenser.game.GameManager;

class StartCommand extends Command {

    public StartCommand() {
        this.prefix = "gd";
        this.name = "start";
        this.requiredArgs.add("name");
        this.description = "Force start a game";
        this.permission = new Permission("gamedispenser.command.start");
    }
    
    @Override
    public void perform() {        
        Game game = GameManager.getGameManager().getGame(args[0]);
        if (game == null) {
            error(messages.getMessage(CommandMessage.UNKNOWN_GAME), args[0]);
            return;
        }
        
		sendMessage(messages.getMessage(CommandMessage.GAME_START_ATTEMPT), game.getName());
        game.start();
    }
}