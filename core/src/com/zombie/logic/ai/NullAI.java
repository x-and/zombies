package com.zombie.logic.ai;

import com.zombie.logic.ai.event.Event;
import com.zombie.logic.object.GameObject;
import com.zombie.logic.object.LiveObject;

public class NullAI extends AI {

	public NullAI(LiveObject owner) {
		super(owner);
	}

	@Override
	public void onHit(GameObject damager) {
	}

	@Override
	public void doDie(GameObject killer) {
	}

	@Override
	public void actionDone() {
	}

	@Override
	public void spawned() {
		remove();
	}

	@Override
	public void onKill(GameObject killed) {
	}

	@Override
	public void handleEvent(Event e) {
	}

}
