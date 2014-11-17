package net.galaxygaming.dispenser.command;

import net.galaxygaming.dispenser.game.Game;
import net.galaxygaming.dispenser.game.GameManager;
import net.galaxygaming.dispenser.kit.Kit;

import org.bukkit.permissions.Permission;

class KitCommand extends Command {

    public KitCommand() {
        this.prefix = "gd";
        this.name = "kit";
        this.mustBePlayer = true;
        this.optionalArgs.add("kit name");
        this.description = "Select a kit.";
        this.permission = new Permission("gamedispenser.command.kit");
    }
    
    @Override
    public void perform() {
        Game game = GameManager.getGameManager().getGameForPlayer(player);
        if (game == null) {
            error(messages.getMessage(CommandMessage.NOT_IN_GAME));
            return;
        }
        
        Kit[] kits = game.getKits().toArray(new Kit[0]);
        
        if (args.length == 0) {
            printList(1, "Kits", kits);
            return;
        }
        
        Kit selectedKit = null;
        for (Kit kit : kits) {
            if (kit.getName().equalsIgnoreCase(args[0])) {
                selectedKit = kit;
                break;
            }
        }
        
        if (selectedKit == null) {
            error(messages.getMessage(CommandMessage.NO_KIT), args[0]);
            printList(1, "Kits", kits);
            return;
        }
        
       selectedKit.apply(player);
    }
}