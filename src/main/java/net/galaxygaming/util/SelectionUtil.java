package net.galaxygaming.util;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

import net.galaxygaming.dispenser.GameDispenser;
import net.galaxygaming.selection.Selection;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.google.common.collect.Maps;

public class SelectionUtil {
	private Material wand = Material.AIR;
	private HashMap<UUID, Selection> selections = Maps.newHashMap();
	private static SelectionUtil instance = new SelectionUtil();
	
	public static SelectionUtil getInstance() {
		return instance;
	}
	
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
	
	public boolean isWandSet() {
		return wand != null;
	}
	
	public Material getWand() {
		return wand;
	}
	
	public Selection getSelection(Player player) {
		return getSelection(player.getUniqueId());
	}
	
	public Selection getSelection(UUID uuid) {
		return selections.get(uuid);
	}
	
	public boolean hasSelection(Player player) {
		return hasSelection(player.getUniqueId());
	}
	
	public boolean hasSelection(UUID uuid) {
		return getSelection(uuid) != null;
	}
	
	public void addSelection(Selection selection) {
		selections.put(selection.getPlayer().getUniqueId(), selection);
	}
}