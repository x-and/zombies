package com.zombie.logic.object.vehicle;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.physics.Physics;
import com.zombie.logic.object.PhysicObject;

public class Wheel extends PhysicObject{	
	/**
	 * Box2d works best with small values. If you use pixels directly you will
	 * get weird results -- speeds and accelerations not feeling quite right.
	 * Common practice is to use a constant to convert pixels to and from
	 * "meters".
	 */
	public static final float PIXELS_PER_METER = 60.0f;
	
	public Vehicle car;//car this wheel belongs to	
	private float width; // width in meters
	private float length; // length in meters
	public boolean revolving; // does this wheel revolve when steering?
	public boolean powered; // is this wheel powered?
	public Body body;

	public Wheel(World world, Car car, float posX, float posY, float width, float length,
			boolean revolving, boolean powered) {
		super();
		this.car = car;
		this.width = width;
		this.length = length;
		this.revolving = revolving;
		this.powered = powered;
		
		//init body 
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(car.body.getWorldPoint(new Vector2(posX, posY)));
		bodyDef.angle = car.body.getAngle();
		body = world.createBody(bodyDef);
		//init shape
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = 10.0f;
		fixtureDef.isSensor=true; //wheel does not participate in collision calculations: resulting complications are unnecessary
		PolygonShape wheelShape = new PolygonShape();
		wheelShape.setAsBox(this.width/2, this.length/2);
		fixtureDef.shape = wheelShape;
		this.body.createFixture(fixtureDef);
		wheelShape.dispose();
		
	    //create joint to connect wheel to body
	    if(this.revolving){
	    	RevoluteJointDef jointdef=new RevoluteJointDef();
	        jointdef.initialize(this.car.body, this.body, this.body.getWorldCenter());
	        jointdef.enableMotor=false; //we'll be controlling the wheel's angle manually
		    world.createJoint(jointdef);
	    }else{
	    	PrismaticJointDef jointdef=new PrismaticJointDef();
	        jointdef.initialize(this.car.body, this.body, this.body.getWorldCenter(), new Vector2(1, 0));
	        jointdef.enableLimit=true;
	        jointdef.lowerTranslation=jointdef.upperTranslation=0;
		    world.createJoint(jointdef);
	    }
	}
	
	public void setAngle (float angle){
		body.setTransform(body.getWorldCenter().x, body.getWorldCenter().y, angle*MathUtils.degRad);
	}
    /*returns get velocity vector relative to car   */
	public Vector2 getLocalVelocity () {
	    return car.body.getLocalVector(car.body.getLinearVelocityFromLocalPoint(body.getPosition()));
	}
    /*  returns a world unit vector pointing in the direction this wheel is moving */
	public Vector2 getDirectionVector () {
		Vector2 directionVector;
		if (this.getLocalVelocity().y > 0)
			directionVector = new Vector2(0,1);
		else
			directionVector = new Vector2(0,-1);
			
		return directionVector.rotate((float) Math.toDegrees(this.body.getAngle()));	    
	}
    /*substracts sideways velocity from this wheel's velocity vector and returns the remaining front-facing velocity vector */
	public Vector2 getKillVelocityVector (){
	    Vector2 velocity = body.getLinearVelocity();
	    Vector2 sidewaysAxis = getDirectionVector();
	    float dotprod = velocity.dot(sidewaysAxis);
	    return new Vector2(sidewaysAxis.x*dotprod, sidewaysAxis.y*dotprod);
	}
    /*removes all sideways velocity from this wheels velocity */
	public void killSidewaysVelocity (){
	    body.setLinearVelocity(getKillVelocityVector());
	}

	public void remove() {
		if (body != null){
			Physics.world.destroyBody(body);
			body.setUserData(null);
			body = null;
		}
	}

	@Override
	public void createBody() {
		// TODO Auto-generated method stub
		
	}
}
