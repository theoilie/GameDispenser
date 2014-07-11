/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.dispenser.game;

/**
 * @author t7seven7t
 */
public class GameState {
    
    public static final GameState   EDITING     = new GameState("editing"),
                                    INACTIVE    = new GameState("inactive"), 
                                    LOBBY       = new GameState("lobby"), 
                                    ACTIVE      = new GameState("active");
    
    private final String name;
    public GameState(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
}
