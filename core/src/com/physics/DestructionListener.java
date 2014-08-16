package com.physics;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Joint;


public class DestructionListener implements com.badlogic.gdx.physics.box2d.DestructionListener {

	public void sayGoodbye(Joint joint) {
		
	}

	public void sayGoodbye(Fixture fixture) {
//		System.out.println("body destroyed "+ fixture.m_body.getUserData());

	}


}
