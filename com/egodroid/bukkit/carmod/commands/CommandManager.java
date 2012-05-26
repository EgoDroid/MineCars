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
	private ChatColor green = ChatColor.DARK_GREEN;
	private ChatColor white = ChatColor.WHITE;
	
	
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
                	if(mML.getSpeedMultiplier((Player)sender)==3){
                		sender.sendMessage(green+"[MineCars]"+white+" You Are Already In This Gear.");
                		return true;
                	}
             	   sender.sendMessage(green+"[MineCars]"+white+" Gear 1 enabled! ");
             	   mML.setSpeedMultiplier(3, (Player) sender);
             	  return true;
                }
                
                if(commandarg == "") {
                	sender.sendMessage(green + "======= MineCars v" + mPlugin.getDescription().getVersion() + " =======");
                	sender.sendMessage(green + "/mcg"+white+" 1,2,3");
                	sender.sendMessage(green + "/mcg"+white+" fuel");
                	sender.sendMessage(green + "/mcg"+white+" prices");
                	sender.sendMessage(green + "/mcg"+white+" buy");
                	if(this.permissions.has(sender, "minecars.admin")){
                		sender.sendMessage(green + "/mcg"+white+" adminfuel");
                	}
                	if(this.permissions.has(sender, "minecars.admin")||this.permissions.has(sender, "minecars.admin.reload")){
                		sender.sendMessage(green + "/mcg"+white+" reload");
                	}
                	return true;
                }
                
                if (commandarg.equals("2")) {
                	if(mML.getSpeedMultiplier((Player)sender)==5){
                		sender.sendMessage(green+"[MineCars]"+white+" You Are Already In This Gear.");
                		return true;
                	}
             	   sender.sendMessage(green+"[MineCars]"+white+" Gear 2 enabled! ");
             	   mML.setSpeedMultiplier(5, (Player) sender);
             	  return true;
                }
                if (commandarg.equals("3")) {
                	if(mML.getSpeedMultiplier((Player)sender)==8){
                		sender.sendMessage(green+"[MineCars]"+white+" You Are Already In This Gear.");
                		return true;
                	}
             	   sender.sendMessage(green+"[MineCars]"+white+" Gear 3 enabled! ");
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
                if(commandarg.equals("?")||commandarg.equals("help")){
                	sender.sendMessage(green + "======= MineCars v" + mPlugin.getDescription().getVersion() + " =======");
                	sender.sendMessage(green + "/mcg"+white+" 1,2,3");
                	sender.sendMessage(green + "/mcg"+white+" fuel");
                	sender.sendMessage(green + "/mcg"+white+" prices");
                	sender.sendMessage(green + "/mcg"+white+" buy");
                	if(this.permissions.has(sender, "minecars.admin")){
                		sender.sendMessage(green + "/mcg"+white+" adminfuel");
                	}
                	if(this.permissions.has(sender, "minecars.admin")||this.permissions.has(sender, "minecars.admin.reload")){
                		sender.sendMessage(green + "/mcg"+white+" reload");
                	}
                	return true;
                }

                
                if(commandarg.equals("adminfuel") && this.permissions.has(sender, "minecars.admin")) {
                	this.mFM.doNewFuel((Player) sender);
                	this.mML.canMove = true;
                	sender.sendMessage(green+"[MineCars]"+ white+" Filled your tank up for free!");
                	return true;
                }
                
                if(commandarg.equals("reload") &&  this.permissions.has(sender, "minecars.admin.reload")) {
                	this.mPlugin.reloadConfig();
                	try {
						this.mPlugin.configAll();
					} catch (IOException e) {
						
						e.printStackTrace();
					}
                	sender.sendMessage(String.format(green+"[%s]"+white+" Sucessfully reloaded config!", this.mPlugin.getDescription().getName()));
                	return true;

                
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
        	return true;
        }
        
        } else
        	sender.sendMessage(green + "======= MineCars v" + mPlugin.getDescription().getVersion() + " =======");
        	sender.sendMessage(green + "/mcg"+white+" 1,2,3");
        	sender.sendMessage(green + "/mcg"+white+" fuel");
        	sender.sendMessage(green + "/mcg"+white+" prices");
        	sender.sendMessage(green + "/mcg"+white+" buy");
        	if(this.permissions.has(sender, "minecars.admin")){
        		sender.sendMessage(green + "/mcg"+white+" adminfuel");
        	}
        	if(this.permissions.has(sender, "minecars.admin")||this.permissions.has(sender, "minecars.admin.reload")){
        		sender.sendMessage(green + "/mcg"+white+" reload");
        	}
        	return true;
		
	}
}
