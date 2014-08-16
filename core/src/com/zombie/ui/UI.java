package com.zombie.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;

public abstract class UI {
	
	public boolean init = false;
	
	public abstract void init();
	public abstract void add(Stage stage);
	public abstract void remove(Stage stage);
	public abstract void resize(int width,int height);
}
