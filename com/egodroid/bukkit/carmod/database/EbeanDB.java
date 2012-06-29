package com.egodroid.bukkit.carmod.database;

import javax.persistence.Entity;
import javax.persistence.Table;


import com.avaje.ebean.validation.NotNull;


@Entity
@Table(name="minecars_fuel")
public class EbeanDB {
	@NotNull
	private String playerName;
	private int fuelpercent;
	
	public String getPlayerName() {
		return playerName;
	}
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	public int getFuelpercent() {
		return fuelpercent;
	}
	public void setFuelpercent(int fuelpercent) {
		this.fuelpercent = fuelpercent;
	}
	
	

	
}
