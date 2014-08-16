package com.zombie.util.state;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class BasicGameState extends State {
	
	private boolean pauseUpdate = false;
	private boolean pauseRender = false;
	
	public BasicGameState(int width, int height, boolean b,
			SpriteBatch spriteBatch) {
		super(width,height,b,spriteBatch);
	}
	public BasicGameState(){
		
	}

	public void pauseUpdate() {
		pauseUpdate = true;
	}
	public void pauseRender() {
		pauseRender = true;
	}
	public void unpauseUpdate() {
		pauseUpdate = false;
	}
	public void unpauseRender() {
		pauseRender = false;
	}
	public boolean isUpdatePaused() {
		return pauseUpdate;
	}
	public boolean isRenderPaused() {
		return pauseRender;
	}
	public void setUpdatePaused(boolean pause) {
		pauseUpdate = pause;
	}
	public void setRenderPaused(boolean pause) {
		pauseRender = pause;
	}
	
	public void pause(){
		pauseRender();
		pauseUpdate();
	}
	
	public void resume(){
		unpauseRender();
		unpauseUpdate();
	}
	
}
