package com.zombie.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.manager.ResourceManager;
import com.zombie.C;
import com.zombie.C.UI;
import com.zombie.ZombieGame;
import com.zombie.input.Input;
import com.zombie.util.Utils;
import com.zombie.util.state.BasicGameState;
import com.zombie.util.state.State;
import com.zombie.util.state.transition.FadeInTransition;
import com.zombie.util.state.transition.FadeOutTransition;

public class StartingState extends BasicGameState {

	private static StartingState instance = new StartingState();
	
	public static StartingState getInstance(){
		return instance;
	}
	
	@Override
	public int getID() {
		return C.STATE_LOADING;
	}
	
	@Override
	public void init() {
	}

	@Override
	public void render() {
		getSpriteBatch().begin();
		Utils.build().append("Loading : ").append(ResourceManager.loadedResources).append(" / ").append(ResourceManager.totalResources);
		UI.FONT.draw(getSpriteBatch(), Utils.sb.toString() ,  Gdx.graphics.getWidth()/2-50, Gdx.graphics.getHeight()/2);
		getSpriteBatch().end();
	}

	@Override
	public void update(float delta) {
		
		if (ResourceManager.isLoaded())
			ZombieGame.getInstance().enterState(C.STATE_MENU, new FadeOutTransition(Color.RED), new FadeInTransition(Color.RED));
		else ResourceManager.loadResources(delta);
	}

	@Override
	public void enter(State from) {
	}

	@Override
	public void leave(State to) {
		getSpriteBatch().dispose();
		Gdx.input.setInputProcessor(ZombieGame.input = Input.init());
	}

	@Override
	public void resize(int width, int height) {
		setViewport(width,height, true);
		
	}

}
