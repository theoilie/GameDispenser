package net.galaxygaming.dispenser.game.component;

import net.galaxygaming.dispenser.game.Game;
import net.galaxygaming.util.FormatUtil;

/**
 * 
 */
public class SetComponentException extends Exception {
    private static final long serialVersionUID = 191857850174787104L;
    
    public SetComponentException(final Throwable cause) {
        super(cause);
    }
    
    public SetComponentException() {
        
    }
    
    public SetComponentException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public SetComponentException(final String message) {
        super(message);
    }
    
    public SetComponentException(final Game game, final String message, final Object... args) {
        super(FormatUtil.format(game.getType().getMessages().getMessage(message), args));
    }
}