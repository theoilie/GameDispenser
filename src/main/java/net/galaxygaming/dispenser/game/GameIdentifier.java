/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.dispenser.game;

/**
 * @author t7seven7t
 */
public class GameIdentifier {

    private Game game;
    
    public GameIdentifier(Game game) {
        this.game = game;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(game.getType().toString());
        builder.append(",");
        return builder.toString();
    }
    
    public static GameIdentifier fromString(String identifier) {
        String[] args = identifier.split(",");
        
        
        GameIdentifier result = null;
        return result;
    }
    
}
