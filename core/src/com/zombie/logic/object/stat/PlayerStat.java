package com.zombie.logic.object.stat;

import com.zombie.logic.object.LiveObject;

public class PlayerStat extends LiveStat {

	private static final long serialVersionUID = 1L;
	
	public int shots;
	public int hits;
	public int kills;
	public int dies;
	public int earned;
	public int skillPoints = 0;
	public int money = 100;
	public int damage;
	
	public PlayerStat(LiveObject live) {
		super(live);
	}

}
