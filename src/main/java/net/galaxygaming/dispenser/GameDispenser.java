/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.dispenser;

import net.galaxygaming.dispenser.event.EventManager;
import net.galaxygaming.dispenser.game.GameManager;
import net.galaxygaming.util.LogUtil;

import org.apache.commons.lang.Validate;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author t7seven7t
 */
public class GameDispenser extends JavaPlugin {

    private static GameDispenser instance;
    
    private LogUtil logHelper;
    private GameManager gameManager;
        
    public void onEnable() {
        GameDispenser.instance = this;
        this.logHelper = new LogUtil(this);
        if (!this.getDataFolder().exists())
            this.getDataFolder().mkdir();
        
        this.gameManager = new GameManager(this, this.getDataFolder());
        EventManager.getInstance().setup(this);
        MessagesResource.getInstance().setup(this);
        
        logHelper.log("Loaded {0} games.", gameManager.loadGameTypes().length);
    }
    
    public LogUtil getLogHelper() {
        return this.logHelper;
    }
    
    public static GameDispenser getInstance() {
        Validate.notNull(GameDispenser.instance, "GameDispenser not yet initialized.");
        return GameDispenser.instance;
    }
    
}
