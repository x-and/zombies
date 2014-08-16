package com.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.zombie.C;

public abstract class SizedQuery implements QueryCallback, Runnable{

	public Vector2 min;
	public Vector2 max;
	public boolean isDone = false;
	
	public void set(float minx,float miny,float maxx,float maxy){
		min = new Vector2(minx,miny);
		min.scl(C.WORLD_TO_BOX);
		max = new Vector2(maxx,maxy);
		max.scl(C.WORLD_TO_BOX);
	}

	public void isDone() {
		isDone = true;
	}
	
}
