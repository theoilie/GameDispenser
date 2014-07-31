package net.galaxygaming.dispenser;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

import net.galaxygaming.util.FileResourceLoader;

/**
 * This class can be used to access new .properties resource files
 * from either a folder or a location inside of a jar.
 * <br>
 * <br>
 * By default each {@link GameType} has its own messages resource
 * which can be accessed through {@link GameType#getMessage(String)}.
 */
public class MessagesResource {
    
    private final ResourceBundle messages;

    /**
     * Looks for a .properties file firstly in a folder and if not
     * found checks the URL path of the class loader.
     * <br>
     * <br>
     * To access resources within a game's jar file you must use
     * the game's class loader in this constructor. Other class
     * loaders will not find the resource or may find the wrong one.
     * <br>
     * <br>
     * Note that this method will also return classes that 
     * have the same name
     * @param folder file directory
     * @param name resource name
     * @param classLoader resource loader
     */
    public MessagesResource(File folder, String name, ClassLoader classLoader) {        
        messages = ResourceBundle.getBundle(name, Locale.getDefault(), new FileResourceLoader(classLoader, folder));
    }
    
    /**
     * A message resource with the default resource name 
     * {@code messages}
     * @param folder file directory
     * @param classLoader resource loader
     */
    public MessagesResource(File folder, ClassLoader classLoader) {
        this(folder, "messages", classLoader);
    }
    
    /**
     * A message resource with GameDispenser's data folder as the
     * folder to search in and the resource name 
     * {@code messages}
     * @param classLoader resource loader
     */
    public MessagesResource(ClassLoader classLoader) {
        this(null, "messages", classLoader);
    }
    
    /**
     * A message resource with Game Dispenser's data folder as the
     * folder to search in.
     * @param name resource name
     * @param classLoader resource loader
     */
    public MessagesResource(String name, ClassLoader classLoader) {
        this(GameDispenser.getInstance().getDataFolder(), name, classLoader);
    }
    
    /**
     * Gets a string for the given key from this resource bundle. 
     * If a string cannot be found, gets the string for the key
     * from Game Dispenser's master messages.properties resource.
     * <br>
     * <br>
     * Will throw a {@link RuntimeException} if key could not be matched in
     * either of these resources.
     * 
     * @param key key for the desired string
     * @return string for the desired key
     */
    public String getMessage(String key) {
        String result = null;
        if (messages != null && messages.containsKey(key)) {
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