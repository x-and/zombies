package com.physics;

import static com.zombie.logic.enums.ObjectType.BULLET;
import static com.zombie.logic.enums.ObjectType.DOOR;
import static com.zombie.logic.enums.ObjectType.EXPLOSIVE;
import static com.zombie.logic.enums.ObjectType.LIVE;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.zombie.logic.object.Bullet;
import com.zombie.logic.object.GameObject;
import com.zombie.logic.object.LiveObject;

public class ContactFilter implements com.badlogic.gdx.physics.box2d.ContactFilter {

	public boolean shouldCollide(Fixture a, Fixture b){
		GameObject o1,o2;
		o1 = (GameObject) a.getBody().getUserData();
		o2 = (GameObject) b.getBody().getUserData();
		
		if (o1 != null && o2 != null){
			if (o1.owner == o2 || o2.owner == o1)
				return false;
			if (o1.type == BULLET && o2.type == BULLET)
				return false;
			if (o1.type == EXPLOSIVE && o2.type == EXPLOSIVE)
				return false;
			if ((o1.type == BULLET || o1.type == EXPLOSIVE) && (b.isSensor() || o1.owner == o2))
				return false;
			if ((o2.type == BULLET || o2.type == EXPLOSIVE) && (a.isSensor() || o2.owner == o1))
				return false;			
		}	
		if (o1 != null && o1.type == BULLET)
			return bulletCheck((Bullet) o1,o2);
		else if (o2 != null && o2.type == BULLET)
			return bulletCheck((Bullet) o2,o1);
		if (o1 != null && o1.type == DOOR && o2 == null)
			return false;
		else if (o2 != null && o2.type == DOOR && o1 == null)
			return false;
		return true;
	}

	private boolean bulletCheck(Bullet bul, GameObject object) {
		if (bul.strongBullet && bul.lastIntersected != null && bul.lastIntersected == object)
			return false;
		else if (object != null && object.type == LIVE && ((LiveObject) object).isDead())
			return false;
		return true;
	}
}
