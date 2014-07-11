/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.dispenser.game;

/**
 * @author t7seven7t
 */
public class InvalidGameException extends Exception {
    private static final long serialVersionUID = 6909203799009299139L;
    
    public InvalidGameException(final Throwable cause) {
        super(cause);
    }
    
    public InvalidGameException() {
        
    }
    
    public InvalidGameException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public InvalidGameException(final String message) {
        super(message);
    }
}
