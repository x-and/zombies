package com.zombie.logic.level;

import com.zombie.logic.item.Item;
import com.zombie.logic.object.LiveObject;
import com.zombie.util.Rnd;

public class MobDef {

	public int id;
	public String className;
	public String name;
	public int minHp;
	public int maxHp;
	public int width = 0;
	public int height = 0;
	public int minDamage;
	public int maxDamage;
	public float minVelocity;
	public float maxVelocity;
	public String image;
	public int level;
	public int defence = 0;
	public float evasion = 0;
	
	
	public LiveObject getNewMobInstance(float x,float y){
		try {
			LiveObject z = null;
		Class<?> c = Class.forName("com.zombie.logic.object.live." +className);
		if (width != 0 && height != 0)
			z = (LiveObject) c.getConstructor(float.class,float.class,String.class,int.class,int.class).newInstance(x,y,image,width,height);
		else 
			z = (LiveObject) c.getConstructor(float.class,float.class,String.class).newInstance(x,y,image);
		z.getStat().mobDefId = id;
		z.getStat().defence = defence;
		z.getStat().evasion = evasion;
		float velocity = minVelocity;
		int damage = minDamage;
		int hp = minHp;
		if (minVelocity != maxVelocity)
			velocity = velocity + Rnd.nextFloat()*(maxVelocity-minVelocity);
		if (minDamage != maxDamage)
			damage = damage + Rnd.nextInt(maxDamage-minDamage);
		if (minHp != maxHp)
			hp = hp + Rnd.nextInt(maxHp-minHp);
		z.getStat().level = level;
		z.setVelocity(velocity);
		z.setDamage(damage);
		z.setMaxHp(hp);
		addDrop(z);
		return z;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	public static void addDrop(LiveObject z) {
		for(Item i : Item.items.values()){
			if (i.dropChance > 0)
				if (Rnd.nextInt(i.dropChance) == 0)
					z.addDrop((Item) i.clone());
		}
	}

	
}
