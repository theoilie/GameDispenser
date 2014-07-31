package net.galaxygaming.dispenser.command;

import org.bukkit.permissions.Permission;

import net.galaxygaming.dispenser.game.Game;
import net.galaxygaming.dispenser.game.GameManager;

class LeaveCommand extends Command {

    public LeaveCommand() {
        this.prefix = "gd";
        this.name = "leave";
        this.mustBePlayer = true;
        this.description = "Leave a game";
        this.permission = new Permission("gamedispenser.command.leave");
    }
    
    @Override
    public void perform() {
        Game game = GameManager.getInstance().getGameForPlayer(player);
        if (game == null) {
            error(messages.getMessage(CommandMessage.NOT_IN_GAME));
            return;
        }
        
        game.removePlayer(player);
    }
}