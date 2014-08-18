package net.galaxygaming.dispenser.event;

import java.util.List;

import net.galaxygaming.dispenser.GameDispenser;
import net.galaxygaming.dispenser.game.Game;
import net.galaxygaming.dispenser.game.GameManager;
import net.galaxygaming.dispenser.game.GameState;
import net.galaxygaming.selection.Selection;
import net.galaxygaming.util.FormatUtil;
import net.galaxygaming.util.LocationUtil;
import net.galaxygaming.util.SelectionUtil;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.metadata.MetadataValue;

class Events implements Listener {

    /*
     * Control what messages players can see while in game
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Game game = GameManager.getGameManager().getGameForPlayer(event.getPlayer());
        if (game == null)
            return;
        
        game.broadcast(String.format(event.getFormat(), event.getPlayer().getDisplayName(), event.getMessage()));
        event.setCancelled(true);
    }
    
    /*
     * Prevent players from using blacklisted commands while in game
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (GameManager.getGameManager().getGameForPlayer(event.getPlayer()) == null) {
            return;
        }
        
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
     * Respawn dead players where they were before joining a game
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (GameManager.getGameManager().getGameForPlayer(event.getPlayer()) != null) {
            return;
        }
        
        // Hopefully no one else registers metadata with this name lol
        List<MetadataValue> metadata = event.getPlayer().getMetadata("gameLastLocation");
        if (metadata != null && !metadata.isEmpty()) {
            event.setRespawnLocation((Location) metadata.get(0).value());
        }
    }
    
    /*
     * Selecting blocks for arena creation
     */
	@EventHandler
	private void onPlayerInteractEvent(final PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if ((player.getItemInHand().getType() == SelectionUtil.getInstance().getWand())) {
			Selection selection = SelectionUtil.getInstance().getSelection(player);
			
			if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
				if (selection == null)
					selection = new Selection(player);
				Location loc = event.getClickedBlock().getLocation();
				
				player.sendMessage(FormatUtil.format(GameDispenser.getInstance().getMessages()
					.getMessage("selection.pointSelected"), "1",
						LocationUtil.serializeLocation(loc)));
				
				selection.setPointOne(loc);
				event.setCancelled(true);
			} else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if (selection == null)
					selection = new Selection(player);
				Location loc = event.getClickedBlock().getLocation();
				
				player.sendMessage(FormatUtil.format(GameDispenser.getInstance().getMessages()
					.getMessage("selection.pointSelected"), "2",
						LocationUtil.serializeLocation(loc)));
				
				selection.setPointTwo(loc);
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
	
	/*
	 * Remove player from game if they disconnect
	 */
	public void onDisconnect(Player player) {
	    Game game = GameManager.getGameManager().getGameForPlayer(player);
	    if (game == null) {
	        return;
	    }
	    
	    game.removePlayer(player);
	}
	
	/*
	 * Add game signs
	 */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onSignChange(SignChangeEvent event) {
	    if (event.getPlayer().hasPermission("gamedispenser.command.sign")) {
	        if (event.getLine(0).equalsIgnoreCase("[game]")) {
	            Game game = GameManager.getGameManager().getGame(event.getLine(1));
	            if (game == null) {
	                event.setLine(1, "GAME NOT FOUND");
	                return;
	            }

	            game.addSign(event.getBlock().getLocation());
	        }
	    }
	}
	
	/*
	 * Remove game signs when broken by anyone/anything
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
	    if (event.getBlock().getType().equals(Material.SIGN)
	            || event.getBlock().getType().equals(Material.SIGN_POST)
	            || event.getBlock().getType().equals(Material.WALL_SIGN)) {
	        for (Game game : GameManager.getGameManager().getGames()) {
	            game.removeSign(event.getBlock().getLocation());
	        }
	    }
	}
	
	/*
	 * Add player to game if they interact with a game sign
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerInteract(PlayerInteractEvent event) {
	    if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
	        if (event.getClickedBlock() == null 
	                || (!event.getClickedBlock().getType().equals(Material.SIGN) 
	                && !event.getClickedBlock().getType().equals(Material.SIGN_POST) 
	                && !event.getClickedBlock().getType().equals(Material.WALL_SIGN))) {
	            return;
	        }
	        
	        if (!event.getPlayer().hasPermission("gamedispenser.command.join")) {
	            event.getPlayer().sendMessage(FormatUtil.format(GameDispenser.getInstance().getMessages().getMessage("game.noPermission")));
	            return;
	        }
	        
	        if (GameManager.getGameManager().getGameForPlayer(event.getPlayer()) != null) {
                event.getPlayer().sendMessage(FormatUtil.format(GameDispenser.getInstance().getMessages().getMessage("commands.alreadyInGame")));
	            return;
	        }
	        
	        for (Game game : GameManager.getGameManager().getGames()) {
	            if (game.getSigns().contains(event.getClickedBlock().getLocation())) {
	                if (game.getState().ordinal() < GameState.INACTIVE.ordinal() || !game.isSetup()) {
	                    event.getPlayer().sendMessage(FormatUtil.format(GameDispenser.getInstance().getMessages().getMessage("commands.gameNotSetup")));
	                    return;
	                } else if (game.getState().ordinal() > GameState.STARTING.ordinal()) {
	                    event.getPlayer().sendMessage(FormatUtil.format(GameDispenser.getInstance().getMessages().getMessage("commands.gameAlreadyActive")));
	                    return;
	                }
	                
	                if (!game.addPlayer(event.getPlayer())) {
	                    event.getPlayer().sendMessage(FormatUtil.format(GameDispenser.getInstance().getMessages().getMessage("commands.gameIsFull")));
	                }
	                event.setCancelled(true);
	                return;
	            }
	        }
	    }
	}
}