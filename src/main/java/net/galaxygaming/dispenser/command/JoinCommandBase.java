/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.dispenser.command;

import net.galaxygaming.dispenser.game.Game;
import net.galaxygaming.dispenser.game.GameManager;

/**
 * @author t7seven7t
 * 
 * Extend this class to setup a join command
 * No permission set for the command by default
 */
public class JoinCommandBase extends Command {

    public JoinCommandBase(String prefix) {
        this.name = "join";
        this.requiredArgs.add("name");
        this.mustBePlayer = true;
        this.prefix = prefix;
    }
    
    @Override
    public void perform() {
        Game game = GameManager.getInstance().getGame(args[0]);
        if (game == null) {
            error(messages.getMessage(CommandMessage.UNKNOWN_GAME), args[0]);
            return;
        }
        
        game.addPlayer(player);
    }
}