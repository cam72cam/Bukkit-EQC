package me.cmesh.EquivalentCompression;

import java.util.*;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SpecialItemStack extends ItemStack {
	public SpecialItemStack(Material m) {
		super(m);
	}
	
	public SpecialItemStack(Material m, int ammount) {
		super(m, ammount);
	}
	
	//item is a superset of us
	public boolean equals(Object o) {
		if (!(o instanceof ItemStack)) {
			return false;
		}
		
		ItemStack item = (ItemStack)o;
		
		if (item.getType() == this.getType()) {
			
			//Check Lore superset
			List<String> mylore = this.getItemMeta().getLore();
			if (mylore != null) {
				List<String> otherlore = item.getItemMeta().getLore();
				if (otherlore == null) {
					return false;
				}
				for (String s : mylore) {
					if (!otherlore.contains(s)) {
						return false;
					}
				}
			}
			return (this.getEnchantments().equals(item.getEnchantments()) && 
					this.getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName()) &&
					this.getDurability() == item.getDurability() &&
					this.getData().equals(item.getData()));
		}
		return false;
	}
}
