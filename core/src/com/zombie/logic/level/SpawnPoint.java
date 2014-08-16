package com.zombie.logic.level;

import com.badlogic.gdx.math.Vector2;
import com.zombie.util.Rnd;


public class SpawnPoint {

	public int minX,maxX,minY,maxY;
	public int id;
	
	public Vector2 getPoint(){
		float x = minX;
		float y = minY;
		if (minX != maxX)
			x = x + Rnd.nextInt((int) Math.abs(maxX-minX));
		if (minY != maxY)
			y = y + Rnd.nextInt((int) Math.abs(maxY-minY));
		return new Vector2(x,y);
	}
}
