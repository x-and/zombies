package com.zombie.logic.object.stat;

import com.zombie.logic.object.LiveObject;

public class LiveStat extends BasicStat {

	private static final long serialVersionUID = 1L;
	public LiveStat(LiveObject live) {
		super(live);
	}

	public int strength = 10;
	public int agility = 10;
	public int endurance = 10;

	public int exp;
	public int mobDefId = -1;
	public int damage = 5;
}
