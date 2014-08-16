package com.zombie.logic.object;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.esotericsoftware.minlog.Log;
import com.manager.ResourceManager;
import com.physics.BodyFactory;
import com.zombie.C;
import com.zombie.logic.enums.ObjectType;
import com.zombie.logic.object.interfaces.Hitable;
import com.zombie.logic.object.interfaces.Useable;
import com.zombie.logic.object.stat.BasicStat;

public class Door extends PhysicObject implements Useable,Hitable{

	public String building = "";
	public boolean isLocked = false;
	float health = 100;
	boolean destroyed = false;
	
	public Door(float x, float y) {
		this(x, y,32,32);
	}
	
	public Door(float x, float y,float w,float h) {
		super(x, y);
		type = ObjectType.DOOR;
		renderGroup = C.GROUP_POST_NORMAL;
		name = "Door";
		setSize(w, h);
		stats = new BasicStat(this);
	}

	public void draw(SpriteBatch batch, ShapeRenderer shapeBatch) {
		if (getTexture() == null) return;
		batch.draw(getTexture(), getX()-getTexture().getRegionWidth()/2, getY()-getTexture().getRegionHeight()/2, getTexture().getRegionWidth()/2, getTexture().getRegionHeight()/2, getTexture().getRegionWidth(), getTexture().getRegionHeight(), 1, 1, getA()+90, true);
	}
		
	@Override
	public void use() {
		if (isLocked || destroyed)
			return;
		body.getFixtureList().first().setSensor(!isOpened());
		image = "door1";
		if (isOpened())
			setTexture(ResourceManager.getImage(image+"_open"));
		else
			setTexture(ResourceManager.getImage(image));
	}
	
	public boolean isOpened(){
		return body.getFixtureList().first().isSensor();
	}
	
	public void createBody(){
		body = BodyFactory.createDynamicBox(getX(), getY(), getW(),getH(), 0, 1000, false, 1000); 
		body.setUserData(this);
		use();
	}

	@Override
	public void hitted(float value, GameObject damager) {
		if (destroyed || isOpened())
			return;
		health-=value;
		if (health <= 0)
			doDie(damager);
		Log.info("Door","hitted");
	}

	@Override
	public void doDie(GameObject killer) {
		if (!isOpened()){
			setTexture(ResourceManager.getImage(image+"_open"));
			body.getFixtureList().first().setSensor(!isOpened());
		}
		destroyed = true;
	}

	public boolean isDead(){
		return destroyed;
	}
	
	@Override
	public int getHp() {
		return (int) health;
	}
	
}
