package com.zombie.util.state;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;

public abstract class State extends Stage{

	public State(int width, int height, boolean b, SpriteBatch spriteBatch) {
		super(width,height,b,spriteBatch);
	}
	
	public State(){

	}
	
	public abstract int getID();
	public abstract void init();
	public abstract void render();
	
	/*act(float delta) is running after this method, not need to run it from update twice */
	public abstract void update(float delta);
	public abstract void enter(State from);
	public abstract void leave(State to);
	public abstract void pause();
	public abstract boolean isRenderPaused();
	public abstract boolean isUpdatePaused();
	public abstract void resume();
	public abstract void resize(int width,int height);

	public void postUpdate(float delta) {
	}

	
}
