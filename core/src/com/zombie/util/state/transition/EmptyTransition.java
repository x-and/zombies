package com.zombie.util.state.transition;

import com.zombie.util.state.State;

public class EmptyTransition implements Transition {

	public boolean isComplete() {
		return true;
	}

	@Override
	public void update(float delta) {
	}

	@Override
	public void preRender() {
	}

	@Override
	public void postRender() {
	}

	@Override
	public void init(State firstState, State secondState) {
	}

	@Override
	public void complete() {
	}
}
