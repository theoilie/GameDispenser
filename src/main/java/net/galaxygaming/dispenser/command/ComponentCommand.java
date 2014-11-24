package net.galaxygaming.dispenser.command;

import org.bukkit.permissions.Permission;

import net.galaxygaming.dispenser.game.Game;
import net.galaxygaming.dispenser.game.GameManager;

class ComponentCommand extends Command {

    public ComponentCommand() {
        this.prefix = "gd";
        this.name = "component";
        this.aliases.add("comp");
        this.aliases.add("components");
        this.requiredArgs.add("game name");
        this.optionalArgs.add("page");
        this.description = "Shows a list of components a game has";
        this.permission = new Permission("gamedispenser.command.component");
    }
    
    String[] titles = new String[] {
            "Name",
            "Type",
            "Value"
    };
    
    int[] spacings = new int[] {
            25,
            15,
            18
    };
    
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
        
        Object[] result = game.getComponentsInfo();
        int[] counts = (int[]) result[0];
        String[][] data = (String[][]) result[1];
        int ratio = (counts[0] * 100 / counts[1]);
        String header = "Components " + (ratio == 100 ? "&a" : "&c") + ratio + "%";
        
        printTable(titles, data, spacings, header, page, !isPlayer());
    }
}