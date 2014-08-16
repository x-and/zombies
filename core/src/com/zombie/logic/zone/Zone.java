package com.zombie.logic.zone;

import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.physics.BodyFactory;
import com.physics.Physics;
import com.zombie.logic.enums.ObjectType;
import com.zombie.logic.object.GameObject;
import com.zombie.logic.object.PhysicObject;
import com.zombie.logic.object.live.Player;
import com.zombie.logic.quest.Quest;

/**���� - ���������� ����� ����, �������� ��� ��������� ����, ��� ������� ������.
 * ������������ �� ���� �������, �� ���� ��������, ��� �� ���������� ������� */
public class Zone extends PhysicObject{
	
	boolean containsPlayer = false;
	Array<GameObject> touched = new Array<GameObject>(false,20);
	
	public Zone(float x, float y) {
		super(x, y);
		type = ObjectType.ZONE;
	}
	
	void playerEntered(){
		containsPlayer = true;
		if (quests != null)
			for(Quest q : quests)
				q.onZoneEnter(this);
	}
	
	void playerExited(){
		containsPlayer = false; 
		if (quests != null)
			for(Quest q : quests)
				q.onZoneExited(this);
	}

	public void checkEnter(GameObject object) {
		if (object instanceof Player && !touched.contains(object, true))
			playerEntered();
		touched.add(object);
	}
	
	public void checkExit(GameObject object) {
		touched.removeValue(object, true);
		if (object instanceof Player && !touched.contains(object, true))
			playerExited();
	}
	
	public void createBody(){
		if (body != null)
			return;
		body  = BodyFactory.createEmptyStaticBody(0, 0);
		body.setUserData(this);
	}

	public void setRect(final Rectangle rect) {
		Physics.task(new Runnable(){
			@Override
			public void run() {
				BodyFactory.addStaticBox(body,rect.x+rect.width/2, rect.y+rect.height/2, rect.width, rect.height, 0).setSensor(true);
			} });
	}

	public void setEllipse(final Ellipse ell) {
		Physics.task(new Runnable(){
			@Override
			public void run() {
				BodyFactory.addStaticCircle(body,ell.x, ell.y, ell.width).setSensor(true);
			} });
	}
	
	//TODO
	public void setPoly(Polygon polygon) {

	}
}
