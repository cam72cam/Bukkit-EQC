package me.cmesh.EquivalentCompression;

import java.util.ArrayList;
import java.util.List;

import me.cmesh.CraftLib.LoreUtil;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class EQCListener implements Listener {
	@EventHandler()
	public void onPlayerRightClick(PlayerInteractEvent ev) {
		ItemStack item = ev.getItem();
		
		if (StockItems.LavaCruicible().equals(item)) {
			LavaCruicibleAction(ev);
		} else if (StockItems.WaterCrystal().equals(item)) {
			WaterCrystalAction(ev);
		}
		
		//Dupe bug
		if (ev.getAction() == Action.RIGHT_CLICK_AIR && ev.getPlayer().isSneaking()) {
			Location loc = ev.getPlayer().getLocation();
			loc.getWorld().dropItem(loc, item);
		}
	}
	
	@SuppressWarnings("deprecation")
	private void LavaCruicibleAction(PlayerInteractEvent ev) {
		ItemStack item = ev.getItem();
		
		if (ev.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block b = ev.getClickedBlock();
			b = b.getRelative(ev.getBlockFace());
			
			List<Material> lavaTypes = new ArrayList<Material>();
			lavaTypes.add(Material.LAVA);
			lavaTypes.add(Material.STATIONARY_LAVA);
			
			if (lavaTypes.contains(b.getType()) && b.getData() == 0) {
				LoreUtil.addLevel(item, "Stored", 1);
				b.breakNaturally();
			} else if ((b.getType() == Material.AIR || lavaTypes.contains(b.getType())) && 
						LoreUtil.getLevel(item, "Stored") > 0) {
				
				LoreUtil.addLevel(item, "Stored", -1);
				b.setType(Material.LAVA);
				b.setData((byte) 0);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	private void WaterCrystalAction(PlayerInteractEvent ev) {
		ItemStack item = ev.getItem();
		
		if (ev.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block b = ev.getClickedBlock();
			b = b.getRelative(ev.getBlockFace());
			
			List<Material> waterTypes = new ArrayList<Material>();
			waterTypes.add(Material.WATER);
			waterTypes.add(Material.STATIONARY_WATER);
			
			if (waterTypes.contains(b.getType()) && b.getData() == 0) {
				LoreUtil.addLevel(item, "Stored", 1);
				b.breakNaturally();
			} else if ((b.getType() == Material.AIR || waterTypes.contains(b.getType())) && 
						LoreUtil.getLevel(item, "Stored") > 0) {
				
				LoreUtil.addLevel(item, "Stored", -1);
				b.setType(Material.WATER);
				b.setData((byte) 0);
			}
		}
	}
}
