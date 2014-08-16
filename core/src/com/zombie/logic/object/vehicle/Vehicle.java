package com.zombie.logic.object.vehicle;

import static com.zombie.logic.enums.ObjectType.VEHICLE;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.droidinteractive.box2dlight.Light;
import com.zombie.SoundInfo;
import com.zombie.ZombieGame;
import com.zombie.logic.enums.ObjectType;
import com.zombie.logic.object.GameObject;
import com.zombie.logic.object.LiveObject;
import com.zombie.logic.object.PhysicObject;
import com.zombie.logic.object.interfaces.Hitable;
import com.zombie.logic.object.stat.VehicleStat;
import com.zombie.util.SoundUtils;

public abstract class Vehicle extends PhysicObject implements Hitable{

	SoundInfo engine;
	long engineSoundId = -1;
	float enginePitch = 1f;
	private LiveObject driver;
	List<LiveObject> passangers = new ArrayList<LiveObject>();
	int maxPassangers = 2;
	float maxSpeed, power;
	boolean headLightsEnabled = true;
	boolean rearLightsEnabled = true;
	boolean isDead = false;
	public byte steer = Control.STEER_NONE;
	public byte accelerate = Control.ACC_NONE;

	public List<Wheel> wheels,poweredWheels,revolvingWheels;
	
	Light[] frontLights;
	Light[] rearLights;
	
	public Vehicle(Vector2 pos, float w, float h, float maxSpeed2, float power2) {
		super(pos.x, pos.y);
		setSize(w,h);
		maxSpeed = maxSpeed2;
		power = power2;
		type = VEHICLE;
	}
	

	protected abstract void disableLights();
	protected abstract void enableLights();
	public abstract void handleCollide(GameObject obj,float speed);
	
	public void dispose(){
		super.dispose();
		for(Wheel w : wheels)
			w.remove();
	}
	
	public void update(float delta){
		super.update(delta);
		if (isDead()){
			if (getDriver() != null)
				getDriver().vehicleExited(this);
			
			for(LiveObject passanger : passangers){
				passanger.vehicleExited(this);
			}
		}
	}
	
	public float getSpeedKMH(){
		if (body == null)
			return 0;
	    Vector2 velocity=body.getLinearVelocity();
	    float len = velocity.len();
	    return (len/1000)*3600*2;
	}
	
	public void control() {
		accelerate = Control.ACC_NONE;
		steer = Control.STEER_NONE;
		if (ZombieGame.input.playerUp)
			accelerate = Control.ACC_ACCELERATE;
		else if (ZombieGame.input.playerDown)
			accelerate = Control.ACC_BRAKE;
		if (ZombieGame.input.playerLeft)
			steer = Control.STEER_LEFT;
		else if (ZombieGame.input.playerRight)
			steer = Control.STEER_RIGHT;
	}

	public LiveObject getDriver() {
		return driver;
	}

	public void setDriver(LiveObject live) {
		if (isDead() && live != null)
			return;
		driver = live;
		if (driver != null) {
			if (engineSoundId == -1)
				engineSoundId = SoundUtils.loopSound(engine, pos.current);
			enableLights();
		} else {
			if (engineSoundId != -1)
				engine.sound.stop(engineSoundId);
			engineSoundId = -1;
			disableLights();
			accelerate = Control.ACC_NONE;
			steer = Control.STEER_NONE;
		}
	}
	
	public void exited(LiveObject liveObject) {
		if (getDriver() == liveObject)
			setDriver(null);
	}
	
	@Override
	public void hitted(float value, GameObject damager) {
		if (isDead())
			return;
		getStat().hp-=value;

		if (getStat().hp <= 0)
			doDie(damager);
		
		System.out.println("vehicle hitted " + value + " hp " +getStat().hp);
	}

	@Override
	public void doDie(GameObject killer) {
		if (engineSoundId != -1)
			engine.sound.stop(engineSoundId);
		isDead = true;
		accelerate = Control.ACC_NONE;
		steer = Control.STEER_NONE;
		if (killer != null && killer.type == ObjectType.LIVE)
			((LiveObject) killer).getAI().onKill(this);
		//TODO create effects
		//TODO car stay damaged as body, but none of damage taken\given
		//TODO exit all passengers, if passenger.hp > 0
	}
	
	public boolean isDead(){
		return isDead;
	}
	
	public VehicleStat getStat(){
		if (stats == null)
			stats = new VehicleStat(this);
		return (VehicleStat) stats;
	}

	public int getHp() {
		return getStat().hp;
	}

	public int getMaxHp() {
		return getStat().maxHp;
	}


	public float getMaxSpeed() {
		return maxSpeed;
	}


	public void setMaxSpeed(float maxSpeed) {
		this.maxSpeed = maxSpeed;
	}
}
