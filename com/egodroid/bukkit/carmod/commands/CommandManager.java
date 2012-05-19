package com.egodroid.bukkit.carmod.commands;

import java.io.IOException;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.egodroid.bukkit.carmod.CarMod;
import com.egodroid.bukkit.carmod.listeners.minecartListener;
import com.egodroid.bukkit.carmod.util.FuelManager;

public class CommandManager implements CommandExecutor {
	
	private minecartListener mML;
	private FuelManager mFM;
	private CarMod mPlugin;
	private static Permission permissions = null;
	
	
	public CommandManager (CarMod pPlugin, FuelManager pFM, minecartListener pML) {
		 this.mFM = pFM;
		 this.mML = pML;
		 this.mPlugin = pPlugin;
		 this.permissions = this.mML.permission;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2,
			String[] arg3) {
		
        if (arg3.length != 0) {
        	String commandarg = arg3[0] ;
            if (this.permissions.has(sender, "minecars.useCommands")) {
                if (commandarg.equals("1")) {
             	   sender.sendMessage(" Gear 1 enabled! ");
             	   mML.setSpeedMultiplier(3, (Player) sender);
             	  return true;
                }
                
                if(commandarg == "") {
                	sender.sendMessage(String.format("[%s] - You have to use a subcommand for /mcg ", this.mPlugin.getDescription().getName()));
                	return true;
                }
                
                if (commandarg.equals("2")) {
             	   sender.sendMessage(" Gear 2 enabled! ");
             	   mML.setSpeedMultiplier(5, (Player) sender);
             	  return true;
                }
                if (commandarg.equals("3")) {
             	   sender.sendMessage(" Gear 3 enabled! ");
             	   mML.setSpeedMultiplier(8, (Player) sender);  
             	  return true;
                }    
                
                if(commandarg.equals("fuel")) {
             	   
             	   sender.sendMessage( mFM.getProgressBar( (Player) sender));
             	  return true;
                }
                
                if(commandarg.equals("buy")) {
             	   boolean sucess = mFM.buyFuel( (Player) sender);
             	   if (sucess) {
             		   this.mML.canMove = true;
             	   }
             	   return true;
                }
                
                
                if(commandarg.equals("adminfuel") && this.permissions.has(sender, "minecars.admin")) {
                	this.mFM.doNewFuel((Player) sender);
                	this.mML.canMove = true;
                }
                
                if(commandarg.equals("reload") &&  this.permissions.has(sender, "minecars.admin.reload")) {
                	this.mPlugin.reloadConfig();
                	try {
						this.mPlugin.configAll();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                	sender.sendMessage(String.format("[%s] - Sucessfully reloaded config!", this.mPlugin.getDescription().getName()));

                
                }
                
                if(commandarg.equals("prices")) {
                	
                	String tempString = ChatColor.GREEN + String.format("[%s] - One Fuel load costs: ", this.mPlugin.getDescription().getName());
                	if (this.mPlugin.getConfig().getBoolean("UseEconomy")) {
                		tempString = tempString + this.mPlugin.getConfig().getString("priceperfuel") + " " + this.mFM.economy.currencyNamePlural();		
                	} else {
                		tempString = tempString + this.mPlugin.getConfig().getString("itemsperfuel") + " " + Material.getMaterial(this.mPlugin.getConfig().getInt("fuelitemid")).name();
                	}
                	sender.sendMessage(tempString);
                	
                	return true;
                }
        } else {
        	sender.sendMessage(String.format("[%s] - You have no permission to use this Command!", this.mPlugin.getDescription().getName()));
        	return false ;
        }
        
        } else
        	sender.sendMessage(String.format("[%s] - You have to use a subcommand!", this.mPlugin.getDescription().getName()));
         return false;
		
	}
	



}
