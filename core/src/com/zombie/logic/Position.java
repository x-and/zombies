package com.zombie.logic;

import com.badlogic.gdx.math.Vector2;
import com.zombie.logic.object.GameObject;

public class Position {

	Vector2 size = new Vector2(32,32);
	public Vector2 current = new Vector2();
	public Vector2 old = new Vector2();
	float angle = 0;
	float oldAngle = 0;
	
	public void updatePosition(){
		old.set(current);	
		oldAngle = angle;
	}
	
	public float getX(){
		return current.x;
	}
	
	public float getY(){
		return current.y;
	}
	
	public float getA(){
		return angle;
	}

	public void set(float x, float y) {
		current.set(x, y);
	}

	public void set(float x, float y,float a) {
		set(x,y);
		angle = a;
	}

	public void setAngle(float a) {
		angle = a;
	}

	public boolean moved() {
		return current.dst(old) > 0.1f;
	}

	public void set(Position pos) {
		current.set(pos.current);
		angle = pos.angle;
		size.set(pos.size);
	}

	public float getW() {
		return size.x;
	}
	
	public float getH() {
		return size.y;
	}

	public float dst(GameObject obj) {
		return obj.getPos().current.dst(current);
	}

	public float getOldX() {
		return old.x;
	}
	
	public float getOldY() {
		return old.y;
	}

	public void setSize(float x, float y) {
		size.set(x, y);
	}

	public void setW(float x) {
		size.x = x;
	}
	
	public void setH(float y) {
		size.y = y;
	}
}
