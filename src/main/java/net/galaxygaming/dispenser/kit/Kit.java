package net.galaxygaming.dispenser.kit;

import net.galaxygaming.dispenser.game.Game;
import net.galaxygaming.util.ItemUtil;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Kit {
	private final String name, configName;
	private final Game game;
	private ItemStack[] items;

	public Kit(String name, String configName, Game game) {
		this.name = ChatColor.translateAlternateColorCodes('&', name);
		this.configName = configName;
		this.game = game;
		load();
	}
	
	/**
     * Gets the items the kit has.
     * @return ItemStack array of items in the kit.
     */
	public ItemStack[] getItems() {
		return items;
	}

	/**
	 * Loads the kit from its file into the class.
	 * This finds kits.configName in the config and loads the items in it.
	 * See {@link net.galaxygaming.dispenser.util.ItemUtil#getItem(String)} for format information.
	 */
	public void load() {
		ConfigurationSection kitSection = game.getConfig().getConfigurationSection("kits." + configName);
		String[] itemStrings = kitSection.getString("items").split(",");
		items = new ItemStack[itemStrings.length];
		for (int i = 0; i < itemStrings.length; i++) {
			items[i] = ItemUtil.getInstance().getItem(itemStrings[i]);
		}
	}

    /**
     * Applies the kit to a player.
     * @param player - The player the kit is being given to.
     */
	public void apply(Player player) {
		player.getInventory().addItem(items);
	}

    /**
     * Added for clarity. Does the same as {@link #apply(Player)}.
     * @param player - The player the kit is being given to.
     */
	public void give(Player player) {
		apply(player);
	}

    /**
     * Gets the name of this kit.
     * @return The name of the kit.
     */
	public String getName() {
		return name;
	}
	
    /**
     * Gets the name of the kit in game the config.
     * @return The name of the kit in the config.
     */
	public String getConfigName() {
		return configName;
	}
	
    /**
     * Gets the game that this kit belongs to.
     * @return The game associated with this kit.
     */
	public Game getGame() {
		return game;
	}
}