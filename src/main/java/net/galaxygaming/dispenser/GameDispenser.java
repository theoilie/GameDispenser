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
    
    private LogUtil log;
        
    public void onEnable() {
        GameDispenser.instance = this;
        if (!this.getDataFolder().exists())
            this.getDataFolder().mkdir();
        
        EventManager.getInstance().setup(this);
        MessagesResource.getInstance().setup(this);
        LogUtil.getInstance().setup(this);
        log = LogUtil.getInstance();
        
        GameManager gameManager = GameManager.getInstance();
        gameManager.setup(this, this.getDataFolder());
                
        log.log("Loaded {0} game types.", gameManager.loadGameTypes().length);
        log.log("Loaded {0} games.", gameManager.loadGames().length);
    }
    
    public static GameDispenser getInstance() {
        Validate.notNull(GameDispenser.instance, "GameDispenser is not yet initialized.");
        return GameDispenser.instance;
    }   
}