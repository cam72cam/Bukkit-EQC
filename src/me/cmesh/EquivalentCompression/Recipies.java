package me.cmesh.EquivalentCompression;

import me.cmesh.CraftLib.AdvancedShapedRecipe;
import me.cmesh.CraftLib.AdvancedShapelessRecipe;
import me.cmesh.CraftLib.CraftUtil;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Recipies {
	public static boolean registerBaseItem() {
		ItemStack item = StockItems.BaseItem();
		
		AdvancedShapedRecipe first = new AdvancedShapedRecipe(item, EquivalentCompression.Instance);
		
		Material firstMat = Material.GLOWSTONE;
		Material secondMat = Material.DIAMOND;
		Material middleMat = Material.MAGMA_CREAM;
		
		CraftUtil.SetupShapedRecipe3x3(first, 
				firstMat,  secondMat, firstMat,
				secondMat, middleMat, secondMat,
				firstMat,  secondMat, firstMat);
		
		first.requireNone(firstMat);
		first.requireNone(secondMat);
		first.requireNone(middleMat);
		
		return Bukkit.getServer().addRecipe(first);
	}
	
	public static AdvancedShapelessRecipe shapelessRecipeUsingBase(ItemStack item) {
		AdvancedShapelessRecipe r = new AdvancedShapelessRecipe(item, EquivalentCompression.Instance);
		
		ItemStack ingredient = StockItems.BaseItem();
		
		r.addIngredient(ingredient.getType());
		r.requireEnchant(ingredient.getType(), ingredient.getEnchantments());
		r.requireLore(ingredient.getType(), ingredient.getItemMeta().getLore());
		r.infiniteItem(ingredient);
		
		return r;
	}
	//TODO cleaner
	public static AdvancedShapedRecipe shapedRecipeUsingBase(ItemStack item) {
		AdvancedShapedRecipe r = new AdvancedShapedRecipe(item, EquivalentCompression.Instance);
		
		ItemStack ingredient = StockItems.BaseItem();
		//r.setIngredient('e', ingredient.getType()); //TODO HACKS
		//r.addIngredient(ingredient.getType());
		r.requireEnchant(ingredient.getType(), ingredient.getEnchantments());
		r.requireLore(ingredient.getType(), ingredient.getItemMeta().getLore());
		r.infiniteItem(ingredient);
		
		return r;
	}
	
	public static boolean registerEquiv(Material out, int out_count, Material in, int in_count) {
		AdvancedShapelessRecipe r =  shapelessRecipeUsingBase(new ItemStack(out, out_count));
		r.addIngredient(in_count, in);
		boolean success = Bukkit.getServer().addRecipe(r);
		
		r =  shapelessRecipeUsingBase(new ItemStack(in, in_count));
		r.addIngredient(out_count, out);
		success = success & Bukkit.getServer().addRecipe(r);
		return success;
	}
	
	public static boolean registerBaseDisk() {
		AdvancedShapedRecipe r = shapedRecipeUsingBase(StockItems.BaseDisk());
		CraftUtil.SetupShapedRecipe3x3(r, 
				Material.OBSIDIAN, Material.OBSIDIAN, Material.OBSIDIAN,
				Material.DIAMOND_BLOCK, StockItems.BaseItem().getType(), Material.DIAMOND_BLOCK,
				Material.OBSIDIAN, Material.OBSIDIAN, Material.OBSIDIAN);
		
		r.requireNone(Material.OBSIDIAN);
		r.requireNone(Material.DIAMOND_BLOCK);
		return Bukkit.getServer().addRecipe(r);
	}
	
	private static AdvancedShapedRecipe stdDiskRecipe(ItemStack res, Material base) {
		AdvancedShapedRecipe r = shapedRecipeUsingBase(res);
		CraftUtil.SetupShapedRecipe3x3(r, 
				Material.OBSIDIAN, StockItems.BaseDisk().getType(), Material.OBSIDIAN,
				base, StockItems.BaseItem().getType(), base,
				Material.OBSIDIAN, StockItems.BaseDisk().getType(), Material.OBSIDIAN);
		
		r.requireNone(Material.OBSIDIAN);
		r.requireNone(base);
		r.requireLore(StockItems.BaseDisk().getType(), StockItems.BaseDisk().getItemMeta().getLore());
		return r;
	}
	
	public static boolean registerAll() {
		return  registerBaseItem() &
				registerTransmogrifications() &
				registerBaseDisk() &
				registerWaterCrystal() &
				registerLavaCruicible() &
				registerBlackHole();
	}
	
	public static boolean registerTransmogrifications() {
		return 	registerEquiv(Material.IRON_INGOT, 4, Material.GOLD_INGOT, 1) &
				registerEquiv(Material.GOLD_INGOT, 4, Material.DIAMOND, 1) &
		
				registerEquiv(Material.LAPIS_BLOCK, 4, Material.REDSTONE_BLOCK, 1) &
				registerEquiv(Material.GLOWSTONE_DUST, 1, Material.REDSTONE, 8) &
				
				registerEquiv(Material.BONE, 1, Material.ROTTEN_FLESH, 4) &
				registerEquiv(Material.SULPHUR, 1, Material.BONE, 4) &
				registerEquiv(Material.ENDER_PEARL, 1, Material.SULPHUR, 8) &
				registerEquiv(Material.BLAZE_ROD, 1, Material.ENDER_PEARL, 2) &
				
				
				registerEquiv(Material.RED_MUSHROOM, 1, Material.BROWN_MUSHROOM, 1);
	}
	
	public static boolean registerWaterCrystal() {
		return Bukkit.getServer().addRecipe(stdDiskRecipe(StockItems.WaterCrystal(), Material.WATER_BUCKET));
	}
	
	public static boolean registerLavaCruicible() {
		return Bukkit.getServer().addRecipe(stdDiskRecipe(StockItems.LavaCruicible(), Material.LAVA_BUCKET));
	}
	
	public static boolean registerBlackHole() {
		return Bukkit.getServer().addRecipe(stdDiskRecipe(StockItems.BlackHole(), Material.ENDER_PEARL));
	}
}
