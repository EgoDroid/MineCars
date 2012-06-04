package com.egodroid.bukkit.carmod.listeners;

import java.sql.SQLException;
import java.util.HashMap;

import org.bukkit.Effect;
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
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleUpdateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Step;
import org.bukkit.material.Wool;
import org.bukkit.util.Vector;

import com.egodroid.bukkit.carmod.CarMod;
import com.egodroid.bukkit.carmod.util.FuelManager;


public class minecartListener implements Listener {

	private int counter ;
	private int mMultiplier = 2;
	private int mStreetSpeedF = 2;
	private int mMotorWaySpeedF = 5;
	private boolean useWool = true;
	private boolean shouldDestroy = true;
	private HashMap<String, Integer> mPlayerMap;
	private HashMap<String, Float> mPlayerYawMap;
	private FuelManager mFuelM;
	private boolean moved = false;
	
	//Blocks from Config
	private Material mMotorwayBlock;
	private Material mStreetBlock;
	private String mStreetWoolColor;
	private String mMWWoolColor;
	//End
	
	private CarMod mPlugin;

	
	public HashMap<String,Boolean> canMove;
	
	private Location oldlightLoc;
	private byte oldlightLevel;
	private boolean lightswitch = true;
	
	//Permissions



public minecartListener(CarMod plugin, FuelManager pFM) {
	this.mFuelM = pFM;
	this.mPlugin = plugin;
	this.mPlayerMap = new HashMap<String, Integer>();
	this.mPlayerYawMap = new HashMap<String, Float>();
	this.canMove = new HashMap<String,Boolean>();
    this.setupConfig();
	
    for(Player p: mPlugin.getServer().getOnlinePlayers()){
    	try {
			boolean move = mFuelM.canMove(p);
			canMove.put(p.getName(), move);
		} catch (SQLException e) {

		}
    }
	
}	

public void setupConfig() {
	this.useWool = this.mPlugin.getConfig().getBoolean("useWool");
	if (this.useWool == true) {
		this.mStreetWoolColor = this.mPlugin.getConfig().getString("WoolColorstreet");
		this.mMWWoolColor = this.mPlugin.getConfig().getString("WoolColorMotorway");
	}
	else {
		this.mStreetBlock = Material.getMaterial(this.mPlugin.getConfig().getInt("streetBlock"));
		this.mMotorwayBlock = Material.getMaterial(this.mPlugin.getConfig().getInt("motorwayBlock"));
	}
}
	
	
@EventHandler
public void onPlayerLogin(PlayerLoginEvent event) {
	
	Player p = event.getPlayer();
	boolean move;
	
	if (this.mPlayerMap.containsKey(event.getPlayer().getName())) {
		
	} else {
		this.mPlayerMap.put(event.getPlayer().getName(), 2);
	}
	
	try {
		move = this.mFuelM.canMove(p);
		this.canMove.put(p.getName(), move);
	} catch (SQLException e) {
		
		e.printStackTrace();
	}
}



@EventHandler
public void onVehicleUpdate(VehicleUpdateEvent event) throws SQLException {

    Vehicle vehicle = event.getVehicle();
    Block unterblock = vehicle.getLocation().getBlock().getRelative(BlockFace.DOWN);
    Block normalblock = vehicle.getLocation().getBlock();
  //  Block vorderblock = vehicle.getLocation().getBlock().getRelative(arg0)
    Entity passenger = vehicle.getPassenger();
    if (!(passenger instanceof Player)) {
      return;
    }

    Player player = (Player)passenger;
    	if (event.getVehicle() instanceof Minecart) {
    		Minecart Auto = (Minecart) vehicle ;
    	//	vehicle.getLocation().getBlock()
    		Vector plPos = player.getLocation().getDirection();
    		if(!this.mPlayerMap.containsKey(player.getName())){
        		this.mPlayerMap.put(player.getName(), 3);	
    		}
        	int drivingspeednormal = this.mPlayerMap.get(player.getName());
        	int drivingspeedmw = this.mPlayerMap.get(player.getName());	

    		Location newLoc = Auto.getLocation();
    		Vector plvelocity = Auto.getPassenger().getVelocity();
    		//plvelocity.multiply(this.mMultiplier);	
    		
    		if (this.mPlugin.getConfig().getBoolean("UseExhaust")) {
    			player.getWorld().playEffect(player.getLocation(), Effect.SMOKE, 0);
    			player.getWorld().playEffect(player.getLocation(), Effect.SMOKE, 0);
    		}
    		
    		if (player.isInsideVehicle() && CarMod.permission.has(player, "minecars.move")) {
    		    float dir = (float)player.getLocation().getYaw();
    		    BlockFace face = getClosestFace(dir);
    		    Block stepblock = vehicle.getLocation().getBlock().getRelative(face );
    			
    			//HIER
    			if(event.getVehicle().getLocation().getBlock().getTypeId() != 0 && normalblock.getTypeId() != 27 && normalblock.getTypeId() != 28 && normalblock.getTypeId() != 66 && normalblock.getTypeId() != this.mPlugin.getConfig().getInt("RailingBlock")) {
    				if(normalblock.getTypeId() == 43 || normalblock.getTypeId() == 44) {	
    					Step step = new Step(normalblock.getType(), normalblock.getData());
    					if (step.getData() == (byte) this.mPlugin.getConfig().getInt("StreetStepType")) {
    						
    						} else { this.destroyCar(player, Auto, unterblock); return; }
    				} else {
    				this.destroyCar(player, Auto, unterblock);
    				return;
    				}
    			}
    			
    			if (stepblock.getTypeId() == 43 || stepblock.getTypeId() == 44) {
					Step step = new Step(stepblock.getType(), stepblock.getData());
					if (step.getData() == (byte) this.mPlugin.getConfig().getInt("StreetStepType")) {
						if(this.canMove.get(player.getName()) && player.getVelocity().getX() != 0 && player.getVelocity().getY() != 0) {
							Location newLoc2 = stepblock.getLocation();
							newLoc2.add(0, 1.5d, 0);
							Auto.teleport(newLoc2);
							return;
    				}
    				
					}
    			}
    			
    		/*	if(this.lightswitch) {
    				World world =  normalblock.getWorld();
    				
    				if (player.getVelocity().getX() == 0 && player.getVelocity().getZ() == 0) {
    					((CraftWorld)world).getHandle().a(EnumSkyBlock.BLOCK, (int) Auto.getLocation().getX(), (int) Auto.getLocation().getY() + 2, (int) Auto.getLocation().getZ(), 15);
    					this.oldlightLoc = Auto.getLocation();
    				} else {
    					((CraftWorld)world).getHandle().a(EnumSkyBlock.BLOCK, (int) this.oldlightLoc.getX(), (int) this.oldlightLoc.getY() + 2, (int) this.oldlightLoc.getZ(), 0);
    					((CraftWorld)world).getHandle().a(EnumSkyBlock.BLOCK, (int) Auto.getLocation().getX(), (int) Auto.getLocation().getY() + 2, (int) Auto.getLocation().getZ(), 15);
    					this.oldlightLoc = Auto.getLocation();
    					
    				}
    				
    				Block block = Auto.getLocation().getBlock();
    				block.setData(block.getData());
    				block.setType(block.getType());
    				
    				
    			} */
    			if (player.getVelocity().getX() == 0 && player.getVelocity().getZ() == 0) {
    				Auto.setDerailedVelocityMod(new Vector(0,0,0));
    			}
    			  		    
    			if (this.canMove.get(player.getName()) && player.getVelocity().getX() !=0 && player.getVelocity().getZ() !=0 && normalblock.getTypeId() == 0) {
    				
    				
    				
    			
    				if(this.useWool == true) {			
    						
    					if (unterblock.getType() == Material.WOOL) {
    						
    						Wool wolle = new Wool(unterblock.getType(), unterblock.getData());

    						if (wolle.getColor().toString().equalsIgnoreCase(this.mStreetWoolColor)) {
    							this.movingCar(Auto, drivingspeednormal, player, plvelocity, false);
    						} else if (wolle.getColor().toString().equalsIgnoreCase(this.mMWWoolColor)) {
    							this.movingCar(Auto, drivingspeednormal, player, plvelocity, true);
    								}
    						if (!wolle.getColor().toString().equals(this.mMWWoolColor) && !wolle.getColor().toString().equals(this.mStreetWoolColor) && normalblock.getTypeId() != 27 && normalblock.getTypeId() != 28 && normalblock.getTypeId() != 66 ) {
    							if (! this.isRightStep(unterblock)) 
    								this.destroyCar(player, Auto, unterblock);
    						}
    					} else if(normalblock.getTypeId() != 27 && normalblock.getTypeId() != 28 && normalblock.getTypeId() != 66 ){ if (! this.isRightStep(unterblock)) this.destroyCar(player, Auto, unterblock);};
    				}
    			else {
    	    			if (unterblock.getType() == this.mStreetBlock) {
    	    				this.movingCar(Auto, drivingspeednormal, player, plvelocity, false);
    	    			} else if (unterblock.getType() == this.mMotorwayBlock) {
    	    				this.movingCar(Auto, drivingspeednormal, player, plvelocity, true);
    	    			}
    	    			
    	    			if (unterblock.getTypeId() != this.mMotorwayBlock.getId() && unterblock.getTypeId() != this.mStreetBlock.getId() && normalblock.getTypeId() != 66 && normalblock.getTypeId() != 27 && normalblock.getTypeId() != 28) {
    	    				if (! this.isRightStep(unterblock)) 
    	    					this.destroyCar(player, Auto, unterblock);
    	    			}
    	    		
    				
    			}
    				
    				if (unterblock.getType() == Material.DOUBLE_STEP || unterblock.getType() == Material.STEP) {
    					if (normalblock.getTypeId() == 0) {
    					Step step = new Step(unterblock.getType(), unterblock.getData());
    					if (step.getData() == (byte) this.mPlugin.getConfig().getInt("StreetStepType")) {
    						this.counter++;
    						if (this.counter == 40) {
    							this.counter = 0;
    							this.mFuelM.hasMoved(player);
    							try {
    								boolean move = this.mFuelM.canMove(player);
    								this.canMove.put(player.getName(), move);
    							} catch (SQLException e) {
    								
    								e.printStackTrace();
    							}
    						}
    						
    							plvelocity.multiply(15d);
    						
    						
    						newLoc.add(new Vector(plvelocity.getX() ,0.0D, plvelocity.getZ()));
    						this.moved = true; //HIER
    					    Auto.teleport(newLoc);
    	    	    		//Auto.setVelocity(new Vector(plvelocity.getX(),0.0D, plvelocity.getZ()));
    					}
    					}
    					
    				}
    				
    				
    			
    		}
    			
    			if (normalblock.getTypeId() == 0) {
    				
				Location tempLocYaw = Auto.getLocation();
				tempLocYaw.setYaw(this.mPlayerYawMap.get(player.getName()));
				Auto.teleport(tempLocYaw);
    			}
    			
    			
    		}

    	}
    
	
	

}

public void setSpeedMultiplier(int pMultiplier, Player pPlayer) {
	String tempString = pPlayer.getName();
	this.mPlayerMap.remove(tempString);
	this.mPlayerMap.put(tempString, pMultiplier);

	
}

public int getSpeedMultiplier(Player pPlayer) {
	String tempString = pPlayer.getName();
	
	if (mPlayerMap.containsKey(tempString)){
		return this.mPlayerMap.get(tempString);
	}
	return 0;
	
}


public void setSpeedFactors(int pStreetF, int pMotorwayF) {
	this.mStreetSpeedF = pStreetF;
	this.mMotorWaySpeedF = pMotorwayF;
	
}

private void destroyCar(Player pPlayer, Minecart pVehicle, Block pUnterblock) {
	
	if (pUnterblock.getRelative(BlockFace.DOWN).getTypeId() == 43 || pUnterblock.getRelative(BlockFace.DOWN).getTypeId() == 44) {
		Step step = new Step(pUnterblock.getRelative(BlockFace.DOWN).getType(), pUnterblock.getRelative(BlockFace.DOWN).getData());
			if (step.getData() == (byte) this.mPlugin.getConfig().getInt("StreetStepType")) {
				return;
			} else {
				
				if (this.mPlugin.getConfig().getBoolean("destroyCar") && this.moved ) {
					Location temploc = pVehicle.getLocation();
					pVehicle.eject();
					pVehicle.remove();
					pVehicle.getWorld().dropItem(temploc, new ItemStack(328, 1));
				}
				
			}
	
	} else {
		if (this.mPlugin.getConfig().getBoolean("destroyCar") && this.moved ) {
			Location temploc = pVehicle.getLocation();
			pVehicle.eject();
			pVehicle.remove();
			pVehicle.getWorld().dropItem(temploc, new ItemStack(328, 1));
		}
	}
	

	

}

public BlockFace getClosestFace(float direction){

    direction = direction % 360;

    if(direction < 0)
        direction += 360;

    direction = Math.round(direction / 45);

    switch((int)direction){

        case 0:
            return BlockFace.WEST;
        case 1:
            return BlockFace.NORTH_WEST;
        case 2:
            return BlockFace.NORTH;
        case 3:
            return BlockFace.NORTH_EAST;
        case 4:
            return BlockFace.EAST;
        case 5:
            return BlockFace.SOUTH_EAST;
        case 6:
            return BlockFace.SOUTH;
        case 7:
            return BlockFace.SOUTH_WEST;
        default:
            return BlockFace.WEST;

    }
}

public boolean isRightStep(Block pTestBlock) {
	if (pTestBlock.getTypeId() == 43 || pTestBlock.getTypeId() == 44) {
		Step step = new Step(pTestBlock.getType(), pTestBlock.getData());
		if (step.getData() == (byte) this.mPlugin.getConfig().getInt("StreetStepType")) {
			return true;
		}
	}
	return false;
}

@EventHandler
public void onVehicleCreate(VehicleCreateEvent event) {
	if (event.getVehicle() instanceof Minecart) {

		//Minecart cart = (Minecart) event.getVehicle();
		

		}
}


@EventHandler
public void onVehicleEnter(VehicleEnterEvent event) {
    Entity passenger = event.getEntered();
    if (!(passenger instanceof Player)) {
  
      return;
    }
   
    this.mPlayerYawMap.put(((Player) event.getEntered()).getName(), new Float( event.getVehicle().getLocation().getYaw()));

	
	Location locyaw = event.getVehicle().getLocation();
	locyaw.setYaw(event.getVehicle().getLocation().getYaw());
	event.getVehicle().teleport(locyaw);
	
} 

private void movingCar(Minecart Auto, int pGear, Player player, Vector plvelocity, boolean motorway) {
	Location newLoc = Auto.getLocation();
	this.counter++;
	if (this.counter == 40) {
		this.counter = 0;
		this.mFuelM.hasMoved(player);
		try {
			boolean move = this.mFuelM.canMove(player);
			this.canMove.put(player.getName(), move);
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}
	if (motorway == false) 
		plvelocity.multiply(this.mStreetSpeedF);
	else
		plvelocity.multiply(this.mMotorWaySpeedF);
	plvelocity = this.checkRailing(Auto, plvelocity);
	newLoc.add(new Vector(plvelocity.getX() * pGear,0.0D, plvelocity.getZ() * pGear ));
	this.moved = true; //HIER
    Auto.teleport(newLoc);
  // 	plvelocity.multiply(pGear);
	//Auto.setVelocity(plvelocity);
	
	//Auto.setMaxSpeed(40D);
//	Auto.setFlyingVelocityMod(new Vector (1,0,0));
	//Auto.setDerailedVelocityMod(new Vector (1,0,0));

	//Auto.setVelocity(new Vector (40D,0,0));
	
}

private Vector checkRailing(Minecart pAuto, Vector pPlayerVel) {
	if (this.mPlugin.getConfig().getInt("RailingBlock") != 0) { 
		int railingblock = this.mPlugin.getConfig().getInt("RailingBlock");
		Block AutoBlock = pAuto.getLocation().getBlock();
		//boolean zisbigger = Math.abs(pPlayerVel.getZ()) > Math.abs(pPlayerVel.getX());
		if (AutoBlock.getRelative(BlockFace.NORTH).getTypeId() == railingblock) {
			pPlayerVel.setX(0.2d);
		}
		
		if (AutoBlock.getRelative(BlockFace.SOUTH).getTypeId() == railingblock) {
			pPlayerVel.setX(-0.2d);
		}
		
		if (AutoBlock.getRelative(BlockFace.WEST).getTypeId() == railingblock) {
			pPlayerVel.setZ(-0.2d);
		}
		
		if (AutoBlock.getRelative(BlockFace.EAST).getTypeId() == railingblock) {
			pPlayerVel.setZ(0.2d);
		}
	} 
	return pPlayerVel;
}

}



