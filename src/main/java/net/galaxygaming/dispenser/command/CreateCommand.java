/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.dispenser.command;

import net.galaxygaming.dispenser.game.GameManager;
import net.galaxygaming.dispenser.game.GameType;
import net.galaxygaming.dispenser.game.InvalidGameException;

/**
 * @author t7seven7t
 */
class CreateCommand extends Command {

    public CreateCommand() {
        this.prefix = "gd";
        this.name = "create";
        this.requiredArgs.add("type");
        this.optionalArgs.add("name");
    }
    
    @Override
    public void perform() {
        GameType type = GameType.get(args[0]);
        if (type == null) {
            error("Not a valid Game Type");
            return;
        }
        
        try {
            if (args.length == 2) {
                GameManager.getInstance().newGame(type, args[1]);
            } else {
                GameManager.getInstance().newGame(type);
            }
        } catch (InvalidGameException e) {
            error(e.getMessage());
        }
    }
}