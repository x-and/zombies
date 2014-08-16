package com.zombie;

import java.io.Serializable;
import java.util.Arrays;

import com.badlogic.gdx.utils.Array;
import com.zombie.achieve.AchieveSystem;
import com.zombie.achieve.Achievement;
import com.zombie.logic.item.Item;
import com.zombie.logic.item.Weapon;
import com.zombie.logic.object.live.Player;
import com.zombie.logic.object.stat.PlayerStat;

public class Save implements Serializable{

	private static final long serialVersionUID = -1181052262809842363L;
	
	public PlayerStat stat;
	public Item[] items;
	public Weapon[] weapons;
	public int[] slots = new int[3];
	public Achievement[] achieved = new Achievement[1];
	
	public Save(){
		Arrays.fill(slots, -1);
	}
	public void save(Player p){
		if (p == null)
			return;
		stat = p.getStat();
		items = p.items.toArray(Item.class);
		weapons = p.weapons.toArray(Weapon.class);
		slots = p.slots;
		achieved = AchieveSystem.achieved.toArray(Achievement.class);
		
	}

	public void loadPlayer(Player p) {
		if (stat != null)
			p.setStat(stat);
		p.setHp(p.getMaxHp());
		if (items != null)
			p.items = new Array<Item>(items);
		if (weapons != null)
		p.weapons = new Array<Weapon>(weapons);
		p.slots = slots;
	}
}
