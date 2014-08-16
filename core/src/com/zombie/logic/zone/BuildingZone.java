package com.zombie.logic.zone;

import com.esotericsoftware.minlog.Log;
import com.zombie.logic.Building;
import com.zombie.state.GameState;


public class BuildingZone extends Zone {

	public Building building;
	
	public BuildingZone(String name) {
		super(0, 0);
		this.name = name;
		Log.info("Zone", "created");
	}
	
	void playerEntered(){
		super.playerEntered();
		building.onEnter();
		GameState.player.building = building.name;
//		GameState.player.insideBuilding = true;
//		GameState.player.buildingName = buildingName;
	}
	
	void playerExited(){
		super.playerExited();
		building.onExit();
		GameState.player.building = null;
//		GameState.player.insideBuilding = false;
	}
}
