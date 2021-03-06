package com.egodroid.bukkit.carmod.util;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import com.egodroid.bukkit.carmod.CarMod;

public class FuelManager implements Listener {
	
	private File inputfile;
	private FileConfiguration input;
	private CarMod mPlugin;
	private int fuelPrice;
	private int itemCount;
	private int itemID;
	private boolean useFuelSystem;
	public boolean useEconomy;
	private String tempString;
	@SuppressWarnings("unused")
	private final Logger log = Logger.getLogger("Minecraft");
	public boolean hasShowed = false;
	private ChatColor dGreen = ChatColor.DARK_GREEN;
	private ChatColor white = ChatColor.WHITE;
	
	public FuelManager(CarMod pPlugin) {
		this.mPlugin = pPlugin;
		this.inputfile = new File(this.mPlugin.getDataFolder(), "playerFuel.yml");
		if(!this.inputfile.exists())
			try {
				this.inputfile.createNewFile();
			} catch (IOException e) {
				 
				e.printStackTrace();
			}
		this.setupConfig();
		this.input = new YamlConfiguration();
		this.loadYamls();
	}
	
	
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
				this.registerPlayer(event.getPlayer().getName());		
	}
	
	public void setEconomy(boolean pUseEconomy) {
		this.useEconomy = pUseEconomy;
	}
	
	public void hasMoved(Player player) {
			if(this.useFuelSystem) {
					this.removeFuelPlayer(player.getName());
				} 
	}	

	public boolean canMove(Player player) throws SQLException {
		if (this.useFuelSystem) {
			int tempfuel =  this.input.getInt(player.getName() + ".fuel");
			
			if (tempfuel == 20 && this.hasShowed == false) {
				player.sendMessage(dGreen+"[MineCars]"+ ChatColor.DARK_RED+" Warning!"+white+" Your fuel is getting �clow! Check fuel with �c/mcg �cfuel");
				this.hasShowed = true;
				return true;
			}
			
			if (tempfuel == 10) {
				this.hasShowed = false;
			}

			if (tempfuel == 0 && this.hasShowed == false ) {
				if(CarMod.permission.has(player, "minecars.buyfuel")){
					player.sendMessage(dGreen + "[MineCars] "+white+ "You don't have any fuel left! Buy more with /mcg buy, or visit a Fuel Station.");
				}else{
					player.sendMessage(dGreen + "[MineCars] "+white+ "You don't have any fuel left! Buy more at a Fuel Station.");
				}
				this.hasShowed = true;
				return false;
			}
			else {
				return true ;
			}
		} else return true;
		
	}
	
	public String getProgressBar(Player pPlayer) {
	
		int i, q;
		int p = 0;
		this.tempString = "";
		i = this.input.getInt(pPlayer.getName() + ".fuel");	
			p = i / 10;
			q=  10 - (i / 10);
		this.tempString= dGreen + "[MineCars]"+white+" Your current fuel level is: ";
		
		while(p > 0) {
			p--;
				this.tempString = this.tempString + "�e| ";
		}
		
		while(q > 0) {
			q--;
			this.tempString = this.tempString + "�f| ";
		}
		
		return this.tempString;
	}
	
	public boolean buyFuel(Player pPlayer) {
		boolean success = false;		
	
		if (this.useEconomy) {
			int tempfuel =  this.input.getInt(pPlayer.getName()+ ".fuel");
			BigDecimal priceperbar = new BigDecimal(this.fuelPrice).divide(new BigDecimal(100));
			BigDecimal deltafuel = new BigDecimal(100).subtract(new BigDecimal(tempfuel));	
			BigDecimal costBD = deltafuel.multiply(priceperbar);
			float costs = costBD.floatValue();
			

			
			EconomyResponse er = CarMod.economy.withdrawPlayer(pPlayer.getName(), costs);
				if (er.transactionSuccess()) {
					if (costs != 0)
						pPlayer.sendMessage(String.format(dGreen+"[%s]"+white+" You successfully bought new fuel for %s %s!", this.mPlugin.getDescription().getName(), costs, CarMod.economy.currencyNamePlural()));
					else pPlayer.sendMessage(String.format(dGreen+"[%s]"+white+" You don't need any fuel!", this.mPlugin.getDescription().getName()));
					this.addFuelPlayer(pPlayer.getName(), (100));
					success = true;
				} else {
					pPlayer.sendMessage(String.format(dGreen+"[%s]"+white+"- You don't have enough Money! You need at least %s %s", this.mPlugin.getDescription().getName(), costs, CarMod.economy.currencyNamePlural()));
					success = false;
					
				}
				
			} else {
				
				int tempfuel =  this.input.getInt(pPlayer.getName() + ".fuel");
				BigDecimal priceperbaritem = new BigDecimal(this.itemCount).divide(new BigDecimal(100));		
				BigDecimal deltafuel = new BigDecimal(100).subtract(new BigDecimal(tempfuel));					
				BigDecimal costBD = deltafuel.multiply(priceperbaritem).setScale(0, BigDecimal.ROUND_CEILING);
				int costsitem=  costBD.intValue();
				
				
				
				
				if(pPlayer.getInventory().contains(this.itemID)) {
					ItemStack found = null;
					HashMap<Integer, ? extends ItemStack> fis = pPlayer.getInventory().all(this.itemID);
					for (ItemStack i : fis.values() ) {
						if (i.getAmount() >= ((int) costsitem)) {
							found = i;
						}

					}
					if (found != null) {
						pPlayer.getInventory().removeItem(new ItemStack(this.itemID, (int) costsitem));
						this.addFuelPlayer(pPlayer.getName(), 100);
						pPlayer.sendMessage(String.format(dGreen+"[%s]"+white+" You sucessfully bought new fuel for %s x %s !", this.mPlugin.getDescription().getName(), costsitem, Material.getMaterial(this.itemID).name()));
						success = true;
					} else {
						if(costsitem !=0)
							pPlayer.sendMessage(String.format(dGreen+"[%s]"+white+" You need at least %s of %s in your Inventory!", this.mPlugin.getDescription().getName(), costsitem,  Material.getMaterial(this.itemID).name()));
						else pPlayer.sendMessage(String.format(dGreen+"[%s]"+white+" You don't need any fuel!", this.mPlugin.getDescription().getName()));
						success = false;
					}
				
				} else {
					if(costsitem !=0)
						pPlayer.sendMessage(String.format(dGreen+"[%s]"+white+" You need at least %s of %s in your Inventory!", this.mPlugin.getDescription().getName(), costsitem,  Material.getMaterial(this.itemID).name()));
					else pPlayer.sendMessage(String.format(dGreen+"[%s]"+white+" You don't need any fuel!", this.mPlugin.getDescription().getName()));
					success = false;
				}
			
		}
		
		
		

		if(success) 
			this.hasShowed = false;
		return success;
		
		
	}
    
    public void doNewFuel(Player pPlayer) {
    	this.addFuelPlayer(pPlayer.getName(), 100);
    }


	public void setupConfig() {
		this.fuelPrice = this.mPlugin.getConfig().getInt("priceperfuel");
		this.itemCount =  this.mPlugin.getConfig().getInt("itemsperfuel");
		this.itemID = this.mPlugin.getConfig().getInt("fuelitemid");
		this.useFuelSystem = this.mPlugin.getConfig().getBoolean("UseFuelSystem");
	}
	
	public void registerPlayer(String pPlayerName) {
		if(!this.input.isSet(pPlayerName + ".fuel"))
			this.input.set(pPlayerName + ".fuel",100 );
	//	((YamlConfiguration) this.input.getList(pPlayerName)).addDefault("fuel", 100);
		this.saveYamls();
	}
	
	public void removeFuelPlayer(String pPlayerName) {
		int tempfuel = this.input.getInt(pPlayerName + ".fuel");
		tempfuel--;	
		this.input.set(pPlayerName + ".fuel", tempfuel);
		
		this.saveYamls();
	}
	
	public void addFuelPlayer(String pPlayerName, int pFuel) {
		int tempfuel = this.input.getInt(pPlayerName + ".fuel");
		tempfuel = pFuel;
		this.input.set(pPlayerName + ".fuel", tempfuel);
		
		this.saveYamls();
	}
	
	
	
	public void saveYamls() {
	    try {
	        this.input.save(this.inputfile);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	public void loadYamls() {
	    try {
	        this.input.load(this.inputfile);

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	

}
