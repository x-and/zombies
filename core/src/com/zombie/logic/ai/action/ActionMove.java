package com.zombie.logic.ai.action;

import com.badlogic.gdx.math.Vector2;
import com.manager.TimeManager;
import com.zombie.C;
import com.zombie.logic.ai.AI;
import com.zombie.logic.object.LiveObject;

public class ActionMove extends Action {

	public Vector2 coords;
	float distance = C.TILESIZE/2;
	
	public ActionMove(){
		super(null);
		type = ActionType.MOVE;
		endTime =  TimeManager.getLongTime()+3000;
	}

	public ActionMove(Vector2 vector2) {
		this();
		coords = vector2;
	}
	
	public void checkCondition(AI ai, LiveObject owner){
		super.checkCondition(ai, owner);
		if (coords.dst(owner.getPos().current) < C.TILESIZE/2)
			done = true;
	}
	
	public Action setDistance(float dst){
		distance = dst;
		return this;
	}

}
