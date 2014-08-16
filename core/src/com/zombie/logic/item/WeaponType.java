package com.zombie.logic.item;

public enum WeaponType {

	STANDART,SHOTGUN,ROCKET,MELEE;

	public static WeaponType forString(String s) {
		for(WeaponType wt : WeaponType.values())
			if (wt.toString().equalsIgnoreCase(s))
				return wt;
		return STANDART;
	}
}
