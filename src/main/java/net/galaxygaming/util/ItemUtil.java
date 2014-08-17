package net.galaxygaming.util;

import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.galaxygaming.dispenser.exception.InvalidInputException;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class ItemUtil {
	private static ItemUtil instance = new ItemUtil();
	
	public static ItemUtil getInstance() {
		return instance;
	}

	/**
	 * Turns a string into an ItemStack.
	 * All items are to be separated by commas.
	 * Example: item_name1,diamond_sword,another_item
	 * See {@link #addEnchantmentsWithoutName(String[], ItemStack)} for enchantment format.
	 * See {@link #addColor(String[], ItemStack)} for leather coloring format.
	 * @param item - A String to make the ItemStack (includes information such as name and enchantments).
	 * @return ItemStack that was formed.
	 */
	public ItemStack getItem(String item) {
		String[] data;
		if (item.contains("/"))
			data = item.split("/");
		else
			data = new String[] {item};
		Material m = Material.getMaterial(data[0].toUpperCase().replaceAll(" ", "_"));
		ItemStack itemstack = new ItemStack(Material.AIR, 1);
		if (m != null) {
			itemstack = new ItemStack(m, 1);
		}

		try {
			addEnchantments(data, itemstack);
			addColor(data, itemstack);
		} catch (InvalidInputException exception) {
			Bukkit.getLogger().log(Level.WARNING, "Error with " + data.toString() + ": " + exception.getMessage());
		}

		return itemstack;
	}

	/**
	 * Determines whether or not data contains the item name or not,
	 * and then adds the enchantments to the item.
	 * This can accept the format item_name/enchantment*level or enchantment*level.
	 * @param data - The enchantment, with or without the item name.
	 * @param item - The ItemStack on which the enchantment will be applied.
	 * @throws InvalidInputException if the input was formatted incorrectly.
	 */
	public void addEnchantments(String[] data, ItemStack item) throws InvalidInputException {
		if (data == null || !data.toString().contains("!"))
			return;
		
		addEnchantmentsWithoutItemName(data[data.length - 1].split("!"), item);
	}
	
	/**
	 * This does the same thing as {@link #addEnchantments(String[], ItemStack)},
	 * but with a String instead of an array.
	 * @param data - The enchantment, with or without the item name.
	 * @param item - The ItemStack on which the enchantment will be applied.
	 * @throws InvalidInputException if the input is formatted incorrectly.
	 */
	public void addEnchantments(String data, ItemStack item) throws InvalidInputException {
		// If data contains the item name
		if (data.contains("/"))
			addEnchantments(data.split("/"), item);
		else
			addEnchantments(new String[] {data}, item);
	}
	
	/**
	 * Applies an array of enchantments to an item.
	 * Use this instead of {@link #addEnchantments(String[], ItemStack)} if
	 * your String array does not contain the item name because
	 * enchantNames requires the enchantments only.
	 * @param enchantNames - A string array of enchantments to be added.
	 * @param item - The item which the enchantments should be applied to.
	 * @throws InvalidInputException if the input is formatted incorrectly.
	 */
	public void addEnchantmentsWithoutItemName(String[] enchantNames, ItemStack item) throws InvalidInputException {
		for (String enchant : enchantNames) {
			addEnchantment(enchant, item);
		}
	}
	
	/**
	 * Add a slash for enchantments, and then a * to set the level.
	 * Individual enchantments are divided by explanation marks.
	 * Example: item_name/protection*1!featherfalling*2
	 * @param enc - The enchantment to be added (example: sharpness*1).
	 * @param item - The item to enchant.
	 * @throws InvalidInputException if the input is formatted incorrectly.
	 */
	public void addEnchantment(String enc, ItemStack item) throws InvalidInputException {
		String[] enchant = null;
		try {
			enchant = enc.split(Pattern.quote("*"));
		} catch (PatternSyntaxException exception) {
			throw new InvalidInputException("The input " + enc + " did not contain an enchantment level preceded by a *.");
		}
		item.addUnsafeEnchantment(getEnchantment(enchant[0]), Integer.parseInt(enchant[1]));
	}

	/**
	 * Checks for leather and adds color from with the format color1!color2!color3.
	 * Example: leather_chestplate/protection*1/255!255!255
	 * @param data - The color information, with or without the item name (item_name/color1!color2!color3 OR color1!color2!color3).
	 * @param item - The leather item to which color will be added.
	 * @throws InvalidInputException if the colors are invalid.
	 */
	public void addColor(String[] data, ItemStack item) throws InvalidInputException {
		if (!item.getType().name().contains("leather"))
			return;
		
		addColorAfterCheck(data[data.length - 1].split("!"), item);
	}

	/**
	 * This does the same thing as {@link #addColor(String[], ItemStack)}, but without an array.
	 * A String with or without the item name are acceptable.
	 * @param data - The String containing three RGB values for leather coloring.
	 * @param item - The item to color.
	 * @throws InvalidInputException if the colors are invalid.
	 */
	public void addColor(String data, ItemStack item) throws InvalidInputException {
		if (data.contains("/"))
			addColor(data.split("/"), item);
		else
			addColor(new String[] {data}, item);
	}
	
	/**
	 * Adds color to leather armor based on the three RGB colors given.
	 * @param data - An array of three RGB colors.
	 * @param item - The leather item to be colored.
	 * @throws InvalidInputException if the colors are invalid.
	 */
	public void addColorAfterCheck(String[] data, ItemStack item) throws InvalidInputException {
		ItemMeta meta = item.getItemMeta();
		LeatherArmorMeta leatherarmor = (LeatherArmorMeta) meta;
		try {
			leatherarmor.setColor(Color.fromBGR(Integer.parseInt(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2])));
		} catch (NumberFormatException exception) {
			throw new InvalidInputException("The input " + data + " contained something other than an integer where an integer was expected.");
		}
		meta = leatherarmor;
		item.setItemMeta(meta);
	}
	
	/**
	 * Turns an enchantment name into an actual Enchantment enum.
	 * @param name - The name to be translated.
	 * @return An Enchantment enumeration based on the name.
	 * @throws InvalidInputException if no enchantment is found.
	 */
	public Enchantment getEnchantment(String name) throws InvalidInputException {
		String newName = name;
		switch (newName.toLowerCase().replaceAll("_", "").replaceAll(" ", "")) {
		case "protection":
			return Enchantment.PROTECTION_ENVIRONMENTAL;
		case "fireprotection":
		case "protectionfire":
			return Enchantment.PROTECTION_FIRE;
		case "blastprotection":
		case "protectionblast":
			return Enchantment.PROTECTION_EXPLOSIONS;
		case "projectileprotection":
		case "protectionprojectile":
			return Enchantment.PROTECTION_PROJECTILE;
		case "featherfalling":
			return Enchantment.PROTECTION_FALL;
		case "respiration":
			return Enchantment.OXYGEN;
		case "aquaaffinity":
			return Enchantment.WATER_WORKER;
		case "thorns":
			return Enchantment.THORNS;
		case "sharpness":
		case "damageall":
			return Enchantment.DAMAGE_ALL;
		case "smite":
			return Enchantment.DAMAGE_UNDEAD;
		case "baneofarthropods":
			return Enchantment.DAMAGE_ARTHROPODS;
		case "knockback":
			return Enchantment.KNOCKBACK;
		case "fireaspect":
			return Enchantment.FIRE_ASPECT;
		case "looting":
			return Enchantment.LOOT_BONUS_MOBS;
		case "effeciency":
			return Enchantment.DIG_SPEED;
		case "silktouch":
			return Enchantment.SILK_TOUCH;
		case "unbreaking":
			return Enchantment.DURABILITY;
		case "fortune":
			return Enchantment.LOOT_BONUS_BLOCKS;
		case "power":
			return Enchantment.ARROW_DAMAGE;
		case "punch":
			return Enchantment.ARROW_KNOCKBACK;
		case "flame":
			return Enchantment.ARROW_FIRE;
		case "infinity":
			return Enchantment.ARROW_INFINITE;
		case "luckofthesea":
		case "luckofsea":
			return Enchantment.LUCK;
		case "lure":
			return Enchantment.LURE;
		default:
			throw new InvalidInputException("No enchantment was found for: " + name + ". Was everything spelled correctly?");
		}
	}
}