package com.zombie.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.zombie.C;
import com.zombie.input.Input;
import com.zombie.ui.LevelUI;
import com.zombie.util.state.BasicGameState;
import com.zombie.util.state.State;

public class LevelState extends BasicGameState {

	private static LevelState instance = new LevelState();

	LevelUI ui = new LevelUI();
	public static LevelState getInstance(){
		return instance;
	}
	
	@Override
	public int getID() {
		return C.STATE_LEVEL_MENU;
	}

	@Override
	public void init() {
	}

	@Override
	public void render() {;
		draw();
//		Table.drawDebug(this);
	}

	@Override
	public void update(float delta) {
		act();
	}

	@Override
	public void enter(State from) {
		ui.init();
		Input.addInputProcessor(this);
		ui.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		ui.add(this);
	}

	@Override
	public void leave(State to) {
		Input.removeInputProcessor(this);
		ui.remove(this);
	}

	@Override
	public void resize(int width, int height) {
		setViewport(width,height, true);
		if (ui.init)
			ui.resize(width,height);
	}
	
	@Override	
	public boolean keyUp(int keyCode) {
		if (keyCode == Keys.F1){
			ui.remove(this);
			ui.init();
			ui.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			ui.add(this);
			return true;
		}
		return false;
	}
		

}
