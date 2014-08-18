package net.galaxygaming.dispenser.command;

import org.bukkit.permissions.Permission;

import net.galaxygaming.dispenser.game.Game;
import net.galaxygaming.dispenser.game.GameManager;

class ComponentCommand extends Command {

    public ComponentCommand() {
        this.prefix = "gd";
        this.name = "component";
        this.requiredArgs.add("game name");
        this.optionalArgs.add("page");
        this.description = "Shows a list of components a game has";
        this.permission = new Permission("gamedispenser.command.component");
    }
    
    @Override
    public void perform() {
        Game game = GameManager.getGameManager().getGame(args[0]);
        if (game == null) {
            error(messages.getMessage(CommandMessage.UNKNOWN_GAME), args[0]);
            return;
        }
        
        int page = 1;
        if (args.length > 1) {
            try {
                page = Integer.valueOf(args[1]);
            } catch (NumberFormatException e) {
                error(messages.getMessage(CommandMessage.NOT_A_NUMBER), args[1]);
                return;
            }
        }
        printList(page, game.getComponents().toArray(new String[0]));
    }
}