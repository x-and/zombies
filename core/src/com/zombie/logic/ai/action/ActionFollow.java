package com.zombie.logic.ai.action;

import com.manager.TimeManager;
import com.zombie.C;
import com.zombie.logic.ai.AI;
import com.zombie.logic.object.GameObject;
import com.zombie.logic.object.LiveObject;

public class ActionFollow extends Action {

	public static int distance = C.TILESIZE;
	
	public ActionFollow(GameObject t){
		super(t);
		endTime = TimeManager.getLongTime() + 1000;
		type = ActionType.FOLLOW;
	}
	
	public void checkCondition(AI ai, LiveObject owner){
		super.checkCondition(ai, owner);
//		System.out.println(target.position.dst(owner.position));
		if (target.dst(owner) < distance)
			done = true;
	}
	
}
