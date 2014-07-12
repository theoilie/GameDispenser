/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.dispenser;

import net.galaxygaming.dispenser.event.EventManager;
import net.galaxygaming.dispenser.game.GameManager;
import net.galaxygaming.util.LogHelper;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author t7seven7t
 */
public class GameDispenser extends JavaPlugin {

    private static GameDispenser instance;
    
    private LogHelper logHelper;
    private GameManager gameManager;
    private EventManager eventManager;
    
    public void onEnable() {
        GameDispenser.instance = this;
        this.logHelper = new LogHelper(this);
        if (!this.getDataFolder().exists())
            this.getDataFolder().mkdir();
        
        this.gameManager = new GameManager(this, this.getDataFolder());
        this.eventManager = new EventManager(this);
        
        logHelper.log("Loaded {0} games.", gameManager.loadGameTypes().length);
    }
    
    public LogHelper getLogHelper() {
        return this.logHelper;
    }
    
    public static GameDispenser getInstance() {
        return GameDispenser.instance;
    }
    
}
