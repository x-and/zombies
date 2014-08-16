package com.zombie.effect;

import com.badlogic.gdx.math.MathUtils;


public class MovingImageEffect extends ImageEffect {

	public float angle2;
	public float velocity = 0.005f;
	public float aVelocity = 0.1f;
	public boolean slowDown = true;
	public int stopTime;
	
	@Override
	public void update(int delta) {
		super.update(delta);
		if (stopTime <= 0)
			return;
		stopTime--;
		float x1 = velocity*delta*MathUtils.cos(MathUtils.degRad*angle2);
		float y1 = velocity*delta*MathUtils.sin(MathUtils.degRad*angle2);
		setX(getX()+x1);
		setY(getY()+y1);
		angle+=aVelocity;
		if (slowDown){
			velocity-= velocity/20;
			aVelocity-= aVelocity/20;
		}
	}
}
