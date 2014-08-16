package com.zombie.logic.ai.event;

import com.badlogic.gdx.math.Vector2;
import com.zombie.logic.Faction;
import com.zombie.logic.object.GameObject;

public class Event {

	public static enum EventType{
		SOUND,SOCIAL
	}
	public Vector2 position;
	public GameObject owner;
	public Faction faction;
	public EventType type;
	
	public float getX() {
		return position.x;
	}
	
	public float getY() {
		return position.y;
	}
	
}
