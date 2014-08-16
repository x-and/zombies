package com.me.mygdxgame;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.zombie.ZombieGame;

public class Main {
	public static LwjglApplication app;
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Zombies";
		cfg.useGL20 = true;
		cfg.width = 800;
		cfg.height = 600;
		cfg.vSyncEnabled = true;
		cfg.audioDeviceBufferSize= 1024;
		cfg.audioDeviceSimultaneousSources= 100;
		app = new LwjglApplication(ZombieGame.getInstance(), cfg);
	}
}
