package com.zombie.ui;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.zombie.C;
import com.zombie.ZombieGame;
import com.zombie.state.MenuState;
import com.zombie.ui.window.Options;
import com.zombie.ui.window.Profiles;
import com.zombie.util.state.transition.FadeInTransition;
import com.zombie.util.state.transition.FadeOutTransition;

public class MainMenuUI extends UI implements EventListener{

	public Button start,options,profile,exit;
	public Actor current;
	public Options menu;
	
	public void init(){
		
		start = new TextButton("Start", C.UI.SKIN);
		start.setColor(Color.RED);
		start.addListener(this);
		
		options = new TextButton("Options", C.UI.SKIN);
		options.setColor(Color.RED);
		options.addListener(this);
		
		profile = new TextButton("Profile", C.UI.SKIN);
		profile.setColor(Color.RED);
		profile.addListener(this);
		
		exit = new TextButton("Exit", C.UI.SKIN);
		exit.setColor(Color.RED);
		exit.addListener(this);
		
		Dialog.fadeDuration = 1f;
	}

	@Override
	public void add(Stage stage) {
		stage.addActor(start);
		stage.addActor(options);
		stage.addActor(profile);
		stage.addActor(exit);
		stage.addActor(menu);

		init = true;
	}

	@Override
	public boolean handle(Event event) {
		if (event instanceof ChangeEvent){
			if (event.getListenerActor() == exit)
				Gdx.app.exit();
			if (event.getListenerActor() == start){
				if (C.PROFILE == null){
					Profiles.profiles.show(MenuState.getInstance());
					return false;
				}
				ZombieGame.getInstance().enterState(C.STATE_LEVEL_MENU, new FadeOutTransition(Color.RED), new FadeInTransition(Color.RED));
			}
			if (event.getListenerActor() == options){
				if (!menu.isVisible()){
					menu.getColor().a = 0;
					menu.addAction(sequence(Actions.visible(true),fadeIn(1, Interpolation.fade)));
				} else {
					menu.addAction(sequence(fadeOut(1, Interpolation.fade), Actions.visible(false)));
				}
			}
			if (event.getListenerActor() == profile){
				Profiles.profiles.show(MenuState.getInstance());
			}
		}
		return false;
	}

	@Override
	public void remove(Stage stage) {
		start.remove();
		options.remove();
		profile.remove();
		exit.remove();
		menu.remove();

		init = false;
	}

	@Override
	public void resize(int width, int height) {
		float currentX = C.UI.SPACING;
		start.setBounds(currentX, C.UI.SPACING*8, C.UI.MENU_BTN_W, C.UI.MENU_BTN_H);
		options.setBounds((currentX+=C.UI.MENU_BTN_W+C.UI.SPACING), C.UI.SPACING*8, C.UI.MENU_BTN_W, C.UI.MENU_BTN_H);
		profile.setBounds((currentX+=C.UI.MENU_BTN_W+C.UI.SPACING), C.UI.SPACING*8, C.UI.MENU_BTN_W, C.UI.MENU_BTN_H);
		exit.setBounds((currentX+=C.UI.MENU_BTN_W+C.UI.SPACING), C.UI.SPACING*8, C.UI.MENU_BTN_W, C.UI.MENU_BTN_H);
		menu = new Options("Options", C.UI.SKIN);

	}
	
}
