package com.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

public class RayCastRequest implements Runnable {

	public RayCastCallback callback;
	public Vector2 start, finish;
	
	public void add(){
		if(start.equals(finish))
			return;
		Physics.task(this);
	}

	@Override
	public void run() {
	}
}
