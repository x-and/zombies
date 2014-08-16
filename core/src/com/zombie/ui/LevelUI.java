package com.zombie.ui;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.manager.ResourceManager;
import com.zombie.C;
import com.zombie.ZombieGame;
import com.zombie.logic.GameWorld;
import com.zombie.logic.level.Level;
import com.zombie.util.XMLUtils;
import com.zombie.util.state.transition.FadeInTransition;
import com.zombie.util.state.transition.FadeOutTransition;

public class LevelUI extends UI {


	ScrollPane pane;
	Table table;
	Button back, next, prev;
	int current;
	List<LevelWindow> list = new ArrayList<LevelWindow>();
	
	public void init(){
		table = new Table(C.UI.SKIN);
		table.debug();
		table.setHeight(10000);
		table.defaults().center().width(200).height(200).pad(16);
		loadLevels();
		pane = new ScrollPane(table,C.UI.SKIN);
		pane.setFadeScrollBars(false);
		pane.setScrollbarsOnTop(true);
		pane.setScrollingDisabled(true, false);
		
		back = new TextButton("Back", C.UI.SKIN);
		prev = new TextButton("<-", C.UI.SKIN);

		next = new TextButton("->", C.UI.SKIN);
		
		back.addListener(new ChangeListener(){

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				ZombieGame.getInstance().enterState(C.STATE_MENU);
			}});
		
	}

	private void loadLevels() {
		int c = 0;
		Node first = XMLUtils.getNodeForStream(Gdx.files.internal("data/data/level.xml").read());
		for(int i = 0; i < first.getChildNodes().getLength();i++ ){
			Node n = first.getChildNodes().item(i);	
			if (n.getNodeName().equalsIgnoreCase("level")){
				Level l = Level.loadLevel(n, true);
				
				LevelWindow lw = new LevelWindow(l);
				list.add(lw);
				table.add(lw);
				if (c == 2){
					table.row();
					c = 0;
				}
				c++;
			}
		}
		
		if (c == 0)
			return;
		while (c != 2){
			c++;
			table.add();
		}
	}

	@Override
	public void resize(int width, int height) {
		pane.setBounds(0, 64, width, height-64);
		current = (int) (pane.getHeight()+128);
		int btnWidth = (width-32*4)/3;
		back.setBounds(32, 16, btnWidth, 32);
//		prev.setBounds(32*2 + btnWidth, 16, btnWidth, 32);
//		next.setBounds(32*3 + btnWidth*2, 16, btnWidth, 32);
	}
	
	@Override
	public void add(Stage stage) {
		stage.addActor(pane);
		stage.addActor(back);
//		stage.addActor(prev);
//		stage.addActor(next);
		init = true;
	}

	@Override
	public void remove(Stage stage) {
		pane.remove();
		back.remove();
		prev.remove();
		next.remove();
		init = false;
	}
	
	public static class LevelWindow extends Button {
		
		Image icon;
		Image[] stars;
		int id;
		
		public LevelWindow(Level l){
			super(C.UI.SKIN);
			this.setSize(200, 200);
			id = l.id;
			top().center();
			defaults().pad(4);
			((Label) add("Level "+ (l.id+1)).colspan(3).center().top().getWidget()).setColor(Color.YELLOW);
			row();
			add(l.name).center().colspan(3).expandY();
			icon = new Image(ResourceManager.getImage(l.icon));
			row();
			add(icon).fill();
			
			if (id != 0){
				if (C.PROFILE != null && !C.PROFILE.levelsPassed.contains(l.id-1)){
					setColor(Color.GRAY);
					setDisabled(true);
				}
			}
				
			addListener(new ChangeListener(){

				@Override
				public void changed(ChangeEvent event, Actor actor) {
					ZombieGame.getInstance().enterState(C.STATE_GAME, new FadeOutTransition(Color.RED), new FadeInTransition(Color.RED));
					Gdx.app.postRunnable(new Runnable(){

						@Override
						public void run() {
							GameWorld.init(id);
						}});
					
				}});
		}
		
	}

	
}
