package com.egodroid.bukkit.carmod.listeners;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.egodroid.bukkit.carmod.CarMod;
import com.egodroid.bukkit.carmod.util.FuelManager;

public class signListener implements Listener {
	
	
	private CarMod mPlugin;
	private boolean useEconomy = false;
	private minecartListener mML;
	private final Logger log = Logger.getLogger("Minecraft");
	private static Permission permissions = null;
	private FuelManager mFM;
	private File inputFile, tempFile;
	
	
	
	
	
public signListener(CarMod pPlugin, minecartListener pML, FuelManager pFM) throws IOException {
	this.mPlugin = pPlugin;
	this.mML = pML;
	this.mFM = pFM;
	this.permissions = this.mML.permission;
	this.inputFile = new File("plugins/MineCars/signs.txt");
	this.tempFile = new File("plugins/MineCars/signs_temp.txt");
	if(!this.inputFile.exists())
		this.inputFile.createNewFile();
	if(!this.tempFile.exists())
		this.tempFile.createNewFile();
	
	this.loadSigns();
	
}

@EventHandler
public void onSignChange(SignChangeEvent event) throws IOException {
	if (this.mPlugin.getConfig().getBoolean("UseFuelSystem")) {
		String[] templines = event.getLines();
		if(templines[0].equalsIgnoreCase("Fuel Station")) {
			if (this.permissions.has(event.getPlayer(), "minecars.fuelstation.create")) {
	      String temploc = Double.toString(event.getBlock().getLocation().getX()) + ";" + Double.toString(event.getBlock().getLocation().getY()) + ";" + Double.toString(event.getBlock().getLocation().getZ()) + ";" + event.getBlock().getWorld().getName(); 
		  this.addSign(event.getBlock().getLocation(), event.getBlock().getWorld().getName());
		  
		  templines[0] = ChatColor.RED + "Fuel Station";
		  templines[1] = " Costs: ";
		  
		  if (this.useEconomy) {
			  templines[2] = new Integer(this.mPlugin.getConfig().getInt("priceperfuel")).toString() + " " + FuelManager.economy.currencyNamePlural();
		  } else {
			  templines[2] = new Integer(this.mPlugin.getConfig().getInt("itemsperfuel")).toString() + " " + Material.getMaterial(this.mPlugin.getConfig().getInt("fuelitemid")).name();
		  }
		  
		  event.getPlayer().sendMessage(String.format("[%s] - Fuel Station sucessfully registered!", this.mPlugin.getDescription().getName()));
			} else {
				event.getPlayer().sendMessage(String.format("[%s] - You don't have the permission to create a Fuel Station!", this.mPlugin.getDescription().getName()));
				event.setLine(0, "");
			}
		}
	}
	}

@EventHandler
public void onBlockBreak(BlockBreakEvent event) throws IOException {
	if (this.mPlugin.getConfig().getBoolean("UseFuelSystem")) {
	if(event.getBlock().getType() == Material.WALL_SIGN || event.getBlock().getType() == Material.SIGN_POST || event.getBlock().getType() == Material.SIGN) {
		Sign sign = (Sign) event.getBlock().getState();
		if (sign.getLine(0).equalsIgnoreCase(ChatColor.RED + "Fuel Station")) {
			if (this.permissions.has(event.getPlayer(), "minecars.fuelstation.destroy")) {
				String temploc = Double.toString(event.getBlock().getLocation().getX()) + ";" + Double.toString(event.getBlock().getLocation().getY()) + ";" + Double.toString(event.getBlock().getLocation().getZ()) + ";" + event.getBlock().getWorld().getName(); 
				this.destroySign(temploc);
				event.getPlayer().sendMessage(String.format("[%s] - Fuel Station sucessfully unregistered!", this.mPlugin.getDescription().getName()));
			} else {
				event.getPlayer().sendMessage(String.format("[%s] - You don't have the permission to create a Fuel Station!", this.mPlugin.getDescription().getName()));
				event.setCancelled(true);
			}
	      }
		}
	}
}

@EventHandler
public void onBlockPlace(BlockPlaceEvent event) throws IOException {
	
}




@EventHandler
public void onPlayerInteract (PlayerInteractEvent event) {
	if (event.hasBlock()) {
		if (event.getClickedBlock().getType()  == Material.WALL_SIGN || event.getClickedBlock().getType() == Material.SIGN_POST || event.getClickedBlock().getType() == Material.SIGN) {
			
			Sign sign = (Sign) event.getClickedBlock().getState();
			if (sign.getLine(0).equalsIgnoreCase(ChatColor.RED + "Fuel Station")) {
				
				if (this.permissions.has(event.getPlayer(), "minecars.fuelstation.use"))
					this.mFM.buyFuel(event.getPlayer());
				else 
					event.getPlayer().sendMessage(String.format("[%s] - You don't have Permissions to buy Fuel over here!", this.mPlugin.getDescription().getName()));
			} 
		}
	}
}



public void loadSigns() throws IOException {
	 
	  BufferedReader br = new BufferedReader(new FileReader(this.inputFile));	  
	  String strLine;
	  while ((strLine = br.readLine()) != null)   {
		  
		  String[] tempcord = strLine.split(";");
		  Sign tempsign = (Sign) this.mPlugin.getServer().getWorld(tempcord[3]).getBlockAt(new Location(this.mPlugin.getServer().getWorld(tempcord[3]), new Double(tempcord[0]), new Double(tempcord[1]), new Double(tempcord[2]))).getState();
		 
		  String[] lines = tempsign.getLines();
		  lines[0] = ChatColor.RED + "Fuel Station";
		
		  lines[1] = " Costs: ";
		  if (this.useEconomy) {
			  lines[2] = new Integer(this.mPlugin.getConfig().getInt("priceperfuel")).toString() + " " + FuelManager.economy.currencyNamePlural();
		  } else {
			  lines[2] = new Integer(this.mPlugin.getConfig().getInt("itemsperfuel")).toString() + " " + Material.getMaterial(this.mPlugin.getConfig().getInt("fuelitemid")).name();
		  }
		
		  tempsign.update();
		  
		  
		  
	  }
	  
	  br.close();

	  
	
	  
}

private void destroySign(String pLoctoRemove) throws IOException {
	 /* FileInputStream fstream = new FileInputStream("signs.txt");
	  FileWriter fwrt = new FileWriter("signs_temp.txt", true);
	  BufferedWriter out = new BufferedWriter(fwrt);
	  DataInputStream in = new DataInputStream(fstream);
	  BufferedReader br = new BufferedReader(new InputStreamReader(in));
	  String strLine;
	  while ((strLine = br.readLine()) != null)   {
		  if(strLine.equalsIgnoreCase(pLoctoRemove)) {
			  continue;
		  } else {
			  out.write(strLine);
			  out.newLine();
		  }	  
	  }
	  in.close(); */
	  
	  

	  

	  BufferedReader reader = new BufferedReader(new FileReader(this.inputFile));
	  BufferedWriter writer = new BufferedWriter(new FileWriter(this.tempFile, true));

	  String lineToRemove = pLoctoRemove;
	  String currentLine;

	  while((currentLine = reader.readLine()) != null) {
	      // trim newline when comparing with lineToRemove
	      String trimmedLine = currentLine.trim();

	      if(trimmedLine.equalsIgnoreCase(lineToRemove)) {

	    	  continue;
	      }
	      
	      writer.write(currentLine);
	      writer.flush();
	      writer.newLine();
	  }
	  writer.close();
	  reader.close();

	  this.inputFile.delete();
	  
	  boolean successful = this.tempFile.renameTo(this.inputFile);

}

private void addSign(Location pLoc, String pWorldName) throws IOException {
	  BufferedWriter out = new BufferedWriter(new FileWriter(this.inputFile, true));
	  
	  out.write(Double.toString(pLoc.getX()) + ";" +Double.toString(pLoc.getY()) + ";" + Double.toString(pLoc.getZ()) + ";" + pWorldName);
	  out.newLine();
	  out.close();
	  
}

public void useEconomy(boolean pValue) {
	this.useEconomy = pValue;
}
	
}
