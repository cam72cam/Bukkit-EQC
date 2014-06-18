package me.cmesh.EquivalentCompression;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class StockItems {
	private StockItems() { }
	
	private static void addLore(ItemStack item, String str) {
		ItemMeta data = item.getItemMeta();
		List<String> lore = data.getLore();
		
		if (lore == null) {
			lore = new ArrayList<String>();
		}
		lore.add(str);
		data.setLore(lore);
		item.setItemMeta(data);
	}
	
	private static void setDisplayName(ItemStack item, String name, ChatColor color) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(color + name);
		item.setItemMeta(meta);
	}
	
	public static SpecialItemStack BaseItem() {
		SpecialItemStack item = new SpecialItemStack(Material.MAGMA_CREAM);
		addLore(item, ChatColor.GOLD + "The Key");
		setDisplayName(item, "Base Stone", ChatColor.RED);
		item.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
		return item;
	}
	
	private static ChatColor diskColor = ChatColor.GOLD;
	
	public static SpecialItemStack BaseDisk() {
		SpecialItemStack item = new SpecialItemStack(Material.RECORD_11);
		addLore(item, ChatColor.GOLD + "A sign of things to come");
		setDisplayName(item, "Base Disk", diskColor);
		return item;
	}
	
	public static SpecialItemStack LavaCruicible() {
		SpecialItemStack item = new SpecialItemStack(Material.RECORD_4);
		addLore(item, ChatColor.RED + "Burns a hole in your inventory");
		setDisplayName(item, "Lava Cruicible", diskColor);
		return item;
	}
	
	public static SpecialItemStack WaterCrystal() {
		SpecialItemStack item = new SpecialItemStack(Material.RECORD_12);
		addLore(item, ChatColor.BLUE + "Makes you feel moist");
		setDisplayName(item, "Water Crystal", diskColor);
		return (SpecialItemStack) item;
	}
	
	public static SpecialItemStack BlackHole() {
		SpecialItemStack item = new SpecialItemStack(Material.RECORD_8);
		addLore(item, ChatColor.DARK_BLUE + "A great and terrible energy");
		setDisplayName(item, "Black Hole Rune", diskColor);
		return item;
	}
}
