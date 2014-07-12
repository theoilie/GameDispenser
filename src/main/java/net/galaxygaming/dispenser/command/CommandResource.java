/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.dispenser.command;

import java.util.ResourceBundle;

import net.galaxygaming.dispenser.GameDispenser;
import net.galaxygaming.util.I18n;

/**
 * @author t7seven7t
 */
public class CommandResource {
    
    public static final String
        ERROR                        = "error",
        INSUFFICIENT_PERMISSION      = "insufficient_permission",
        TOO_FEW_ARGS                 = "too_few_args",
        MUST_BE_PLAYER               = "must_be_player";
    
    private final ResourceBundle messages;
    
    public CommandResource() {
        this.messages = I18n.getResourceBundle(GameDispenser.getInstance().getDataFolder(), "messages_commands", getClass().getClassLoader());
    }
    
    public String getMessage(String key) {
        return messages.getString(key);
    }
    
}
