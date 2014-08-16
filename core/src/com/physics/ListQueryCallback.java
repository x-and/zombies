package com.physics;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;

public class ListQueryCallback extends SizedQuery{

	public List<Body> bodyes = new ArrayList<Body>();
	
	@Override
	public boolean reportFixture(Fixture fixture) {
		bodyes.add(fixture.getBody());
		return true;
	}

	@Override
	public void run() {
		Physics.world.QueryAABB(this, min.x, min.y, max.x, max.y);
		isDone();
	}

}
