package com.physics;

import static com.zombie.logic.enums.ObjectType.BULLET;
import static com.zombie.logic.enums.ObjectType.EXPLOSIVE;
import static com.zombie.logic.enums.ObjectType.STATIC;
import static com.zombie.logic.enums.ObjectType.VEHICLE;
import static com.zombie.logic.enums.ObjectType.ZONE;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.esotericsoftware.minlog.Log;
import com.zombie.effect.RicochetEffect;
import com.zombie.logic.Formulas;
import com.zombie.logic.object.Bullet;
import com.zombie.logic.object.ExplosiveObject;
import com.zombie.logic.object.GameObject;
import com.zombie.logic.object.StaticObject;
import com.zombie.logic.object.interfaces.Hitable;
import com.zombie.logic.object.vehicle.Vehicle;
import com.zombie.logic.zone.Zone;


public class ContactListener implements com.badlogic.gdx.physics.box2d.ContactListener {

	@Override
	public void beginContact(Contact contact) {
		Body b1,b2;
		b1 = contact.getFixtureA().getBody();
		b2 = contact.getFixtureB().getBody();
		GameObject o1, o2;
		o1 = (GameObject) contact.getFixtureA().getBody().getUserData();
		o2 = (GameObject) contact.getFixtureB().getBody().getUserData();

		// only static object have null user data
		if (o1 == null && o2 == null)
			return;
		
		if (o1 != null){
			if (o1.type == BULLET){
				if (bulletStaticCheck(b1,b2,contact)){
					return;
				}
				else if (o2 != null){
					checkForDamage(o1,o2,b1,b2);
					return;
				}
			} else if (o1.type == EXPLOSIVE){
				if (((ExplosiveObject) o1).blowOnContact){
					if (o2 != null && o1.owner == o2)
						return;
					else
						((ExplosiveObject) o1).blow = true;
					return;
				}
			} else if (o1.type == VEHICLE){
				float vel = Math.abs(b2.getLinearVelocity().len());
				((Vehicle) o1).handleCollide(o2,vel);
			} else if (o1.type == ZONE){
				if (o2 != null){
					Zone z = (Zone) o1;
					z.checkEnter(o2);
				}
			}
		}
		if (o2 != null){
			if (o2.type == BULLET){
				if (bulletStaticCheck(b2,b1,contact))
					return;
				else if (o1 != null){
					checkForDamage(o2,o1,b2,b1);
					return;
				}
			} else if (o2.type == EXPLOSIVE){
				if (((ExplosiveObject) o2).blowOnContact){
					if (o1 != null && o2.owner == o1)
						return;
					else
						((ExplosiveObject) o2).blow = true;
					return;
				}
			} else if (o2.type == VEHICLE){
				float vel = Math.abs(b1.getLinearVelocity().len());
				((Vehicle) o2).handleCollide(o1,vel);
			} else if (o2.type == ZONE){
				if (o1 != null){
					Zone z = (Zone) o2;
					z.checkEnter(o1);
				}
			}
		}
	}

	private void checkForDamage(GameObject bullet, GameObject damaged,Body b1,Body b2) {
		if (((Bullet)bullet).damage == 0)
			return;
		if (damaged.type == STATIC || damaged.type == VEHICLE){
			createRicochetEffect(b1,b2,(Bullet) bullet);
			if (damaged.type == STATIC)
				((Bullet) bullet).push((StaticObject) damaged);
		}
		if (!(damaged instanceof Hitable))
			return;
		Hitable live = (Hitable) damaged;
		Bullet b = (Bullet) bullet;
		b.lastIntersected = (GameObject) live;
		if (Formulas.calcMissChance(b,live))
			return;
		b.doDamage(live);
	}

	private boolean bulletStaticCheck(Body bullet, Body body, Contact contact) {
		if (body.getType() != BodyType.DynamicBody && !body.getFixtureList().first().isSensor()){
				if (bullet.getUserData() != null)
					((Bullet)bullet.getUserData()).lifeTime = 0;
				createRicochetEffect(bullet,body,((Bullet)bullet.getUserData()));
				return true;
			}
		Log.info("ContactListener", "bulletStaticCheck false");
		return false;
	}

	void createRicochetEffect(Body b1,Body b2,Bullet b){
		new RicochetEffect(b1, b2,((Bullet)b1.getUserData()));
	}

	@Override
	public void endContact(Contact contact) {
		if (contact.getFixtureA() == null ||contact.getFixtureB() == null)
			return;
		Body b1,b2;
		b1 = contact.getFixtureA().getBody();
		b2 = contact.getFixtureB().getBody();
		GameObject o1, o2;
		o1 = (GameObject) b1.getUserData();
		o2 = (GameObject) b2.getUserData();
		if (o1 == null && o2 == null)
			return;
		
		if (o1 != null){
			if (o1.type == ZONE){
				if (o2 != null){
					Zone z = (Zone) o1;
					z.checkExit(o2);
				}
			}
		}
		if (o2 != null){
			if (o2.type == ZONE){
				if (o1 != null){
					Zone z = (Zone) o2;
					z.checkExit(o1);
				}
			}
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
	}

}
