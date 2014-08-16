package com.zombie.logic.ai.event;

import com.zombie.logic.ai.AI;

public class SocialEvent extends Event {
	
	public static final byte ATTACKING = 0;
	public static final byte INTEREST = 1;
	public static final byte HITTED = 2;
	public static final byte FLEEING = 3;
	public static final byte DYING = 4;

	public byte socialType  = ATTACKING;
	
	public SocialEvent(AI ai) {
		owner = ai.owner;
		faction = ai.owner.faction;
		type = EventType.SOCIAL;
	}

}
