package com.zombie.logic.object.vehicle;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.droidinteractive.box2dlight.ConeLight;
import com.droidinteractive.box2dlight.Light;
import com.manager.LightManager;
import com.manager.ResourceManager;
import com.physics.Physics;
import com.zombie.C;
import com.zombie.logic.enums.ObjectType;
import com.zombie.logic.object.GameObject;
import com.zombie.logic.object.LiveObject;
import com.zombie.logic.object.PhysicObject;
import com.zombie.logic.object.StaticObject;

public class Car extends Vehicle{

	float wheelAngle = 0;
	
	public Car(float w, float h, Vector2 pos, float angle, float power, float maxSteerAngle, float maxSpeed) {	
		super(pos,w,h,maxSpeed,power);
		getStat().maxSteering = maxSteerAngle;
		getStat().defence = 5;
		getStat().setMaxHp(1000);
		image = "car0";
		setTexture(ResourceManager.getImage(image));
		engine = ResourceManager.getSound("engine");
		enableLights();
	}
	
	public void createBody(){
		//init body 
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(pos.current.cpy().scl(C.WORLD_TO_BOX));
		bodyDef.angle = getA();
		bodyDef.angularDamping = 0.1f;
		body = Physics.world.createBody(bodyDef);
		
		//init shape
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = 10.0f;
		fixtureDef.friction = 1f; //friction when rubbing against other shapes
		fixtureDef.restitution  = 0f; //amount of force feedback when hitting something. >0 makes the car bounce off, it's fun!
		PolygonShape carShape = new PolygonShape();
		carShape.setAsBox(getW() / 2 * C.WORLD_TO_BOX, getH() / 2 * C.WORLD_TO_BOX);
		fixtureDef.shape = carShape;
		body.createFixture(fixtureDef);
		body.setUserData(this);
		//initialize wheels
		wheels = new ArrayList<Wheel>();
		wheels.add(new Wheel(Physics.world, this, -getW() / 2 * C.WORLD_TO_BOX, (-getH() +32) / 2 * C.WORLD_TO_BOX, 8 * C.WORLD_TO_BOX, 24 * C.WORLD_TO_BOX, true,  true)); //top left
		wheels.add(new Wheel(Physics.world, this, getW() / 2 * C.WORLD_TO_BOX, (-getH() +32) / 2 * C.WORLD_TO_BOX, 8 * C.WORLD_TO_BOX, 24 * C.WORLD_TO_BOX, true,  true)); //top right
		wheels.add(new Wheel(Physics.world, this, -getW() / 2 * C.WORLD_TO_BOX, (getH() -32) / 2 * C.WORLD_TO_BOX, 8 * C.WORLD_TO_BOX, 24 * C.WORLD_TO_BOX, false,  false)); //back left
		wheels.add(new Wheel(Physics.world, this, getW() / 2 * C.WORLD_TO_BOX, (getH() -32) / 2 * C.WORLD_TO_BOX, 8 * C.WORLD_TO_BOX, 24 * C.WORLD_TO_BOX, false,  false)); //back right
		//init lights
		disableLights();
	}
	
	public List<Wheel> getPoweredWheels () {
		if (poweredWheels != null)
			return poweredWheels;
		poweredWheels = new ArrayList<Wheel>();
		for (Wheel wheel:this.wheels) {
			if (wheel.powered)
				poweredWheels.add(wheel);
		}
		return poweredWheels;
	}
    /*   returns car's velocity vector relative to the car */
	public Vector2 getLocalVelocity() {
		return this.body.getLocalVector(this.body.getLinearVelocityFromLocalPoint(new Vector2(0, 0)));
	}
	
	public List<Wheel> getRevolvingWheels () {
		if (revolvingWheels != null)
			return revolvingWheels;
		revolvingWheels = new ArrayList<Wheel>();
		for (Wheel wheel:this.wheels) {
			if (wheel.revolving)
				revolvingWheels.add(wheel);
		}
		return revolvingWheels;
	}
    /*   speed - speed in kilometers per hour  */
	public void setSpeed (float speed){
	    Vector2 velocity=this.body.getLinearVelocity();
	    velocity=velocity.nor();
	    velocity=new Vector2(velocity.x*((speed*1000.0f)/3600.0f),
	    				velocity.y*((speed*1000.0f)/3600.0f));
	    this.body.setLinearVelocity(velocity);
	}
	
	Vector2 forceVector = new Vector2();
	
	Runnable physics = new Runnable(){

		@Override
		public void run() {
	        for(Wheel wheel: wheels){
	        	wheel.killSidewaysVelocity();
	        }
	        
	        for(Wheel wheel:getRevolvingWheels()) {
	        	wheel.setAngle(wheelAngle+getBodyA());
	        }
	        
	        for(Wheel wheel : getPoweredWheels()){
	        	 wheel.body.applyForceToCenter(wheel.body.getWorldVector(new Vector2(forceVector.x, forceVector.y)),true);
	        }
	        pos.set(getBodyX(),getBodyY(),getBodyA());
	        updateLights();
		}};
		
	@Override
	public void update (float delta){
	    super.update(delta);
	    if (body == null)
	    	return;
        Physics.task(physics);
	    

       float scaleFactor = (1-getSpeedKMH()/maxSpeed)*1.2f;
//        scaleFactor = Math.max(0.25f,scaleFactor);
//		calculate the change in wheel's angle for this updates
        float incr=(getStat().maxSteering) * delta;
        if(steer==Control.STEER_LEFT){
            wheelAngle=Math.min(wheelAngle+incr, getStat().maxSteering*scaleFactor); //increment angle without going over max steer
        } else if(steer==Control.STEER_RIGHT){
            wheelAngle=Math.max(wheelAngle-incr, -getStat().maxSteering*scaleFactor); //decrement angle without going over max steer
        } else{
        	if (wheelAngle > 0)
        		wheelAngle=Math.max(0,wheelAngle-incr);
        	else 
        		wheelAngle=Math.min(0,wheelAngle+incr);
        }
        stopLights(false);
        //if accelerator is pressed down and speed limit has not been reached, go forwards
        if((accelerate==Control.ACC_ACCELERATE) && (getSpeedKMH() < maxSpeed)){
        	forceVector.set(0, -1);
    		if (engineSoundId != -1)
    			engine.sound.setPitch(engineSoundId, enginePitch = Math.min(enginePitch+=0.01f, 2.5f));

        } else if(accelerate==Control.ACC_BRAKE){
        	stopLights(true);
            //braking, but still moving forwards - increased force
            if(getLocalVelocity().y<0){
            	forceVector.set(0f, 1.6f);
            //going in reverse - less force
           	if (engineSoundId != -1)
        		engine.sound.setPitch(engineSoundId, enginePitch = Math.max(enginePitch-=0.02f, 0.25f));

            } else if (getSpeedKMH() < maxSpeed/2) {
            	forceVector.set(0f, 0.3f);
           		if (engineSoundId != -1)
        			engine.sound.setPitch(engineSoundId, enginePitch = Math.min(enginePitch+=0.02f, 2.5f));
            }
            else
            	forceVector.set(0f, 0f);

        } else if (accelerate==Control.ACC_NONE ) {
          	if (engineSoundId != -1)
          		enginePitch-=0.01f;
          		enginePitch = Math.max(enginePitch, 1);
        		engine.sound.setPitch(engineSoundId, enginePitch);

        	//slow down if not accelerating
        	forceVector.set(0, 0);
            if (getSpeedKMH()<0.3f)
                setSpeed(0);
            if (getLocalVelocity().y<0)
        		forceVector.set(0, 0.9f);
            else if (getLocalVelocity().y>0)
        		forceVector.set(0, -0.9f);
        } else 
        	forceVector.set(0, 0);
        forceVector.scl(power);
	}
	
	private void stopLights(boolean b) {
		if (!b) {
			rearLights[0].setColor(Color.ORANGE);
			rearLights[1].setColor(Color.ORANGE);
			rearLights[0].setDistance(16);
			rearLights[1].setDistance(16);	
		} else { 
    		rearLights[0].setColor(Color.RED);
    		rearLights[1].setColor(Color.RED);
    		rearLights[0].setDistance(30);
    		rearLights[1].setDistance(30);
		}
	}

	private void updateLights() {
		float frontX = getH()/2*MathUtils.cos(MathUtils.degRad*(getA()-90));
		float frontY = getH()/2*MathUtils.sin(MathUtils.degRad*(getA()-90));
		
		float frontX1 = (getW()/2-4)*MathUtils.cos(MathUtils.degRad*(getA()-180));
		float frontY1 = (getW()/2-4)*MathUtils.sin(MathUtils.degRad*(getA()-180));
		
		frontLights[0].setPosition(getX() + frontX+frontX1,getY() + frontY+frontY1);
		frontLights[0].setDirection(getA()-90);
		frontLights[1].setPosition(getX()+ frontX - frontX1,getY() + frontY - frontY1);
		frontLights[1].setDirection(getA()-90);
		
		rearLights[0].setPosition(getX() - frontX + frontX1,getY() - frontY + frontY1);
		rearLights[0].setDirection(getA()+90);
		rearLights[1].setPosition(getX() - frontX - frontX1,getY() - frontY - frontY1);
		rearLights[1].setDirection(getA()+90);		

	}

	public void draw(SpriteBatch batch, ShapeRenderer shapeBatch) {
		batch.draw(getTexture(), getX()-getH()/2, getY()-getW()/2, getH()/2, getW()/2, getH(), getW(), 1.2f, 1.2f, getA()-90, true);
	}
	
	protected void disableLights() {
		frontLights[0].setActive(false);
		frontLights[1].setActive(false);
		rearLights[0].setActive(false);
		rearLights[1].setActive(false);
	}

	protected void enableLights() {
		if (frontLights == null){
			frontLights = new Light[2];
			frontLights[0] = new ConeLight(LightManager.handler, 8, Light.DefaultColor, 500, 0, 0, 90, 45);
			frontLights[1] = new ConeLight(LightManager.handler, 8, Light.DefaultColor, 500, 0, 0, 90, 45);
		}
		
		if (rearLights == null){
			rearLights = new Light[2];
			rearLights[0] = new ConeLight(LightManager.handler, 8, Color.RED, 32, 0, 0, 90, 30);
			rearLights[1] = new ConeLight(LightManager.handler, 8, Color.RED, 32, 0, 0, 90, 30);
		}
		
		frontLights[0].setActive(true);
		frontLights[1].setActive(true);
		rearLights[0].setActive(true);
		rearLights[1].setActive(true);
	}

	@Override
	public void handleCollide(GameObject obj,float speed) {
		if (Math.abs(body.getLinearVelocity().len()) < 0.5f)
			return;
		speed += Math.abs(body.getLinearVelocity().len());
		float damage = body.getMass()*speed*2;
		if (isDead())
			damage/=4;
		float selfdamage = 0;
		
		if (obj == null) {
			selfdamage = damage*2;
		} else if (obj.type == ObjectType.STATIC) {
			push((StaticObject)obj,(int) damage);
			selfdamage = ((PhysicObject)obj).body.getMass()*speed*20;
		} else if (obj.type == ObjectType.LIVE) {
//			System.out.println(obj + "   " + damage);
			((LiveObject)obj).hitted((int) damage, getDriver() != null? getDriver():this);
			selfdamage = ((PhysicObject)obj).body.getMass()*speed*10;
		} else if (obj.type == ObjectType.VEHICLE){
			selfdamage = 0;
		}
		hitted(selfdamage,obj);
	}
	
	public void push(StaticObject obj,int damage) {
		setVelocity(body.getLinearVelocity().len()*200);
		super.push(obj, damage);
	}

}
