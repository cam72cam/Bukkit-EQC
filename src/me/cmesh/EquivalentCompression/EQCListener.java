package me.cmesh.EquivalentCompression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import me.cmesh.BlockUtil.LavaUtil;
import me.cmesh.BlockUtil.LiquidUtil;
import me.cmesh.BlockUtil.WaterUtil;
import me.cmesh.CraftLib.LoreUtil;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class EQCListener implements Listener, Runnable {
	
	public void startRunning() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(EquivalentCompression.Instance, this, 1, 1);
	}
	
	@EventHandler()
	public void onPlayerRightClick(PlayerInteractEvent ev) {
		ItemStack item = ev.getItem();
		
		if (StockItems.LavaCruicible().equals(item)) {
			LavaCruicibleAction(ev);
		} else if (StockItems.WaterCrystal().equals(item)) {
			WaterCrystalAction(ev);
		} else if (StockItems.BlackHole().equals(item)) {
			BlackHoleAction(ev);
		} else if (StockItems.FlightTalisman().equals(item)) {
			FlightTalismanAction(ev);
		}
		
		//Dupe bug
		/*
		if (ev.getAction() == Action.RIGHT_CLICK_AIR && ev.getPlayer().isSneaking()) {
			Location loc = ev.getPlayer().getLocation();
			loc.getWorld().dropItem(loc, item);
		}*/
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
	
	private HashMap<UUID, Entity> carrymap = new HashMap<UUID, Entity>(); 
	
	private Location playerSight(Player p) {
		Location dest = p.getLocation();
		dest = dest.add(dest.getDirection().multiply(3));
		dest = dest.add(new Vector(0,2,0));
		return dest;
	}
	
	@SuppressWarnings("deprecation")
	private void BlackHoleAction(PlayerInteractEvent ev) {
		Player p = ev.getPlayer();
		UUID key = p.getUniqueId();
		
		if (ev.getAction() == Action.RIGHT_CLICK_AIR && p.isSneaking()) {
			//Find a block at most 15 meters away
			Block b = p.getTargetBlock(null, 15);
			
			if (b.getType() != Material.AIR) {
				//Copy pasta ftw
				
				BlockBreakEvent ourbreak = new BlockBreakEvent(b, p);
				Bukkit.getServer().getPluginManager().callEvent(ourbreak);
				if (!ourbreak.isCancelled()) {
					ev.setCancelled(true);
					FallingBlock playerholding = p.getLocation().getWorld().spawnFallingBlock(playerSight(p), b.getType(), b.getData());
					carrymap.put(key, playerholding);
					b.setType(Material.AIR);
					//TODO inventory and other state
				}
			}
		}
		
		//Start move block
		if (ev.getAction() == Action.RIGHT_CLICK_BLOCK && p.isSneaking()) {
			Block b = ev.getClickedBlock();
			switch(b.getType()) {
			case PORTAL:
			case ENDER_PORTAL:
			case ENDER_PORTAL_FRAME:
			case OBSIDIAN:
				return;
			default:
			}
			
			BlockBreakEvent ourbreak = new BlockBreakEvent(b, p);
			Bukkit.getServer().getPluginManager().callEvent(ourbreak);
			if (!ourbreak.isCancelled()) {
				ev.setCancelled(true);
				FallingBlock playerholding = p.getLocation().getWorld().spawnFallingBlock(playerSight(p), b.getType(), b.getData());
				carrymap.put(key, playerholding);
				b.setType(Material.AIR);
				//TODO inventory and other state
			}
		}
		
		if (ev.getAction() == Action.LEFT_CLICK_AIR) {
			if (p.isSneaking()) {
				if (!vacuummap.containsKey(key)) {
					p.sendMessage("Atraction Ritual Activated");
					vacuummap.put(key, new ArrayList<Entity>());
				} else {
					p.sendMessage("Atraction Ritual Deactivated");
					vacuummap.remove(key);
				}
				
			} else if (carrymap.containsKey(key)) {
				//Throw entity
				ev.setCancelled(true);
				carrymap.get(key).setVelocity(p.getLocation().getDirection().multiply(2));
				carrymap.remove(key);
			}
		}
	}
	
	@EventHandler()
	public void blockFall(EntityChangeBlockEvent ev) {
		UUID toRemove = null; 
		//meh
		for(Entry<UUID, Entity> entry : carrymap.entrySet()) {
			if (entry.getValue().getEntityId() == ev.getEntity().getEntityId()) {
				toRemove = entry.getKey();
			}
		}
		
		if (toRemove != null) {
			carrymap.remove(toRemove);
		}
	}
	
	@EventHandler()
	public void BlackHoleEntityInteract(PlayerInteractEntityEvent ev) {
		ItemStack item = ev.getPlayer().getItemInHand();
		
		if (StockItems.BlackHole().equals(item) && ev.getRightClicked().getType() != EntityType.PLAYER) {
			ev.setCancelled(true);
			
			Entity e = ev.getRightClicked();
			Player p = ev.getPlayer();
			UUID key = p.getUniqueId();
			if (!carrymap.containsKey(key)) {
				carrymap.put(key, e);
				e.teleport(playerSight(p));
			}
		}
	}

	private HashMap<UUID, List<Entity>> vacuummap = new HashMap<UUID, List<Entity>>();
	@Override
	public void run() {
		for(Entry<UUID, Entity> entry : carrymap.entrySet()) {
			Player player = Bukkit.getPlayer(entry.getKey());
			Entity playerholding = entry.getValue();
			
			Location dest = playerSight(player);
			Location src = playerholding.getLocation();
			
			if (dest.getWorld().getUID() != src.getWorld().getUID()) {
				entry.getValue().teleport(dest);
				continue;
			}
			
			Vector diff = dest.subtract(src).toVector();
			
			playerholding.setVelocity(diff);
			playerholding.setFallDistance(0);
		}
		
		for(UUID id : vacuummap.keySet()) {
			Player p = Bukkit.getPlayer(id);
			
			List<Entity> neighbors = p.getNearbyEntities(5, 5, 5);
			List<Entity> already = vacuummap.get(id);
			
			//Find new entities
			for (Entity e : neighbors) {
				//make sure some other force is not tugging on this entity
				if (e.getType() == EntityType.DROPPED_ITEM && e.getVelocity().equals(new Vector(0,0,0)) && !already.contains(e)) {
					already.add(e);
				}
			}
			
			for (Entity e : already) {
				e.setVelocity(p.getLocation().add(new Vector(0,1,0)).subtract(e.getLocation()).toVector().multiply(0.1));
			}
		}
	}
	
	private List<UUID> wasAllowedFlying = new ArrayList<UUID>();
	private List<UUID> isFlying = new ArrayList<UUID>(); 

	//TODO make sure player has item when flying
	private void FlightTalismanAction(PlayerInteractEvent ev) {
		Player p = ev.getPlayer();
		if (ev.getAction() == Action.RIGHT_CLICK_AIR) {
			p.setVelocity(p.getLocation().getDirection().multiply(3));
			p.setFallDistance(0);
			//p.setAllowFlight(false);
			//p.sendMessage(p.getAllowFlight() + "");
		}
		
		UUID key = p.getUniqueId();
		//p.setAllowFlight(false);
		if (ev.getAction() == Action.LEFT_CLICK_AIR) {
			if (isFlying.contains(key)) {
				//stop flight and restore state
				p.setFlying(false);
				
				p.setAllowFlight(wasAllowedFlying.contains(key));
				if (wasAllowedFlying.contains(key)) {
					wasAllowedFlying.remove(key);
				}
				
				isFlying.remove(key);
			} else {
				if (p.getAllowFlight()) {
					wasAllowedFlying.add(p.getUniqueId());
				}
				
				p.setAllowFlight(true);
				p.teleport(p.getLocation().add(new Vector(0,0.1,0)));
				p.setFlying(true);
				isFlying.add(p.getUniqueId());
			}
		}
	}
	
	@EventHandler()
	public void flightToggle(PlayerToggleFlightEvent ev) {
		Player p = ev.getPlayer();
		UUID key = p.getUniqueId();
		//copy pasta!
		if (isFlying.contains(key)) {
			//stop flight and restore state
			p.setFlying(false);
			
			p.setAllowFlight(wasAllowedFlying.contains(key));
			if (wasAllowedFlying.contains(key)) {
				wasAllowedFlying.remove(key);
			}
			
			isFlying.remove(key);
		}
	}
	
	@EventHandler
	public void onFallDamage(EntityDamageEvent ev) {
		if (ev.getEntity().getType() == EntityType.PLAYER && ev.getCause() == DamageCause.FALL) {
			Player p = (Player)ev.getEntity();
			if (p.getInventory().contains(StockItems.FlightTalisman())) {
				ev.setCancelled(true);
			}
		}
	}
}
