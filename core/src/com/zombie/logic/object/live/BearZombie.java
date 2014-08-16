package com.zombie.logic.object.live;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.manager.ResourceManager;
import com.manager.TimeManager;
import com.zombie.C;
import com.zombie.effect.ImageEffect;
import com.zombie.logic.GameWorld;
import com.zombie.logic.object.GameObject;

public class BearZombie extends Zombie {

	int accelerationTime = 2000;
	long accelerationStarted = 0;
	boolean isAccelerated = false;

	
	public BearZombie(float x, float y, String image, int w, int h) {
		super(x, y, image, w, h);
	}

	public BearZombie(float x, float y, String image) {
		super(x, y, image,28,28);
	}
	
	@Override
	public void update(float delta) {
		super.update(delta);
		if (isDead() || wait)
			return;
		
		if (TimeManager.getLongTime()-accelerationStarted > accelerationTime){
			isAccelerated = false;
		}
//		
		if (isAccelerated){
			ImageEffect eff = new ImageEffect();
			eff.image = ResourceManager.getAnimation(image+"_move").getKeyFrame(TimeManager.getLongTime());
			eff.renderGroup = C.GROUP_PRE_NORMAL;
			eff.setBounds(getX()-getW()/2, getY()-getH()/2, getW(), getH());
			eff.angle = getA();
			eff.opacity = 0.5f;
			eff.setFullLifeTime(500);
			GameWorld.addEffect(eff);
		}
	}
	
	public void draw(SpriteBatch batch, ShapeRenderer shapeBatch){
		if (isAccelerated){
			Color old = batch.getColor();
			batch.setColor(Color.RED.r,Color.RED.g,Color.RED.b,0.7f);
			super.draw(batch, shapeBatch);
			batch.setColor(old);
		} else
			super.draw(batch, shapeBatch);
	}
	
	protected void onHit(GameObject damager){
		super.onHit(damager);
		if (accelerationStarted != 0 && TimeManager.getLongTime()-accelerationStarted < accelerationTime*3)
			return;
		accelerate();
	}

	private void accelerate() {
		isAccelerated = true;
		accelerationStarted = TimeManager.getLongTime();
		playSound();
	}
	
	public float getVelocity(){
		if (isAccelerated)
			return super.getVelocity()*2f;
		return super.getVelocity();
	}
	
	public int getDamage() {
		if (isAccelerated)
			return (int) (super.getDamage()*2f);
		return super.getDamage();
	}
}
