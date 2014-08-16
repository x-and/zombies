package com.zombie.logic.ai.action;

import com.zombie.logic.ai.AI;
import com.zombie.logic.object.GameObject;
import com.zombie.logic.object.LiveObject;

public class ActionFlee extends Action {
	
	public ActionFlee(GameObject t) {
		super(t);
		type = ActionType.FLEE;
	}

	public void checkCondition(AI ai, LiveObject owner){
		super.checkCondition(ai, owner);
		if (target.dst(owner) > 400)
			done = true;
	}
}
