package com.zombie.logic.object.vehicle.v2;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.utils.Array;
import com.physics.Physics;
import com.zombie.C;
import com.zombie.ZombieGame;
import com.zombie.logic.object.PhysicObject;


public class Car extends PhysicObject{
	
	Array<Tire> tires;
	RevoluteJoint leftJoint, rightJoint;

	public Car() {
		tires = new Array<Tire>();
	}

	public void update(float delta) {
		super.update(delta);
		if (body == null || leftJoint == null || rightJoint == null)
			return;
		for (Tire tire : tires) {
			tire.updateFriction();
		}
		for (Tire tire : tires) {
			tire.updateDrive();
		}

		float lockAngle = 35 * MathUtils.degRad;
		float turnSpeedPerSec = 160 * MathUtils.degRad;
		float turnPerTimeStep = turnSpeedPerSec / 60.0f;
		float desiredAngle = 0;

		if(ZombieGame.input.playerLeft){
			desiredAngle = lockAngle;
		} else if(ZombieGame.input.playerRight){
			desiredAngle = -lockAngle;
		}
		
		float angleNow = leftJoint.getJointAngle();
		float angleToTurn = desiredAngle - angleNow;
		angleToTurn = CarMath.clamp(angleToTurn, -turnPerTimeStep, turnPerTimeStep);
		float newAngle = angleNow + angleToTurn;
		
		leftJoint.setLimits(newAngle, newAngle);
		rightJoint.setLimits(newAngle, newAngle);
	}

	@Override
	public void createBody() {

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(new Vector2(30*C.WORLD_TO_BOX, 30*C.WORLD_TO_BOX));
		body = Physics.world.createBody(bodyDef);
		body.setAngularDamping(1);
		body.setLinearDamping(0);
		Vector2[] vertices = new Vector2[8];
		vertices[0] = new Vector2(15f*C.WORLD_TO_BOX, 0);
		vertices[1] = new Vector2(30*C.WORLD_TO_BOX, 25f*C.WORLD_TO_BOX);
		vertices[2] = new Vector2(28f*C.WORLD_TO_BOX, 55f*C.WORLD_TO_BOX);
		vertices[3] = new Vector2(10*C.WORLD_TO_BOX, 100*C.WORLD_TO_BOX);
		vertices[4] = new Vector2(-10*C.WORLD_TO_BOX, 100*C.WORLD_TO_BOX);
		vertices[5] = new Vector2(-28f*C.WORLD_TO_BOX, 55f*C.WORLD_TO_BOX);
		vertices[6] = new Vector2(-30*C.WORLD_TO_BOX, 25f*C.WORLD_TO_BOX);
		vertices[7] = new Vector2(-15f*C.WORLD_TO_BOX, 0);

		PolygonShape polygonShape = new PolygonShape();
		polygonShape.set(vertices);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = polygonShape;
		fixtureDef.density = 0.1f;
		fixtureDef.filter.categoryBits = Constants.CAR;
		fixtureDef.filter.maskBits = Constants.GROUND;
		
		body.createFixture(fixtureDef);

		RevoluteJointDef jointDef = new RevoluteJointDef();
		jointDef.bodyA = body;
		jointDef.enableLimit = true;
		jointDef.lowerAngle = 0;
		jointDef.upperAngle = 0;
		jointDef.localAnchorB.set(0, 0);

		float maxForwardSpeed = 100;
		float maxBackwardSpeed = -25;
		float backTireMaxDriveForce = 3;
		float frontTireMaxDriveForce = 8;
		float backTireMaxLateralImpulse = 0.85f;
		float frontTireMaxLateralImpulse = 0.75f;

		Tire tire = new Tire(Physics.world);
		tire.setCharacteristics(maxForwardSpeed, maxBackwardSpeed,
				backTireMaxDriveForce, backTireMaxLateralImpulse);
		jointDef.bodyB = tire.body;
		jointDef.localAnchorA.set(-30*C.WORLD_TO_BOX, 7.5f*C.WORLD_TO_BOX);
		Physics.world.createJoint(jointDef);
		tires.add(tire);

		tire = new Tire(Physics.world);
		tire.setCharacteristics(maxForwardSpeed, maxBackwardSpeed,
				backTireMaxDriveForce, backTireMaxLateralImpulse);
		jointDef.bodyB = tire.body;
		jointDef.localAnchorA.set(30*C.WORLD_TO_BOX, 7.5f*C.WORLD_TO_BOX);
		Physics.world.createJoint(jointDef);
		tires.add(tire);

		tire = new Tire(Physics.world);
		tire.setCharacteristics(maxForwardSpeed, maxBackwardSpeed,
				frontTireMaxDriveForce, frontTireMaxLateralImpulse);
		jointDef.bodyB = tire.body;
		jointDef.localAnchorA.set(-30*C.WORLD_TO_BOX, 85f*C.WORLD_TO_BOX);
		leftJoint = (RevoluteJoint)Physics.world.createJoint(jointDef);
		tires.add(tire);

		tire = new Tire(Physics.world);
		tire.setCharacteristics(maxForwardSpeed, maxBackwardSpeed,
				frontTireMaxDriveForce, frontTireMaxLateralImpulse);
		jointDef.bodyB = tire.body;
		jointDef.localAnchorA.set(30*C.WORLD_TO_BOX, 85f*C.WORLD_TO_BOX);
		rightJoint = (RevoluteJoint)Physics.world.createJoint(jointDef);
		tires.add(tire);
	}
}
