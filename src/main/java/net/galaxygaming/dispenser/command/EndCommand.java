package net.galaxygaming.dispenser.command;

import org.bukkit.permissions.Permission;

import net.galaxygaming.dispenser.game.Game;
import net.galaxygaming.dispenser.game.GameManager;

class EndCommand extends Command {

    public EndCommand() {
        this.prefix = "gd";
        this.name = "end";
        this.requiredArgs.add("name");
        this.description = "Force end a game";
        this.permission = new Permission("gamedispenser.command.end");
    }
    
    @Override
    public void perform() {        
        Game game = GameManager.getGameManager().getGame(args[0]);
        if (game == null) {
            error(messages.getMessage(CommandMessage.UNKNOWN_GAME), args[0]);
            return;
        }
        
		sendMessage(messages.getMessage(CommandMessage.GAME_END_ATTEMPT), game.getName());
        game.end();
    }
}