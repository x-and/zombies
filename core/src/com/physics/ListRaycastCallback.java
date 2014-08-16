package com.physics;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

public class ListRaycastCallback implements RayCastCallback{

	public List<Body> bodyes = new ArrayList<Body>();

	@Override
	public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal,
			float fraction) {
		if (!bodyes.contains(fixture.getBody()))
			bodyes.add(fixture.getBody());
		return 1;
	}


}
