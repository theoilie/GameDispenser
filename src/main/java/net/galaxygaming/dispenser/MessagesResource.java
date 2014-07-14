/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.dispenser;

import java.util.ResourceBundle;

import net.galaxygaming.util.I18n;

/**
 * @author t7seven7t
 */
public class MessagesResource {
    
    /** Singleton instance */
    private static final MessagesResource instance = new MessagesResource();
    private ResourceBundle messages;

    private MessagesResource() {}
    
    @Override
    public MessagesResource clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
    
    public void setup(GameDispenser plugin) {
        messages = I18n.getResourceBundle(plugin.getDataFolder(), "messages", plugin.getClass().getClassLoader());
    }
        
    public String getMessage(String key) {
        if (messages == null) {
            return "MessagesResource not setup.";
        }
        
        return messages.getString(key);
    }
    
    public static MessagesResource getInstance() {
        return instance;
    }
    
}
