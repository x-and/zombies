package com.zombie.logic.level;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.zombie.C;
import com.zombie.logic.GameWorld;
import com.zombie.logic.item.Item;
import com.zombie.logic.object.ItemObject;
import com.zombie.util.Rnd;


public class RandomItemSpawn {

	public int chance;
	public int timer;
	public int nextTime;
	public int id;
	public List<Item> drop = new ArrayList<Item>();
	public List<SpawnPoint> spawnPoints = new ArrayList<SpawnPoint>();
	
	public void init() {
		nextTime+=timer;
	}

	public void doSpawn() {
		if (Rnd.nextInt(C.MAX_CHANCE) < chance){
			Item item = drop.get(Rnd.nextInt(drop.size()));
			SpawnPoint point = spawnPoints.get(Rnd.nextInt(spawnPoints.size()));
			Vector2 vec2f = point.getPoint();
			ItemObject object = new ItemObject(vec2f.x,vec2f.y,Item.getItemById(item.id));
			GameWorld.addObject(object);
		}
		nextTime+=timer;
	}
	
}
