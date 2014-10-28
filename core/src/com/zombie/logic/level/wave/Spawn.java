package com.zombie.logic.level.wave;


import com.badlogic.gdx.math.Vector2;
import com.zombie.logic.GameWorld;
import com.zombie.logic.level.SpawnPoint;
import com.zombie.logic.object.LiveObject;
import com.zombie.state.GameState;

public class Spawn {

	public int id;
	public int time;
	public int mobId;
	public int count;
	public boolean spawned = false;
	public int spawnPoint;
	
	public void spawn(Wave w) {
		if (spawned)
			return;
		SpawnPoint sp = w.level.spawnPoints.get(spawnPoint);
		for(int i = 0; i < count;i++){
			Vector2 start = sp.getPoint();
			LiveObject z = w.level.mobs.get(mobId).getNewMobInstance(start.x,start.y);
			z.getAI().setTarget(GameState.player);
			GameWorld.addObject(z);
		}
		spawned = true;
	}

}
