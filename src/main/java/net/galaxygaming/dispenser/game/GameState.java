/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.dispenser.game;

/**
 * @author t7seven7t
 */
public class GameState {
    
    public static final GameState   
        EDITING     = new GameState(0),
        INACTIVE    = new GameState(1), 
        LOBBY       = new GameState(2), 
        ACTIVE      = new GameState(3);
    
    private final int ordinal;
    public GameState(int ordinal) {
        this.ordinal = ordinal;
    }
    
    /**
     * Returns the ordinal of this GameState (its position in 
     * the progression of a game)
     * @return the ordinal of this state
     */
    public int ordinal() {
        return this.ordinal;
    }
}