package com.zombie.logic.object.stat;

import java.io.Serializable;

import com.zombie.logic.object.GameObject;

/** Объект владеет неким набором характеристик,
 *  которые могут изменяться из-за некоторых условий - наложенных ядов, и пр. навыков */
public class BasicStat implements Serializable{

	private static final long serialVersionUID = 1L;
	public transient GameObject owner;
	
	public BasicStat(GameObject live){
		owner = live;	
	}

	public int maxHp = 100;	
	public int hp = 100;
	public int defence = 0;
	public float evasion = 0;
	public int level = 1;


	public void setMaxHp(int max) {
		hp = maxHp = max;
	}
}
