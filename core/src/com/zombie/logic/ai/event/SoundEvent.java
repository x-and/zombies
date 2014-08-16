package com.zombie.logic.ai.event;


public class SoundEvent extends Event {
	
	public float radius;
	
	public SoundEvent(){
		type = EventType.SOUND;
	}
}
