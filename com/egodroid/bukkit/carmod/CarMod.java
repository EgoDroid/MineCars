/*
 * MineCars V.1.2
 * by Rene Ahlsdorf // Reshka94
 * All Rights reserved.
 * EGODROID
 * 
 * 
 * CLEANED
 */

package com.egodroid.bukkit.carmod;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;


import javax.persistence.PersistenceException;


import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;

import org.bukkit.plugin.java.JavaPlugin;

import com.egodroid.bukkit.carmod.commands.CommandManager;
import com.egodroid.bukkit.carmod.database.EbeanDB;
import com.egodroid.bukkit.carmod.listeners.minecartListener;
import com.egodroid.bukkit.carmod.listeners.playerListener;
import com.egodroid.bukkit.carmod.listeners.signListener;

import com.egodroid.bukkit.carmod.util.FuelManager;

public class CarMod extends JavaPlugin  {
	
	private final Logger log = Logger.getLogger("Minecraft");
	private minecartListener mML;
	private FuelManager mFM;
	private playerListener mPL;
	private signListener mSL;
	public static Permission permission = null;
	public static Economy economy = null;

	
	
	public void onDisable() {

		//Disable PlugIn
		PluginManager pm = getServer().getPluginManager();
		PluginDescriptionFile pdfFile = getDescription();
		this.log.info("[" + pdfFile.getName() + "] Version " + pdfFile.getVersion() + " is now disabled.");
		pm.disablePlugin(this);
	
	}
	
	public void onEnable() {
		//PluginManager & Configuration
	    PluginManager pm = getServer().getPluginManager();
	    PluginDescriptionFile pdfFile = getDescription();
	    getConfig().options().copyDefaults(true);
	    saveConfig();
	    //Database Setup
	    this.setupDatabase();
	    
	    //Utilities-Init
	    
        //Vault setups
		setupEconomy();
		setupPermissions();
		log.info(permission+"");
	    
	    this.mFM = new FuelManager(this);
	    this.mML = new minecartListener(this, this.mFM);
	    this.mPL = new playerListener(this, this.mML);
	    try {
			this.mSL = new signListener(this, this.mML, this.mFM);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
       
        // Event Registration
	    pm.registerEvents(this.mML, this);
	    pm.registerEvents(this.mFM, this);
	    pm.registerEvents(this.mPL, this);
	    pm.registerEvents(this.mSL, this);
	    
	    //Config Setup
	    try {
			this.configAll();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	    
	    //Command Executor
        this.getCommand("mcg").setExecutor(new CommandManager(this, this.mFM, this.mML));
        
        //Finally enabled
	    this.log.info("[" + pdfFile.getName() + "] Version " + pdfFile.getVersion() + " is now enabled.");
	}

	  public List<Class<?>> getDatabaseClasses()
	  {
	    List list = new ArrayList();
	    list.add(EbeanDB.class);
	    return list;
	  }
	  
	  public void configAll() throws IOException {
	        //Detect, whether Vault is installed
	        if(getServer().getPluginManager().getPlugin("Vault") != null) {  
	        	 log.info(String.format("[%s] - Found Vault! Can use Economy Support!", getDescription().getName()));
	            this.mFM.setEconomy(getConfig().getBoolean("UseEconomy"));   
	            this.mSL.useEconomy(getConfig().getBoolean("UseEconomy"));
	         } else {
	        	log.info(String.format("[%s] - Disabled Economy Support due to no Vault dependency found! Using Items for Fuel.", getDescription().getName()));
	            this.mFM.setEconomy(false);
	            this.mSL.useEconomy(false);
	            
	            
	        }
		    //Config Speed Multiplier for Listener
		    this.mML.setSpeedFactors(getConfig().getInt("street-speedfactor") , getConfig().getInt("motorway-speedfactor") );
		    this.mSL.setUseLicense(getConfig().getBoolean("UseLicense"));
		    this.mSL.setLicenseCost(getConfig().getInt("LicenseCost"));
		    this.mSL.loadSigns();
		    this.mML.setupConfig();
		    this.mFM.setupConfig();
	  }
    
	  private void setupDatabase() {
		    try {
		      getDatabase().find(EbeanDB.class).findRowCount();
		    } catch (PersistenceException ex) {
		      this.log.info(new StringBuilder().append("Installing persistence database for ").append(getDescription().getName()).append(" due to first time usage").toString());
		      installDDL();
		    }
		  }
	  
	  private boolean setupEconomy(){
	        RegisteredServiceProvider<Economy> economyProvider = this.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
	        if (economyProvider != null) {
	            economy = economyProvider.getProvider();
	        }

	        return (economy != null);
	    }
	  
	  public boolean setupPermissions(){
		  
	      RegisteredServiceProvider<Permission> permissionProvider = this.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
	      if (permissionProvider != null) {
	          permission = permissionProvider.getProvider();
	      }
	      return (permission != null);
	  }
	  
/*	  private void checkVault(PluginManager pm){
		  Plugin p = pm.getPlugin("Vault");
	      if (p != null) {
	    	  return;
	      }else{
	    	  log.info("[MineCars] Vault is required for MineCars, Install Vault.");
	    	  log.info("[MineCars] Disabling MineCars");
	    	  this.setEnabled(false);
	        }
	    }
	    */
}

