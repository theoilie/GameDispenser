/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.dispenser.event;

import net.galaxygaming.dispenser.GameDispenser;
import net.galaxygaming.dispenser.game.Game;
import net.galaxygaming.dispenser.game.GameManager;
import net.galaxygaming.util.FormatUtil;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * @author t7seven7t
 */
class Events implements Listener {

    /*
     * Control what messages players can see while in game
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Game game = GameManager.getInstance().getGameForPlayer(event.getPlayer());
        if (game == null)
            return;
        
        game.broadcast(event.getFormat());
        event.setCancelled(true);
    }
    
    /*
     * Prevent players from using blacklisted commands while in game
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerCommandPreproces(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage()
                .replaceAll("^/*", "") // Remove forward slash if exists
                .replaceAll(" .*", ""); // Remove anything after first word
        
        for (String s : GameDispenser.getInstance().getBlacklistedCommands()) {
            if (s.equalsIgnoreCase(command)) {
                event.getPlayer().sendMessage(FormatUtil.format("&4Cannot use that command while playing a game."));
                event.setCancelled(true);
                return;
            }
        }
    }
}