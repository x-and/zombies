package com.zombie.logic.object.stat;

import com.zombie.logic.object.vehicle.Vehicle;

public class VehicleStat extends BasicStat {

	private static final long serialVersionUID = 1L;

	public VehicleStat(Vehicle vehicle) {
		super(vehicle);
	}
	
	/**max speed in km/h*/
	public float maxSpeed = 40;
	public float maxSteering = 15;

	

}
