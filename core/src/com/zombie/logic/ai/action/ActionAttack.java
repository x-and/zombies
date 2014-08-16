package com.zombie.logic.ai.action;

import com.zombie.logic.ai.AI;
import com.zombie.logic.object.GameObject;
import com.zombie.logic.object.LiveObject;

public class ActionAttack extends Action {

	public boolean started = false;
	
	public ActionAttack(GameObject t){
		super(t);
		type = ActionType.ATTACK;
	}
	
	public void checkCondition(AI ai, LiveObject owner){
//		super.checkCondition(ai, owner);
//		if (target.position.dst(owner.position) < distance)
//			done = true;
	}
	
}
