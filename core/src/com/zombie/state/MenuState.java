package com.zombie.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.manager.ResourceManager;
import com.zombie.C;
import com.zombie.C.UI;
import com.zombie.Profile;
import com.zombie.input.Input;
import com.zombie.ui.MainMenuUI;
import com.zombie.util.Utils;
import com.zombie.util.state.BasicGameState;
import com.zombie.util.state.State;

public class MenuState extends BasicGameState {

	private static MenuState instance = new MenuState();

	Actor menuActor;
	MainMenuUI ui = new MainMenuUI();
	public static MenuState getInstance(){
		return instance;
	}
	
	@Override
	public int getID() {
		return C.STATE_MENU;
	}

	@Override
	public void init() {
	}

	@Override
	public void render() {
		draw();
		Table.drawDebug(this);

	}

	@Override
	public void update(float delta) {
		act();
	}
	
	@Override
	public void enter(State from) {
		ui.init();

		Input.addInputProcessor(this);
		addActor(menuActor = new Actor(){
			@Override
			public void draw (SpriteBatch batch, float parentAlpha) {
				batch.setColor(Color.WHITE);
				batch.draw(ResourceManager.getImage("menu"), 0, 0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
				Utils.sb.setLength(0);
				Utils.sb.append("FPS: ").append(Gdx.graphics.getFramesPerSecond());
				UI.FONT.draw(batch, Utils.sb.toString(),	0, Gdx.graphics.getHeight());
			}
		});
		ui.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		ui.add(this);
		if (Profile.noProfiles){
			Dialog d = new Dialog("New profile: enter your name", C.UI.SKIN){
				protected void result (Object object) {
					if (object instanceof TextField){
						Profile.create(((TextField) object).getText());
					}
				}
			};
			TextField field = new TextField("", C.UI.SKIN);
			d.getContentTable().add(field).left();
			d.button("Ok", field);
			d.show(MenuState.getInstance());
		}
	}

	@Override
	public void leave(State to) {
		Input.removeInputProcessor(this);
		menuActor.remove();
		ui.remove(this);
	}

	@Override
	public void resize(int width, int height) {
		setViewport(width,height, true);
		if (ui.init)
			ui.resize(width, height);
	}

}
