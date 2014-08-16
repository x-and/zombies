package com.zombie.logic.ai;

import com.zombie.logic.ai.action.ActionFollow;
import com.zombie.logic.ai.action.ActionType;
import com.zombie.logic.ai.event.Event;
import com.zombie.logic.object.LiveObject;

public class FriendAI extends FleeAI {

	public int DST_TO_FRIEND = 50;

	public FriendAI(LiveObject owner) {
		super(owner);
		WAIT_TIMER_MIN = 500;
		WAIT_TIMER_MAX = 2000;
	}
	
	@Override
	public void actionDone() {
		if (action.type != ActionType.FOLLOW){
			if (friend.dst(owner) > DST_TO_FRIEND){
				if (losCheck(friend))
					setAction(new ActionFollow(friend));
				else
					findPath(friend);
			}
		} else super.actionDone();
	}

	@Override
	public void spawned() {
		if (friend == null)
			return;
		if (losCheck(friend))
			setAction(new ActionFollow(friend));
		else
			findPath(friend);
	}
	
	@Override
	public void handleEvent(Event e) {

	}
}
