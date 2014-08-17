package net.galaxygaming.dispenser.kit;

import net.galaxygaming.dispenser.game.Game;
import net.galaxygaming.util.ItemUtil;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KitBase implements Kit {
	private final String name, configName;
	private final Game game;
	private ItemStack[] items;

	public KitBase(String name, String configName, Game game) {
		this.name = ChatColor.translateAlternateColorCodes('&', name);
		this.configName = configName;
		this.game = game;
		load();
	}
	
	@Override
	public ItemStack[] getItems() {
		return items;
	}

	/**
	 * This finds kits.configName in the config and loads the items in it.
	 * See {@link net.galaxygaming.dispenser.util.ItemUtil#getItem(String)} for format information.
	 */
	@Override
	public void load() {
		ConfigurationSection kitSection = game.getConfig().getConfigurationSection("kits." + configName);
		String[] itemStrings = kitSection.getString("items").split(",");
		for (int i = 0; i < itemStrings.length; i++) {
			items[i] = ItemUtil.getInstance().getItem(itemStrings[i]);
		}
	}

	@Override
	public void apply(Player player) {
		player.getInventory().addItem(items);
	}

	@Override
	public void give(Player player) {
		player.getInventory().addItem(items);
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getConfigName() {
		return configName;
	}
	
	@Override
	public Game getGame() {
		return game;
	}
}