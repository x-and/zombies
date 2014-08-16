package com.zombie;

import com.esotericsoftware.minlog.Log;
import com.manager.ResourceManager;
import com.physics.Physics;
import com.zombie.C.APP;
import com.zombie.C.UI;
import com.zombie.achieve.AchieveSystem;
import com.zombie.logic.GameWorld;
import com.zombie.logic.Material;
import com.zombie.logic.item.Item;
import com.zombie.state.GameState;
import com.zombie.state.LevelState;
import com.zombie.state.MenuState;
import com.zombie.state.StartingState;
import com.zombie.state.TestState;
import com.zombie.util.state.State;
import com.zombie.util.state.StateBasedGame;

public class ZombieGame extends StateBasedGame {

	private static ZombieGame instance = new ZombieGame();
	
	public static ZombieGame getInstance(){
		return instance;
	}

	@Override
	public void initStatesList() {
		addState(StartingState.getInstance());
		addState(MenuState.getInstance());
		addState(GameState.getInstance());
		addState(LevelState.getInstance());
		addState(new TestState());
	}
	
	@Override
	public void resize(int width, int height) {
		for(State s : states.values())
			s.resize(width,height);
		Log.info("Game", "resize" + width + "_" + height);
	}

	@Override
	public void pause() {
		Log.info("Game", "onPause");
		getCurrentState().pause();
	}

	@Override
	public void resume() {
		Log.info("Game", "onResume");
		getCurrentState().resume();
	}

	@Override
	public void dispose() {
		Log.info("Game", "onDispose");
	}

	@Override
	public void init() {
		APP.init();
		UI.init();
		ResourceManager.getInstance().init(false);
		AchieveSystem.init();
		Profile.init();
		GameWorld.getInstance();
		Physics.getInstance();
		Item.reload();
		Material.reload();
	}
}
