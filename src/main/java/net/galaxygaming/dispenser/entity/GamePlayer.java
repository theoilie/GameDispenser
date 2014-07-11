/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.dispenser.entity;

import org.bukkit.entity.Player;

/**
 * @author t7seven7t
 */
public class GamePlayer {

    private final Player player;
    
    public GamePlayer(Player player) {
        this.player = player;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
}
