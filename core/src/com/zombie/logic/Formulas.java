package com.zombie.logic;

import com.zombie.C;
import com.zombie.logic.object.Bullet;
import com.zombie.logic.object.GameObject;
import com.zombie.logic.object.LiveObject;
import com.zombie.logic.object.interfaces.Hitable;
import com.zombie.util.Rnd;

public class Formulas {

	/** return true if bullet miss target*/
	public static boolean calcMissChance(Bullet bullet, Hitable live) {
		if (live instanceof LiveObject){
			int missChance = C.MAX_CHANCE-(int)(((LiveObject) live).getStat().evasion*C.MAX_CHANCE);
			if (missChance < Rnd.nextInt(C.MAX_CHANCE)){
				return true;
			}
		}
		return false;
	}

	public static void calcDamage(Bullet bullet, Hitable live) {
		calcDamage(bullet.damage,bullet.owner,live);
	}

	/** calc damage considering the stats of damaged liveobject*/
	public static void calcDamage(float dmg, GameObject damager,
			Hitable live) {
		int damage = (int) (dmg - ((GameObject) live).getStat().defence);
		live.hitted(Math.max(damage, 1),damager);		
	}

}
