package net.galaxygaming.dispenser.game;

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