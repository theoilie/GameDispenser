/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.dispenser.event;

import net.galaxygaming.dispenser.GameDispenser;
import net.galaxygaming.dispenser.game.Game;
import net.galaxygaming.dispenser.game.GameManager;
import net.galaxygaming.util.FormatUtil;
import net.galaxygaming.util.LocationUtil;
import net.galaxygaming.util.SelectionUtil;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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
    
    /*
     * Selecting blocks for arena creation
     */
	@EventHandler
	private void onPlayerInteractEvent(final PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		if ((player.getItemInHand().getType() == SelectionUtil.getInstance()
				.getWand())) {
			if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
			    player.sendMessage(FormatUtil.format(GameDispenser.getInstance()
			            .getMessages().getMessage("selection.pointSelected"), 
			            "1", LocationUtil.serializeLocation(event.getClickedBlock().getLocation())));
				SelectionUtil.getInstance().getSelection(player)
						.setPointOne(event.getClickedBlock().getLocation());
				event.setCancelled(true);
			} else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                player.sendMessage(FormatUtil.format(GameDispenser.getInstance()
                        .getMessages().getMessage("selection.pointSelected"), 
                        "2", LocationUtil.serializeLocation(event.getClickedBlock().getLocation())));
				SelectionUtil.getInstance().getSelection(player)
						.setPointTwo(event.getClickedBlock().getLocation());
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
	    onDisconnect(event.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerKick(PlayerKickEvent event) {
	    onDisconnect(event.getPlayer());
	}
	
	public void onDisconnect(Player player) {
	    Game game = GameManager.getInstance().getGameForPlayer(player);
	    if (game == null) {
	        return;
	    }
	    
	    game.removePlayer(player);
	}
}