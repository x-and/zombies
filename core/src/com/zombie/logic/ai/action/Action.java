package com.zombie.logic.ai.action;

import com.manager.TimeManager;
import com.zombie.logic.ai.AI;
import com.zombie.logic.object.GameObject;
import com.zombie.logic.object.LiveObject;

public class Action implements Cloneable {

	public boolean done = false;
	public boolean interrupted = false;
	public GameObject target;
	public ActionType type = ActionType.WAIT;
	// in game time  - TimeManager.getLongTime();
	public long endTime = -1;
	
	public static final Action WAIT = new Action(null);

	public Action(GameObject target){
		this.target = target;
	}
	
	public Action(){
		endTime = TimeManager.getLongTime() + 1000;
	}
	
	public Action copy() {
		try {
			return (Action) clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void checkCondition(AI ai, LiveObject owner){
		if (endTime != -1 && endTime < TimeManager.getLongTime())
			done = true;
	}
	
	
	public Action setTime(long l){
		endTime = l;
		return this;
	}
}
