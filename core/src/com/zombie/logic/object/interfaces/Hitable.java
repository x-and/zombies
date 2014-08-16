package com.zombie.logic.object.interfaces;

import com.zombie.logic.object.GameObject;

public interface Hitable {

	/**this object is hitted by damager with damage value */
	public void hitted(float value, GameObject damager);
	
	public void doDie(GameObject killer);

	public boolean isDead();

	public int getHp();
	
}
