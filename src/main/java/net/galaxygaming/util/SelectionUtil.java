package net.galaxygaming.util;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

import net.galaxygaming.dispenser.GameDispenser;
import net.galaxygaming.dispenser.event.EventManager;
import net.galaxygaming.selection.Selection;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.google.common.collect.Maps;

public class SelectionUtil {
	private Material wand = Material.AIR;
	private HashMap<UUID, Selection> selections = Maps.newHashMap();
	private static SelectionUtil instance = new SelectionUtil();
	
	/**
	 * Gets the singelton instance of SelectionUtil
	 * @return SelectionUtil instance
	 */
	public static SelectionUtil getInstance() {
		return instance;
	}
	
	/**
	 * Sets the selection wand
	 * @param material the material of the wand
	 */
	@SuppressWarnings("deprecation")
	public void setWand(String material) {
		try {
			wand = Material.getMaterial(Integer.parseInt(material));
		} catch (NumberFormatException e) {
			wand = Material.getMaterial(material);
		}
		if (wand == null)
			GameDispenser.getInstance().getLogger().log(Level.WARNING,
				"The wand could not be set because the value from the config was invalid.");
	}
	
	/**
	 * Checks if the wand is set
	 * @return true if the wand is set
	 */
	public boolean isWandSet() {
		return wand != null && wand != Material.AIR;
	}
	
	/**
	 * Gets the wand material
	 * @return material of the wand
	 */
	public Material getWand() {
		return wand;
	}
	
	/**
	 * Gets a player's selection
	 * @param player the player with the selection
	 * @return the player's selection
	 */
	public Selection getSelection(Player player) {
		return getSelection(player.getUniqueId());
	}
	
	/**
	 * Gets a player's selection
	 * @param uuid the unique identifier of the player with the selection
	 * @return the player's selection
	 */
	public Selection getSelection(UUID uuid) {
		return selections.get(uuid);
	}
	
	/**
	 * Checks if the player has a selection
	 * @param player the player to check
	 * @return true if the player has a selection
	 */
	public boolean hasSelection(Player player) {
		return hasSelection(player.getUniqueId());
	}
	
	/**
	 * Checks if the player has a selection
	 * @param uuid the unique identifier of the player to check
	 * @return true if the player has a selection
	 */
	public boolean hasSelection(UUID uuid) {
		return getSelection(uuid) != null;
	}
	
	/**
	 * Adds a selection to the list
	 * @param selection the selection to be added
	 */
	public void addSelection(Selection selection) {
		selections.put(selection.getPlayer().getUniqueId(), selection);
	}
	
    /**
     * Cloning is not supported.
     */
    @Override
    public EventManager clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}