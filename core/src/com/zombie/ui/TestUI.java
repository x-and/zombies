package com.zombie.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.manager.ResourceManager;
import com.zombie.C;
import com.zombie.util.Utils;

public class TestUI extends UI implements EventListener{

	Table mainTable,upperTable;
	HpBar hp;
	ExpBar exp;
	Table vehicle;
	Image menu_btn;
	
	public void init(){
		hp = new HpBar();
		hp.setHp(90, 100);
		exp = new ExpBar();
		exp.setHp(550, 1000);
		upperTable = new Table(C.UI.SKIN);
		upperTable.debug();
		upperTable.setSize(32*3+4*5, 40);
		upperTable.setPosition(Gdx.graphics.getWidth()-upperTable.getWidth(), Gdx.graphics.getHeight()-upperTable.getHeight());
		upperTable.add().pad(4).width(32).height(32);
		upperTable.add("Char").pad(4).width(32).height(32);
		upperTable.add(menu_btn = new Image(ResourceManager.getImage("icon_menu"))).pad(4).width(32).height(32).fill();
		mainTable = new Table(C.UI.SKIN);
		mainTable.debug();
		mainTable.setSize(Gdx.graphics.getWidth()/3, Gdx.graphics.getHeight()/8);
		mainTable.setPosition(Gdx.graphics.getWidth()/2-mainTable.getWidth()/2, 30);
		mainTable.defaults().padBottom(4);
		mainTable.add(new TextButton("1", new TextButtonStyle(C.UI.SKIN.get(TextButtonStyle.class)))).expandX().height(32);
		mainTable.add(new TextButton("2", new TextButtonStyle(C.UI.SKIN.get(TextButtonStyle.class)))).expandX().height(32);
		mainTable.add(new TextButton("3", new TextButtonStyle(C.UI.SKIN.get(TextButtonStyle.class)))).expandX().height(32);
		mainTable.add(new TextButton("4", new TextButtonStyle(C.UI.SKIN.get(TextButtonStyle.class)))).expandX().height(32).row();
		mainTable.add(hp).colspan(4).minHeight(10).maxHeight(16).expand().fill().row();
		mainTable.add(exp).colspan(4).minHeight(6).maxHeight(10).expandX().fill().bottom().row();
		
		Dialog.fadeDuration = 1f;
	}

	@Override
	public void add(Stage stage) {
		stage.addActor(mainTable);
		stage.addActor(upperTable);
		init = true;
	}

	public static class HpBar extends Actor{
		
		TextureRegion hp, back;
		public float hpPercent = 1;
		String text = "HP: 100/100";
		int hp1 = 100,maxHp1 = 100;
		
		public HpBar(){
			hp = ResourceManager.getImage("hp_bar");
			back =  ResourceManager.getImage("bar_back");
		}
		
		public void setHp(int hp, int maxHp) {
			if (hp == hp1 && maxHp1 == maxHp)
				return;
			hp1 = hp;
			maxHp1 = maxHp;
			
			float percent = maxHp/100f;
			hpPercent = Math.max(0, hp/percent/100);
			
			Utils.sb.setLength(0);
			Utils.sb.append("HP: ").append(hp).append("/").append(maxHp);
			text = Utils.sb.toString();			
		}

		public void draw (SpriteBatch batch, float parentAlpha) {
			batch.setColor(Color.WHITE);
			batch.draw(back, getX(), getY(), getWidth(), getHeight());
			batch.setColor(Color.BLACK);
			batch.draw(back, getX()+1, getY()+1, getWidth()-2, getHeight()-2);
			batch.setColor(Color.WHITE);
			batch.draw(hp, getX()+1, getY()+1, getWidth()*hpPercent-2, getHeight()-2);
			
			C.UI.FONT.setColor(getColor());
			C.UI.FONT.drawMultiLine(batch, text, getX(), getY()+getHeight()/2+4, getWidth(), HAlignment.CENTER);
		}
	}	
	
	public static class ExpBar extends Actor{
		
		TextureRegion hp, back;
		public float hpPercent = 1;
		int hp1 = 100,maxHp1 = 100;
		
		public ExpBar(){
			hp = ResourceManager.getImage("exp_bar");
			back =  ResourceManager.getImage("bar_back");
		}
		
		public void setHp(int hp, int maxHp) {
			if (hp == hp1 && maxHp1 == maxHp)
				return;
			hp1 = hp;
			maxHp1 = maxHp;
			
			float percent = maxHp/100f;
			hpPercent = Math.max(0, hp/percent/100);
		
		}

		public void draw (SpriteBatch batch, float parentAlpha) {
			batch.setColor(Color.WHITE);
			batch.draw(back, getX(), getY(), getWidth(), getHeight());
			batch.setColor(Color.BLACK);
			batch.draw(back, getX()+1, getY()+1, getWidth()-2, getHeight()-2);
			batch.setColor(Color.WHITE);
			batch.draw(hp, getX()+1, getY()+1, getWidth()*hpPercent-2, getHeight()-2);
		}
	}	
	
	@Override
	public boolean handle(Event event) {
		return false;
	}

	@Override
	public void remove(Stage stage) {

		init = false;
	}

	@Override
	public void resize(int width, int height) {
		if (!init)
			return;
		mainTable.setSize(width/3, height/8);
		mainTable.setPosition(width/2-mainTable.getWidth()/2, 30);
		mainTable.pack();
		upperTable.setSize(32*3+4*5, 40);
		upperTable.setPosition(width-upperTable.getWidth(), height-upperTable.getHeight());
		upperTable.pack();
	}
	
}
