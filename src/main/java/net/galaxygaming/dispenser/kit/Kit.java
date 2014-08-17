package net.galaxygaming.dispenser.kit;

import net.galaxygaming.dispenser.game.Game;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface Kit {
	
	/**
	 * Gets the items the kit has.
	 * @return ItemStack array of items in the kit.
	 */
	public ItemStack[] getItems();
	
	/**
	 * Loads the kit from its file into the class.
	 */
	public void load();
	
	/**
	 * Applies the kit to a player.
	 * @param player - The player the kit is being given to.
	 */
	public void apply(Player player);
	
	/**
	 * Added for clarity. Does the same as {@link #apply(Player)}.
	 * @param player - The player the kit is being given to.
	 */
	public void give(Player player);
	
	/**
	 * Gets the name of this kit.
	 * @return The name of the kit.
	 */
	public String getName();
	
	/**
	 * Gets the name of the kit in game the config.
	 * @return The name of the kit in the config.
	 */
	public String getConfigName();
	
	/**
	 * Gets the game that this kit belongs to.
	 * @return The game associated with this kit.
	 */
	public Game getGame();
}