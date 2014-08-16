package com.zombie.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.zombie.C;
import com.zombie.logic.enums.ItemType;
import com.zombie.logic.enums.ObjectType;
import com.zombie.logic.item.Item;
import com.zombie.logic.object.GameObject;
import com.zombie.logic.object.ItemObject;
import com.zombie.logic.object.interfaces.Searchable;
import com.zombie.logic.object.interfaces.Useable;
import com.zombie.state.GameState;
import com.zombie.util.Cam;

public class ActionPicker extends Table {
	public static Table t; 
	public static GameObject target;
	
	public static Table get(GameObject object){
		target = object;
		
		t = new Table(C.UI.SKIN);
		t.add(target.name + " Actions:");
		t.row();
		t.addListener(new FocusListener(){
			public void keyboardFocusChanged (FocusEvent event, Actor actor, boolean focused) {
				t.remove();
			}
			public void scrollFocusChanged (FocusEvent event, Actor actor, boolean focused) {
				t.remove();
			}
		});
		int count = 0;
		if (object.type == ObjectType.LIVE){
			t.add(healButton());
			count++;
		}
		if (object instanceof Searchable){
			t.add(searchButton());
			count++;
		}
		if (object instanceof Useable){
			t.add(useButton());
			count++;
		}
		if (object instanceof ItemObject){
			t.add(pickupButton());
			count++;
		}		
		if (count == 0){
			t.add("empty");
		}
		position(object);
		return t;
	}

	private static Button useButton() {
		Button b = new TextButton("Use",C.UI.SKIN);
		b.addListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (target != null && (GameState.player.distanceToObject(target) < C.TILESIZE*2))
					((Useable) target).use();
				
//				System.out.println(GameState.player.distanceToObject(target) );
				t.remove();
			}});
		
		return b;
	}

	private static Button pickupButton() {
		Button b = new TextButton("Pickup",C.UI.SKIN);
		b.addListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (target != null && (GameState.player.distanceToObject(target) < C.TILESIZE*2)){
					GameState.player.pickup((ItemObject) target);
				
				System.out.println(GameState.player.distanceToObject(target) );
				}
				t.remove();
			}});
		
		return b;
	}

	static Button healButton(){
		Button b = new TextButton("Heal",C.UI.SKIN);
		b.addListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (target == GameState.player)
					for (Item i: GameState.player.items)
						if (i.type == ItemType.HEAL){
							GameState.player.use(i.id);
							break;
						}
				t.remove();
			}});
		return b;
	}
	
	static Button searchButton(){
		Button b = new TextButton("Search",C.UI.SKIN);
		b.addListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (target != null && (GameState.player.distanceToObject(target) < C.TILESIZE*2))
					GameState.player.startSearching(target);
				
				t.remove();
			}});
		
		return b;
	}

	public static void position(GameObject target) {
		if (t == null)
			return;
		
		float x = target.getX()-Cam.offsetX;//*;(1/GameState.zoom);
		float y = target.getY()-Cam.offsetY;//*(1/GameState.zoom);
		System.out.println(x + "  " + y + "  " + Cam.zoom);
		float zoom = Cam.zoom;
		if (zoom < 1)
			zoom = 1- (1-zoom);
		else
			zoom = 1+(1-zoom);
		x+=Gdx.graphics.getWidth()/2*zoom;
		y+=Gdx.graphics.getHeight()/2*zoom;
		t.setPosition(x,y);
		
	}
}
