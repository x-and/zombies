package com.zombie.logic.object.vehicle.v2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.zombie.C;
import com.zombie.ZombieGame;

public class Tire {

	Body body;
	float maxForwardSpeed;
	float maxBackwardSpeed;
	float maxDriveForce;
	float maxLateralImpulse;

	Array<GroundAreaType> groundAreas;

	float currentTraction;

	public Tire(World world) {

		groundAreas = new Array<GroundAreaType>();

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		body = world.createBody(bodyDef);

		PolygonShape polygonShape = new PolygonShape();
		polygonShape.setAsBox(5f*C.WORLD_TO_BOX, 12.5f*C.WORLD_TO_BOX);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = 0.5f;
		fixtureDef.shape = polygonShape;
		fixtureDef.isSensor = true;
		fixtureDef.filter.categoryBits = Constants.TIRE;
		fixtureDef.filter.maskBits  = Constants.GROUND;
		Fixture fixture = body.createFixture(fixtureDef);
		fixture.setUserData(new CarTireType());

		body.setUserData(this);
		
		currentTraction = 1;
	}

	public void addGroundArea(GroundAreaType item) {
		groundAreas.add(item);
		updateTraction();
	}

	public void removeGroundArea(GroundAreaType item) {
		groundAreas.removeValue(item, false);
		updateTraction();
	}

	void setCharacteristics(float maxForwardSpeed, float maxBackwardSpeed,
			float maxDriveForce, float maxLateralImpulse) {
		this.maxForwardSpeed = maxForwardSpeed;
		this.maxBackwardSpeed = maxBackwardSpeed;
		this.maxDriveForce = maxDriveForce;
		this.maxLateralImpulse = maxLateralImpulse;
	}

	void updateTraction() {
		if (groundAreas.size == 0) {
			currentTraction = 1;
			return;
		}

		currentTraction = 0;

		for (GroundAreaType groundType : groundAreas) {
			if (groundType.frictionModifier > currentTraction) {
				currentTraction = groundType.frictionModifier;
			}
		}
	}

	Vector2 getLateralVelocity() {
		Vector2 currentRightNormal = body.getWorldVector(new Vector2(1, 0));
		return CarMath.multiply(
				currentRightNormal.dot(body.getLinearVelocity()),
				currentRightNormal);
	}

	Vector2 getForwardVelocity() {
		Vector2 currentForwardNormal = body.getWorldVector(new Vector2(0, 1));
		return CarMath.multiply(
				currentForwardNormal.dot(body.getLinearVelocity()),
				currentForwardNormal);
	}

	public void updateFriction() {
		Vector2 impulse = CarMath.multiply(body.getMass(),
				CarMath.minus(getLateralVelocity()));

		if (impulse.len() > maxLateralImpulse) {
			impulse = CarMath.multiply(impulse,
					maxLateralImpulse / impulse.len());
		}
		body.applyLinearImpulse(CarMath.multiply(currentTraction, impulse),
				body.getWorldCenter(), true);
		body.applyAngularImpulse(currentTraction * 0.1f * body.getInertia()
				* -body.getAngularVelocity(), true);

		Vector2 currentForwardNormal = getForwardVelocity();
		float currentForwardSpeed = CarMath.normalize(currentForwardNormal);
		float dragForceMagnitude = -2 * currentForwardSpeed;
		body.applyForce(CarMath.multiply(currentTraction * dragForceMagnitude,
				currentForwardNormal), body.getWorldCenter(), true);
	}

	void updateDrive() {
		float desiredSpeed = 0;

		if(ZombieGame.input.playerUp){
			desiredSpeed = maxForwardSpeed;
		} else if(ZombieGame.input.playerDown){
			desiredSpeed = maxBackwardSpeed;
		} else {
//			desiredSpeed = 0;
		}
		
		Vector2 currentForwardNormal = body.getWorldVector(new Vector2(0, 1));
		float currentSpeed = getForwardVelocity().dot(currentForwardNormal);

		float force = 0;

		if (desiredSpeed > currentSpeed) {
			force = maxDriveForce;
		} else if (desiredSpeed < currentSpeed) {
			force = (-maxDriveForce);
		} else {
			return;
		}
		body.applyForce(
				CarMath.multiply(currentTraction * force, currentForwardNormal),
				body.getWorldCenter(), true);
	}
	
	void updateTurn(CarMoves moves){
		float desiredTorque = 0;
		
		switch(moves){
			case Left: 
				desiredTorque = 15; 
				break;
			case Right: 
				desiredTorque = -15; 
				break;
			default:
				return;
		}
		body.applyTorque(desiredTorque, true);
	}
}
