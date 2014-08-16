package com.zombie.logic.ai;

import com.manager.TimeManager;
import com.zombie.logic.ai.action.Action;
import com.zombie.logic.ai.action.ActionFlee;
import com.zombie.logic.ai.action.ActionType;
import com.zombie.logic.ai.event.Event;
import com.zombie.logic.object.GameObject;
import com.zombie.logic.object.LiveObject;
import com.zombie.util.Rnd;

/**Стоит на месте респа, иногда передвигаясь. При атаке старается убежать. */
public class FleeAI extends AI {

	public float percentHp = 1f;
	
	public FleeAI(LiveObject owner) {
		super(owner);
	}
	
	@Override
	public void onHit(GameObject damager) {
		if (owner.getHp()/owner.getMaxHp()/100f < percentHp)
			if (action.type != ActionType.FLEE){
				setAction(new ActionFlee(damager));
				state = AIState.FLEING;
			}
	}

	@Override
	public void doDie(GameObject killer) {
		remove();
	}

	@Override
	public void actionDone() {
		if (action.type == ActionType.WAIT){
			randomMove(Rnd.randomInt(MOVE_RND_DST_MIN, MOVE_RND_DST_MAX));
		} else if (action.type == ActionType.MOVE || action.type == ActionType.FLEE || action.type == ActionType.FOLLOW){
			setAction(Action.WAIT.copy().setTime((TimeManager.getLongTime() + Rnd.randomInt(WAIT_TIMER_MIN, WAIT_TIMER_MAX))));
		}
		if (last.type == ActionType.FLEE)
			state = AIState.NO_TARGET;
	}
	
	@Override
	public void spawned() {
		setAction(Action.WAIT.copy().setTime((TimeManager.getLongTime() + Rnd.randomInt(WAIT_TIMER_MIN, WAIT_TIMER_MAX))));
	}

	@Override
	public void onKill(GameObject killed) {
		
	}

	@Override
	public void handleEvent(Event e) {

	}

}
