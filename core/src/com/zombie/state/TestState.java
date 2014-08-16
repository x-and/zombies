package com.zombie.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.zombie.input.Input;
import com.zombie.ui.TestUI;
import com.zombie.util.state.BasicGameState;
import com.zombie.util.state.State;

public class TestState extends BasicGameState {

	TestUI ui = new TestUI();
	@Override
	public int getID() {
		return 10;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render() {
		draw();
		Table.drawDebug(this);
	}

	@Override
	public void update(float delta) {

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

	}

	@Override
	public void resize(int width, int height) {
		ui.resize(width, height);
		setViewport(width, height);
	}

}
