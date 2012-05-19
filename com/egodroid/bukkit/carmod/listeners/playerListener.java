package com.egodroid.bukkit.carmod.listeners;

import java.util.logging.Logger;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.material.Step;
import org.bukkit.material.Wool;
import org.bukkit.util.Vector;

import com.egodroid.bukkit.carmod.CarMod;

public class playerListener implements Listener {
	private final Logger log = Logger.getLogger("Minecraft");
	private CarMod mPlugin;
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		
		if(event.hasBlock() && this.mPlugin.getConfig().getBoolean("placeMinecart")) {
			if (!this.mPlugin.getConfig().getBoolean("useWool")) {
			
				if (event.getPlayer().getItemInHand().getTypeId() == 328) {
					if (event.getClickedBlock().getTypeId() == this.mPlugin.getConfig().getInt("streetBlock") || event.getClickedBlock().getTypeId() == this.mPlugin.getConfig().getInt("motorwayBlock")) {
						Location temploc = event.getClickedBlock().getLocation();
						temploc.add(new Vector(0,1,0));
						event.getPlayer().getWorld().spawn(temploc, Minecart.class);
						event.getPlayer().getInventory().removeItem(event.getPlayer().getInventory().getItemInHand());
					}
				}
		
				} else {
					if (event.getPlayer().getItemInHand().getTypeId() == 328) {
					if (event.getClickedBlock().getTypeId() == 35) {
						Location temploc = event.getClickedBlock().getLocation();
						temploc.add(new Vector(0,1,0));
						event.getPlayer().getWorld().spawn(temploc, Minecart.class);
						event.getPlayer().getInventory().removeItem(event.getPlayer().getInventory().getItemInHand());
					}
				}
				}
			
			if (event.getPlayer().getItemInHand().getTypeId() == 328) {
			if (event.getClickedBlock().getTypeId() == 43 || event.getClickedBlock().getTypeId() == 44) {
				Step step = new Step(event.getClickedBlock().getType(), event.getClickedBlock().getData());
				if (step.getData() == (byte) this.mPlugin.getConfig().getInt("StreetStepType")) { 
					Location temploc = event.getClickedBlock().getLocation();
					temploc.add(new Vector(0,1,0));
					event.getPlayer().getWorld().spawn(temploc, Minecart.class);
					event.getPlayer().getInventory().removeItem(event.getPlayer().getInventory().getItemInHand());
				}
			}
			
			}
		}
		
	}
	
	public playerListener(CarMod pPlugin) {
		this.mPlugin = pPlugin;
	}


}
