package net.galaxygaming.dispenser;

import java.util.logging.Level;

import net.galaxygaming.dispenser.command.CommandManager;
import net.galaxygaming.dispenser.event.EventManager;
import net.galaxygaming.dispenser.game.GameManager;
import net.galaxygaming.selection.Selection;
import net.galaxygaming.util.FormatUtil;
import net.galaxygaming.util.SelectionUtil;

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

public class GameDispenser extends JavaPlugin {

    private static GameDispenser instance;
    
    private MessagesResource messages;
    private String[] blacklistedCommands;
        
    public void onEnable() {
        GameDispenser.instance = this;
        if (!this.getDataFolder().exists())
            this.getDataFolder().mkdir();
        
        ConfigurationSerialization.registerClass(Selection.class);
        
        messages = new MessagesResource(getDataFolder(), getClassLoader());
        blacklistedCommands = getConfig().getStringList("blacklistedCommands").toArray(new String[0]);
        
        EventManager.getInstance().setup(this);
        CommandManager.getInstance().setup(this);
        
        GameManager gameManager = GameManager.getInstance();
        gameManager.setup(this, this.getDataFolder());
        
        SelectionUtil.getInstance().setWand(getConfig().getString("wand"));
                
        getLogger().log(Level.INFO, FormatUtil.format("Loaded {0} game types.", gameManager.loadGameTypes().length));
        getLogger().log(Level.INFO, FormatUtil.format("Loaded {0} games.", gameManager.loadGames().length));
    }
    
    public void onDisable() {
        GameManager.getInstance().unloadAll();
    }
    
    /**
     * Gets the messages file
     * @return the messages file
     */
    public MessagesResource getMessages() {
        return messages;
    }
    
    /**
     * Gets the list of blacklisted commands
     * @return the blacklisted commands
     */
    public String[] getBlacklistedCommands() {
        return blacklistedCommands.clone();
    }
    
    /**
     * Gets the singleton instance
     * @return singleton instance of GameDispenser
     */
    public static GameDispenser getInstance() {
        Validate.notNull(instance, "GameDispenser is not yet initialized.");
        return instance;
    }
    
    /**
     * Cloning is not supported.
     */
    @Override
    public EventManager clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}