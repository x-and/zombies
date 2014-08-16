package com.zombie.logic.level.wave;

import com.manager.TimeManager;
import com.zombie.logic.GameWorld;
import com.zombie.state.GameState;

public class WaveStarter implements Runnable {

	int i;
	public WaveStarter(int id){
		i = id;
	}
	
	@Override
	public void run() {
		GameState.getInstance().currentWave = GameWorld.level.waves.get(i);
		GameState.getInstance().waveStarted = TimeManager.getLongTime();
		GameState.getInstance().waitNewWave = false;
	}

}
