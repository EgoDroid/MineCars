package com.egodroid.bukkit.carmod.listeners;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Step;
import org.bukkit.util.Vector;

import com.egodroid.bukkit.carmod.CarMod;

public class playerListener implements Listener {

	private CarMod mPlugin;
	private minecartListener mML;
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		
		Player p = event.getPlayer();
		Block block = event.getClickedBlock();
		
		if(event.hasBlock() && this.mPlugin.getConfig().getBoolean("placeMinecart")) {
			if (!this.mPlugin.getConfig().getBoolean("useWool")) {
			
				if (p.getItemInHand().getTypeId() == 328) {
					if (block.getTypeId() == this.mPlugin.getConfig().getInt("streetBlock") || block.getTypeId() == this.mPlugin.getConfig().getInt("motorwayBlock")) {
						if(!CarMod.permission.has(p, "minecars.create")){
							return;
						}
						Location temploc = event.getClickedBlock().getLocation();
						temploc.add(new Vector(0,1,0));
						Minecart m = p.getWorld().spawn(temploc, Minecart.class);
						p.getInventory().removeItem(event.getPlayer().getInventory().getItemInHand());
						mML.mineCars.put(p.getName(), m);
					}
				}
		
			}else{
				if (p.getItemInHand().getTypeId() == 328) {
					if (block.getTypeId() == 35) {
						if(!CarMod.permission.has(p, "minecars.create")){
							return;
						}
						Location temploc = block.getLocation();
						temploc.add(new Vector(0,1,0));
						Minecart m = p.getWorld().spawn(temploc, Minecart.class);
						p.getInventory().removeItem(event.getPlayer().getInventory().getItemInHand());
						mML.mineCars.put(p.getName(), m);
					}
				}
			}
			
			if (p.getItemInHand().getTypeId() == 328) {
				if (block.getTypeId() == 43 || block.getTypeId() == 44) {
					Step step = new Step(block.getType(), block.getData());
					if (step.getData() == (byte) this.mPlugin.getConfig().getInt("StreetStepType")) { 
						if(!CarMod.permission.has(p, "minecars.create")){
							return;
						}
						Location temploc = event.getClickedBlock().getLocation();
						temploc.add(new Vector(0,1,0));
						Minecart m = p.getWorld().spawn(temploc, Minecart.class);
						p.getInventory().removeItem(event.getPlayer().getInventory().getItemInHand());
						mML.mineCars.put(p.getName(), m);
					}
				}
			}
		}
		
	}
	
	public playerListener(CarMod pPlugin, minecartListener pML) {
		this.mPlugin = pPlugin;
		this.mML = pML;
	}


}
