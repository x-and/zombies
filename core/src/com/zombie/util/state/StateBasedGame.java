package com.zombie.util.state;

import java.util.HashMap;
import java.util.Iterator;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.zombie.input.Input;
import com.zombie.util.state.transition.EmptyTransition;
import com.zombie.util.state.transition.Transition;

/**
 * A state based game isolated different stages of the game (menu, ingame, hiscores, etc) into 
 * different states so they can be easily managed and maintained.
 *
 * @author kevin
 */
public abstract class StateBasedGame implements ApplicationListener{

	protected HashMap<Integer, State> states = new HashMap<Integer, State>();
	private State currentState;
	private State nextState;
	
	private Transition enterTransition;
	private Transition leaveTransition;
	public static Input input;
	
	public int getStateCount() {
		return states.keySet().size();
	}

	public int getCurrentStateID() {
		return currentState.getID();
	}

	public State getCurrentState() {
		return currentState;
	}

	public void addState(State state) {
		states.put(new Integer(state.getID()), state);
		if (currentState == null || currentState.getID() == -1) {
			currentState = state;
		}
	}
	
	public State getState(int id) {
		return (State) states.get(new Integer(id));
	}

	public void enterState(int id) {
		enterState(id, new EmptyTransition(), new EmptyTransition());
	}
	
	public void enterState(int id, Transition leave, Transition enter) {
		if (leave == null) {
			leave = new EmptyTransition();
		}
		if (enter == null) {
			enter = new EmptyTransition();
		}
		leaveTransition = leave;
		enterTransition = enter;
		nextState = getState(id);
		if (nextState == null) {
			throw new RuntimeException("No game state registered with the ID: "+id);
		}
		leaveTransition.init(currentState, nextState);
	}
	

	public  void create(){
		init();
		initStatesList();
		
		Iterator<State> gameStates = states.values().iterator();
		
		while (gameStates.hasNext()) {
			State state = (State) gameStates.next();
			state.init();
		}
		if (currentState != null) {
			currentState.enter(null);
		}
	}

	public abstract void init();
	public abstract void initStatesList();
	
	public void render(){
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		preRenderState();

		if (leaveTransition != null) {
			leaveTransition.preRender();
		} else if (enterTransition != null) {
			enterTransition.preRender();
		}
		
		if(!currentState.isRenderPaused()) {
				currentState.render();
			}
		if (leaveTransition != null) {
			leaveTransition.postRender();
		} else if (enterTransition != null) {
			enterTransition.postRender();
		}
		
		postRenderState();
		update(Gdx.graphics.getRawDeltaTime());
	}
	
	protected void preRenderState() {
		// NO-OP
	}

	protected void postRenderState() {
		// NO-OP
	}
	
	public void update(float delta){
		preUpdateState(delta);
		
		if (leaveTransition != null) {
			leaveTransition.update(delta);
			if (leaveTransition.isComplete()) {
				currentState.leave(nextState);
				State prevState = currentState;
				currentState = nextState;
				nextState = null;
				leaveTransition.complete();
				leaveTransition = null;
				currentState.enter(prevState);
				if (enterTransition != null) {
					enterTransition.init(currentState, prevState);
				}
			} else {
				return;
			}
		}
		
		if (enterTransition != null) {
			enterTransition.update(delta);
			if (enterTransition.isComplete()) {
				enterTransition.complete();
				enterTransition = null;
			} else {
				return;
			}
		}
		if (input != null)
			input.update(delta);
		if(!currentState.isUpdatePaused()) {
			currentState.update(delta);
		}
		currentState.act(delta);
		postUpdateState(delta);
	}

	protected void preUpdateState(float delta){
		// NO-OP
	}

	protected void postUpdateState(float delta){
		if(!currentState.isUpdatePaused()) {
			currentState.postUpdate(delta);
		}
	}
	
}
