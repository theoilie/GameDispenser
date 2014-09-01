package net.galaxygaming.dispenser.command;

import java.util.Arrays;

import org.bukkit.permissions.Permission;

import net.galaxygaming.dispenser.game.Game;
import net.galaxygaming.dispenser.game.GameManager;
import net.galaxygaming.dispenser.game.component.SetComponentException;

class SetCommand extends Command {

    public SetCommand() {
        this.prefix = "gd";
        this.name = "set";
        this.requiredArgs.add("name");
        this.requiredArgs.add("component");
        this.optionalArgs.add("other stuff");
        this.mustBePlayer = true;
        this.description = "Set game components such as spawns, boundaries or other configurations";
        this.permission = new Permission("gamedispenser.command.set");
    }
    
    @Override
    public void perform() {
        Game game = GameManager.getGameManager().getGame(args[0]);
        if (game == null) {
            error(messages.getMessage(CommandMessage.UNKNOWN_GAME), args[0]);
            return;
        }
        
        String joinArgs = "";
        if (args.length > 2) {
            StringBuilder result = new StringBuilder();
            for (String arg : Arrays.copyOfRange(args, 2, args.length)) {
                result.append(arg + " ");
            }
            result.deleteCharAt(result.length());
            joinArgs = result.toString();
        }
        
        try {
            game.setComponent(args[1], player, joinArgs);
            sendMessage(messages.getMessage(CommandMessage.SET_COMPONENT_SUCCESS), args[1], args[0]);
        } catch (SetComponentException e) {
            if (e.getMessage() != null) {
                sendMessage(e.getMessage());
            }
            
            if (e.getCause() != null) {
                error(e.getCause());
            }
        }
    }
}