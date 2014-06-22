package me.cmesh.EquivalentCompression;

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
	
	@SuppressWarnings("deprecation")
	private boolean isWaterBlock(Block b) {
		return (b.getType() == Material.WATER || b.getType() == Material.STATIONARY_WATER) && b.getData() == 0;
	}
	@SuppressWarnings("deprecation")
	private boolean isLavaBlock(Block b) {
		return (b.getType() == Material.LAVA || b.getType() == Material.STATIONARY_LAVA) && b.getData() == 0;
	}
	
	//because java
	//I really hate java
	private interface liquitemp {
		public boolean isLiquid(Block b);
	}
	
	private int suckLiquid(Block block, int max, liquitemp compareor) {
		if (!compareor.isLiquid(block) || max <= 0) {
			return 0;
		}
		block.setType(Material.AIR);
		
		BlockFace[] directions = { BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.DOWN };
		int total = 0;
		for (BlockFace face : directions) {
			Block rel = block.getRelative(face);
			total += suckLiquid(rel, max - 1, compareor);
		}
		return total + 1;
	}
	
	@SuppressWarnings("deprecation")
	private void LavaCruicibleAction(PlayerInteractEvent ev) {
		ItemStack item = ev.getItem();
		Player p = ev.getPlayer();
		
		if (ev.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block b = ev.getClickedBlock();
			b = b.getRelative(ev.getBlockFace());
			if (!p.isSneaking()) {
				if (isLavaBlock(b)) {
					LoreUtil.addLevel(item, "Stored", 1);
					b.breakNaturally();
				} else if ((b.getType() == Material.AIR || (b.getType() == Material.LAVA || b.getType() == Material.STATIONARY_LAVA)) && 
							LoreUtil.getLevel(item, "Stored") > 0) {
					
					LoreUtil.addLevel(item, "Stored", -1);
					b.setType(Material.LAVA);
					b.setData((byte) 0);
				}
			} else {
				//TODO Require redstone
				if (isLavaBlock(b)) {
					LoreUtil.addLevel(item, "Stored", suckLiquid(b, 30, new liquitemp() {
						@Override
						public boolean isLiquid(Block b) {
							return isLavaBlock(b);
						}  }));
				}
			}
		}
		if (ev.getAction() == Action.RIGHT_CLICK_AIR) {
			int level = LoreUtil.getLevel(item, "Stored");
			
			//Looking up
			if (level > 1000 && p.getLocation().getPitch() <= -88) {
				p.getWorld().strikeLightningEffect(p.getLocation());
				Bukkit.broadcastMessage("Cloulds begin to Clear!");
				p.getWorld().setWeatherDuration(0);
				p.getWorld().setStorm(false);
				LoreUtil.addLevel(item, "Stored", -1000);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	private void WaterCrystalAction(PlayerInteractEvent ev) {
		ItemStack item = ev.getItem();
		Player p = ev.getPlayer();
		
		if (ev.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block b = ev.getClickedBlock();
			b = b.getRelative(ev.getBlockFace());
			
			if (!p.isSneaking()) {
				if (isWaterBlock(b)) {
					LoreUtil.addLevel(item, "Stored", 1);
					b.breakNaturally();
				} else if ((b.getType() == Material.AIR || (b.getType() == Material.WATER || b.getType() == Material.STATIONARY_WATER)) && 
							LoreUtil.getLevel(item, "Stored") > 0) {
					
					LoreUtil.addLevel(item, "Stored", -1);
					b.setType(Material.WATER);
					b.setData((byte) 0);
				}
			} else {
				//TODO Require redstone
				if (isWaterBlock(b)) {
					LoreUtil.addLevel(item, "Stored", suckLiquid(b, 30, new liquitemp() {
						@Override
						public boolean isLiquid(Block b) {
							return isWaterBlock(b);
						}  }));
				}
			}
		}
		
		if (ev.getAction() == Action.RIGHT_CLICK_AIR) {
			int level = LoreUtil.getLevel(item, "Stored");
			p.sendMessage(p.getLocation().getPitch() + "");
			//Looking up
			if (level > 10000 && p.getLocation().getPitch() <= -88) {
				Bukkit.broadcastMessage("Cloulds begin to Swirl!");
				p.getWorld().setStorm(true);
				p.getWorld().strikeLightningEffect(p.getLocation());
				LoreUtil.addLevel(item, "Stored", -10000);
			}
		}
	}
}
