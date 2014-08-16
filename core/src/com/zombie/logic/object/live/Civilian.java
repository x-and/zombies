package com.zombie.logic.object.live;

import com.zombie.logic.Faction;
import com.zombie.logic.ai.AI;
import com.zombie.logic.ai.FleeAI;
import com.zombie.logic.object.GameObject;
import com.zombie.logic.object.LiveObject;

public class Civilian extends LiveObject {

	public Civilian(float x, float y,String image) {
		super(x, y,image);
		faction = Faction.HUMAN;
//		createBody();
	}

	
	@Override
	public void update(float delta) {
		super.update(delta);
		if(isDead())
			return;
		performAction();
	}

		
		
	@Override
	protected void onHit(GameObject damager) {
		// TODO Auto-generated method stub

	}
	
	public AI getAI(){
		if (ai == null)
			ai = new FleeAI(this);
		return ai;
	}	

}
