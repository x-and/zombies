package com.zombie.logic.ai.action;

import com.badlogic.gdx.math.Vector2;
import com.path.NavPath;
import com.zombie.C;
import com.zombie.logic.ai.AI;
import com.zombie.logic.object.GameObject;
import com.zombie.logic.object.LiveObject;

public class ActionPath extends Action {
	
	NavPath path;
	public Vector2 coords = new Vector2();
	public int currentStep = 1;
	
	public ActionPath(NavPath p, GameObject t){
		super(t);
		path = p;
		coords.set(t.getPos().current);
		nextPoint(null);
		type = ActionType.PATH;
	}

	public void checkCondition(AI ai, LiveObject owner){
		float distance = coords.dst(owner.getPos().current);
		
//		System.out.println("checkCondition " + distance);
		if (distance < C.TILESIZE/2)
			if (path.getLength() == currentStep){
				ai.actionDone();
				done = true;
			} else
				nextPoint(ai);
//		if (path.getLength() == currentStep){
//			if (owner.position.dst(coords) < C.TILESIZE/2){
//				ai.actionDone();
//				done = true;
//			}
//		} else {
//			System.out.println("checkCondition " +coords.dst(owner.position));
//
//			if (owner.position.dst(coords) < C.TILESIZE/2){
//				nextPoint(ai);
//			}
//		}
	}
	
	void nextPoint(AI ai){
//		System.out.println("index " + currentStep + " / " + path.getLength());
		if (currentStep >= path.getLength())
			return;
		path.getStep(currentStep, coords);
		coords.scl(C.TILESIZE).add(C.TILESIZE/2, C.TILESIZE/2);
		currentStep++;
		
		if (ai != null)
			ai.nextPathNode();
	}
}
