package com.zombie.ui.window;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.visible;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.esotericsoftware.minlog.Log;
import com.zombie.C;
import com.zombie.ZombieGame;
import com.zombie.achieve.AchieveSystem;
import com.zombie.logic.GameWorld;
import com.zombie.state.GameState;
import com.zombie.util.state.transition.FadeInTransition;
import com.zombie.util.state.transition.FadeOutTransition;

public class Menu extends Actor {


	Button back, options, save, exit,restart;
	Dialog exitDialog;
	Options optionWindow; 
	
	public Menu() {
		init();
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha){
		Log.info("Menu", "draw");
		super.draw(batch, parentAlpha);
	}
	
	private void init() {
		
		float h = Gdx.graphics.getHeight()/2;
		h+= C.UI.BTN_BACK_HEIGHT*4/2;
		back = new TextButton("Back",C.UI.SKIN);
		back.setSize(C.UI.BTN_BACK_WIDTH, C.UI.BTN_BACK_HEIGHT);
		back.setY(h);
		
		restart = new TextButton("Restart",C.UI.SKIN);
		restart.setSize(C.UI.BTN_BACK_WIDTH, C.UI.BTN_BACK_HEIGHT);
		restart.setY(back.getY()-C.UI.BTN_BACK_HEIGHT- C.UI.SPACING);

		
		options = new TextButton("Options",C.UI.SKIN);
		options.setSize(C.UI.BTN_BACK_WIDTH, C.UI.BTN_BACK_HEIGHT);
		options.setY(restart.getY()-C.UI.BTN_BACK_HEIGHT- C.UI.SPACING);

		save = new TextButton("Save",C.UI.SKIN);
		save.setSize(C.UI.BTN_BACK_WIDTH, C.UI.BTN_BACK_HEIGHT);
		save.setY(options.getY()-C.UI.BTN_BACK_HEIGHT- C.UI.SPACING);

		exit = new TextButton("Exit",C.UI.SKIN);
		exit.setSize(C.UI.BTN_BACK_WIDTH, C.UI.BTN_BACK_HEIGHT);
		exit.setY(options.getY()-C.UI.BTN_BACK_HEIGHT- C.UI.SPACING);

		exitDialog = new Dialog("Exit?", C.UI.SKIN);
		exitDialog.setPosition(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
		exitDialog.setSize(400, 400);
		exitDialog.setModal(true);
		
		exitDialog.getContentTable().add("Are you sure to exit? Y/N").center().expand();
		exitDialog.getContentTable().row();
		Button mainMenu,back1,exit1;
		exitDialog.getButtonTable().add(mainMenu = new TextButton("To main", C.UI.SKIN)).left();
		exitDialog.getButtonTable().add(back1 = new TextButton("Back",C.UI.SKIN)).center().expandX();
		exitDialog.getButtonTable().add(exit1 = new TextButton("Exit",C.UI.SKIN)).right();

		optionWindow = new Options("Options", C.UI.SKIN);
		
		mainMenu.addListener(new ClickListener(){
			public void clicked (InputEvent event, float x, float y) {
				exitDialog.remove();
//				C.SAVE = null;
				AchieveSystem.init();
				ZombieGame.getInstance().enterState(C.STATE_MENU, new FadeOutTransition(Color.RED), new FadeInTransition(Color.RED));
			}
		});
		back1.addListener(new ClickListener(){
			public void clicked (InputEvent event, float x, float y) {
				exitDialog.remove();
			}
		});		
		exit1.addListener(new ClickListener(){
			public void clicked (InputEvent event, float x, float y) {
				Gdx.app.exit();
			}
		});		
		
		back.addListener(new ClickListener(){
			public void clicked (InputEvent event, float x, float y) {
				hide();
				GameState.getInstance().ui.unhide();
			}
		});
		
		save.addListener(new ClickListener(){
			public void clicked (InputEvent event, float x, float y) {
				C.PROFILE.save();
//				Save.save(GameState.player);
			}
		});		

		exit.addListener(new ClickListener(){
			public void clicked (InputEvent event, float x, float y) {
				exitDialog.show(GameState.getInstance());
			}
		});
		
		options.addListener(new ClickListener(){
			public void clicked (InputEvent event, float x, float y) {
				optionWindow.addAction(sequence(Actions.visible(true),fadeIn(1, Interpolation.fade)));

			}
		});
		
		
		restart.addListener(new ClickListener(){
			public void clicked (InputEvent event, float x, float y) {
				Gdx.app.postRunnable(new Runnable(){

					@Override
					public void run() {
						GameWorld.init(GameWorld.level.id);
						hide();
						GameState.getInstance().ui.unhide();
					}});
			}
		});
		setVisible(false);
	}

	public void hide(){
		back.addAction(sequence(moveTo(Gdx.graphics.getWidth(), back.getY(), 1f, Interpolation.swingIn)));
		restart.addAction(sequence(delay(0.2f),moveTo(Gdx.graphics.getWidth(), restart.getY(), 1f, Interpolation.swingIn)));
		options.addAction(sequence(delay(0.4f),moveTo(Gdx.graphics.getWidth(), options.getY(), 1f, Interpolation.swingIn)));
		exit.addAction(sequence(delay(0.6f),moveTo(Gdx.graphics.getWidth(), exit.getY(), 1f, Interpolation.swingIn)));
		addAction(visible(false));
	}

	public void unhide() {
		back.addAction(sequence(moveTo(Gdx.graphics.getWidth()/2-back.getWidth()/2, back.getY(), 1f, Interpolation.swingOut)));
		restart.addAction(sequence(delay(0.2f),moveTo(Gdx.graphics.getWidth()/2-restart.getWidth()/2, restart.getY(), 1f, Interpolation.swingOut)));
		options.addAction(sequence(delay(0.4f),moveTo(Gdx.graphics.getWidth()/2-options.getWidth()/2, options.getY(), 1f, Interpolation.swingOut)));
		exit.addAction(sequence(delay(0.6f),moveTo(Gdx.graphics.getWidth()/2-exit.getWidth()/2, exit.getY(), 1f, Interpolation.swingOut)));

		addAction(visible(true));
	}
	
	public void add(Stage stage){
		stage.addActor(back);
		stage.addActor(restart);
		stage.addActor(options);
//		stage.addActor(save);
		stage.addActor(exit);
		stage.addActor(optionWindow);
//		stage.addActor(exitDialog);
//		hide();
	}
	
	public boolean remove(){
		back.remove();
		restart.remove();
		options.remove();
//		save.remove();
		exit.remove();
		optionWindow.remove();
//		exitDialog.remove();
		return super.remove();
	}
	
	@Override
	public void setVisible(boolean visible){
		super.setVisible(visible);
//		GameState.getInstance().setUpdatePaused(visible);
		if (!visible){
			back.setX(Gdx.graphics.getWidth());
			restart.setX(Gdx.graphics.getWidth());
			options.setX(Gdx.graphics.getWidth());
			save.setX(Gdx.graphics.getWidth());
			exit.setX(Gdx.graphics.getWidth());
		} else {
//			setPosition(0,-Gdx.graphics.getHeight());
		}
	}

	public void resize(int width, int height) {
		if (isVisible()){
			back.setX(width/2-back.getWidth()/2);
			restart.setX(width/2-restart.getWidth()/2);
			options.setX(width/2-options.getWidth()/2);
			save.setX(width/2-save.getWidth()/2);
			exit.setX(width/2-exit.getWidth()/2);
		} else{
			back.setX(width);
			restart.setX(width);
			options.setX(width);
			save.setX(width);
			exit.setX(width);
		}
	}


}
