/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.dispenser;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author t7seven7t
 */
public class MessagesResource {
    
    private final ResourceBundle messages;

    public MessagesResource(File folder, String name, ClassLoader classLoader) {
        messages = ResourceBundle.getBundle(name, Locale.getDefault(), classLoader);
    }
    
    public MessagesResource(File folder, ClassLoader classLoader) {
        this(folder, "messages", classLoader);
    }
    
    public MessagesResource(ClassLoader classLoader) {
        this(null, "messages", classLoader);
    }
    
    public MessagesResource(String name, ClassLoader classLoader) {
        this(GameDispenser.getInstance().getDataFolder(), name, classLoader);
    }
        
    public String getMessage(String key) {
        String result = null;
        if (messages != null) {
            result = messages.getString(key);
        }

        if (result == null && this != GameDispenser.getInstance().getMessages()) {
            return GameDispenser.getInstance().getMessages().getMessage(key);
        }
        
        if (result == null) {
            throw new RuntimeException("Could not locate a message for key " + key);
        }
        
        return result;
    }
}