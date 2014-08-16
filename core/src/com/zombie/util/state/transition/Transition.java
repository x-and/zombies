package com.zombie.util.state.transition;

import com.zombie.util.state.State;

public interface Transition {

	public void update(float delta);
	public void preRender();
	public void postRender();
	public boolean isComplete();
	public void complete();
	public void init(State firstState, State secondState);
}
