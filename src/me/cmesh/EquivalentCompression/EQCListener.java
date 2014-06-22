package me.cmesh.EquivalentCompression;

import me.cmesh.BlockUtil.LavaUtil;
import me.cmesh.BlockUtil.LiquidUtil;
import me.cmesh.BlockUtil.WaterUtil;
import me.cmesh.CraftLib.LoreUtil;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
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
	
	private int suckLiquid(Block block, Integer max, LiquidUtil manager) {
		if (!manager.isType(block) || max <= 0) {
			return 0;
		}
		block.setType(Material.AIR);
		
		BlockFace[] directions = { BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.DOWN };
		int total = 0;
		for (BlockFace face : directions) {
			Block rel = block.getRelative(face);
			total += suckLiquid(rel, max - 1, manager);
		}
		return total + 1;
	}
	
	private void handleLiquidAction(PlayerInteractEvent ev, int cost, boolean clearWeather, LiquidUtil comareor) {
		ItemStack item = ev.getItem();
		Player p = ev.getPlayer();
		
		String loreKey = "Stored";
		
		if (ev.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block b = ev.getClickedBlock();
			b = b.getRelative(ev.getBlockFace());
			if (!p.isSneaking()) {
				if (comareor.isSource(b)) {
					LoreUtil.addLevel(item, loreKey, 1);
					b.breakNaturally();
				} else if ((b.getType() == Material.AIR || comareor.isType(b)) && 
							LoreUtil.getLevel(item, loreKey) > 0) {
					
					LoreUtil.addLevel(item, loreKey, -1);
					comareor.setSource(b);
				}
			} else {
				//TODO Require redstone
				if (comareor.isSource(b)) {
					LoreUtil.addLevel(item, loreKey, suckLiquid(b, 30, comareor));
				}
			}
		}
		if (ev.getAction() == Action.RIGHT_CLICK_AIR) {
			int level = LoreUtil.getLevel(item, loreKey);
			
			//Looking up
			if (level > cost && p.getLocation().getPitch() <= -88) {
				if (clearWeather) {
					Bukkit.broadcastMessage("Cloulds begin to Clear!");
					p.getWorld().setWeatherDuration(0);
					p.getWorld().setStorm(false);
				} else {
					Bukkit.broadcastMessage("Cloulds begin to Swirl!");
					p.getWorld().setStorm(true);
				}
				p.getWorld().strikeLightningEffect(p.getLocation());
				LoreUtil.addLevel(item, loreKey, -cost);
			}
		}
	}
	
	private void LavaCruicibleAction(PlayerInteractEvent ev) {
		handleLiquidAction(ev, 1000, true, new LavaUtil());
	}
	
	private void WaterCrystalAction(PlayerInteractEvent ev) {
		handleLiquidAction(ev, 10000, false, new WaterUtil());
	}
}
