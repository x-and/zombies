package com.zombie.effect;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Shape;
import com.zombie.C;
import com.zombie.Renderable;

/** Effect must have texture to render it, position, size, angle, scale, alpha, etc */
public abstract class AbstractEffect implements Renderable{

	private Shape shape;
	public Vector2 position = new Vector2();
	public float angle;
	private long lifeTime = 1000L;
	private long fullLifeTime = lifeTime;
	
	public int renderGroup = C.GROUP_PRE_EFFECT;
	
	public abstract void update(int delta);
	public abstract void remove();
	
	public Shape getShape() {
		return shape;
	}
	public void setShape(Shape shape) {
		this.shape = shape;
	}
	public long getLifeTime() {
		return lifeTime;
	}
	protected void setLifeTime(long lifeTime) {
		this.lifeTime = lifeTime;
	}
	public long getFullLifeTime() {
		return fullLifeTime;
	}
	public void setFullLifeTime(long fullLifeTime) {
		this.fullLifeTime = fullLifeTime;
		lifeTime = fullLifeTime;
	}
	public int getRenderGroup(){
		return renderGroup;
	}
	
	public float getX(){
		return position.x;
	}
	
	public float getY(){
		return position.y;
	}
	
	public void setX(float x){
		position.x = x;
	}
	
	public void setY(float y){
		position.y = y;
	}
	
}
