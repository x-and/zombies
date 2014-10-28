package com.zombie.ui.window;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.color;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.forever;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.visible;

import java.util.concurrent.ScheduledFuture;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.esotericsoftware.minlog.Log;
import com.manager.ResourceManager;
import com.manager.ThreadPoolManager;
import com.zombie.C;
import com.zombie.achieve.Achievement;
import com.zombie.logic.object.live.Player;
import com.zombie.state.GameState;

public class Character extends Table {

	public Button shopButton,charButton, weaponButton;
	public Image weapon;
	TextureRegion weaponTexture;
	String currentWeaponImage;
	Image image;
	Label name, level, exp, skillp, money;
	Label hp,speed,def,evasion, str, agil,endur,dmg;
	
	Label shots,rate, killedDied,earned;
	Label rightTitle, generalTitle,combatTitle,peaceTitle;
	Button back,strUp, agilUp,endurUp, statsUp;
	Table achieveTable;
	boolean isOpened = false;
	int colspan,current = 0;
	Dialog statsUpDialog;
	Table charTable;
	
	public Character(String title, Skin skin) {
		super(skin);
		init();
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha){
		Log.info("Character", "draw");
		super.draw(batch, parentAlpha);
	}
	
	void setPadding(Table t){
		t.defaults().space(C.UI.SPACING/4,C.UI.SPACING/4,0,C.UI.SPACING/4);
		t.defaults().pad(C.UI.SPACING/4,C.UI.SPACING/4,0,C.UI.SPACING/4);
		t.defaults().top().left();
		t.align(Align.top);
//		t.debug();
	}
	
	private void init() {
		setBackground(new TextureRegionDrawable(ResourceManager.getImage("table_back")));
		setFillParent(true);
		setPadding(this);
		
		ButtonStyle style = new ButtonStyle();
		style.up = new TextureRegionDrawable(ResourceManager.getImage("arrow_up"));
		style.up.setMinWidth(32);
		style.up.setMinHeight(16);
		strUp = new Button(style);
		strUp.addListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				GameState.player.upStrength();
				update();
			}});
		agilUp = new Button(style);
		agilUp.setSize(32, 32);
		agilUp.addListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				GameState.player.upAgility();
				update();
			}});	
		
		endurUp = new Button(style);
		endurUp.addListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				GameState.player.upEndurance();
				update();
			}});
		
		back = new TextButton("Back", com.zombie.C.UI.SKIN);
		back.addListener(new ChangeListener(){

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				hide();
				GameState.getInstance().ui.unhide();
			}});

		statsUp = new TextButton("Increase Stats", com.zombie.C.UI.SKIN);
		statsUp.addListener(new ChangeListener(){

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				//open Dialog
				statsUpDialog.setVisible(true);
				statsUpDialog.show(GameState.getInstance());
			}});
		
		
		statsUpDialog = new Dialog("Increase your stats", C.UI.SKIN);
		statsUpDialog.setSize(400, 300);
		statsUpDialog.setPosition(Gdx.graphics.getWidth()/2-200, Gdx.graphics.getHeight()/2-150);
		statsUpDialog.getContentTable().add("SkillPoints: ").colspan(2).expandX();
		statsUpDialog.getContentTable().add(skillp);
		statsUpDialog.getContentTable().row();
		statsUpDialog.getContentTable().add(strUp).height(48);
		statsUpDialog.getContentTable().add("Strength : ").left();
		statsUpDialog.getContentTable().add(str).expandX().right();
		statsUpDialog.getContentTable().row();
		statsUpDialog.getContentTable().add(agilUp).height(48);
		statsUpDialog.getContentTable().add("Agility : ").left();
		statsUpDialog.getContentTable().add(agil).expandX().right();
		statsUpDialog.getContentTable().row();
		statsUpDialog.getContentTable().add(endurUp).height(48);
		statsUpDialog.getContentTable().add("Endurance : ").left();
		statsUpDialog.getContentTable().add(endur).expandX().right();
		statsUpDialog.button("Cancel");
		statsUpDialog.setMovable(false);
		statsUpDialog.setVisible(false);
		
		Table nameTable = new Table(C.UI.SKIN);
		setPadding(nameTable);		
		nameTable.add("Name:").left().expandX();
		name = (Label) nameTable.add(C.PROFILE.name).right().getWidget();
		nameTable.row();
		nameTable.add("Health:").left().expandX();
		hp = (Label) nameTable.add("100/100").right().getWidget();
		nameTable.row();
		nameTable.add("Experience:").left().expandX();
		exp = (Label) nameTable.add("100/100").right().getWidget();
		nameTable.row();
		nameTable.add("Level:").left().expandX();
		level = (Label) nameTable.add("1").right().getWidget();
		nameTable.row();
		nameTable.add("SkillPoints:").left().expandX();
		skillp = (Label) nameTable.add("10").right().getWidget();
		nameTable.row();
		nameTable.add("Money:").left().expandX();
		money = (Label) nameTable.add("100 $").right().getWidget();
							
		image = new Image();
		image.setDrawable(new TextureRegionDrawable(ResourceManager.getImage("nophoto")));
		image.setSize(C.UI.PHOTO_W, C.UI.PHOTO_H);
		
		charTable = new Table(C.UI.SKIN);
		setPadding(charTable);	
		charTable.add(image).colspan(2);
		charTable.add(nameTable).expandX().fill().colspan(2);
		charTable.row();
		charTable.add("Strength:").left();
		str = (Label) charTable.add("10").right().getWidget();
		charTable.add("Melee damage:").left();
		dmg = (Label) charTable.add("20").right().getWidget();		
		charTable.row();
		charTable.add("Agility:").left();
		agil = (Label) charTable.add("10").right().getWidget();
		charTable.add("Speed:").left();
		speed = (Label) charTable.add("20").right().getWidget();		
		charTable.row();
		charTable.add("Endurance:").left();
		endur = (Label) charTable.add("10").right().getWidget();
		charTable.add("Defence:").left();
		def = (Label) charTable.add("20").right().getWidget();		
		charTable.row();
		charTable.add().expandY().colspan(4);
		charTable.row();
		charTable.add("Statistics:").center().colspan(4);
		charTable.row();		
		charTable.add("Shots/Hits:").left().colspan(2);
		shots = (Label) charTable.add("20/1").right().colspan(2).getWidget();		
		charTable.row();
		charTable.add("Hit rate:").left().colspan(2);
		rate = (Label) charTable.add("100 %").right().colspan(2).getWidget();		
		charTable.row();
		charTable.add("Killed/Died:").left().colspan(2);
		killedDied = (Label) charTable.add("1/1").right().colspan(2).getWidget();		
		charTable.row();
		charTable.add("Earned money:").left().colspan(2);
		earned = (Label) charTable.add("12886612 $").right().colspan(2).getWidget();		
		charTable.row();		
		charTable.add().expandY().colspan(4);

		achieveTable = new Table(C.UI.SKIN);
		ScrollPane pane = new ScrollPane(achieveTable, C.UI.SKIN);

		pane.setVelocityY(0);
		pane.setSmoothScrolling(true);
		pane.setFadeScrollBars(false);
		pane.setScrollingDisabled(true, false);
		pane.setScrollbarsOnTop(true);
		setPadding(achieveTable);
		colspan = (int) (Gdx.graphics.getWidth()/2-C.UI.SPACING);
		colspan /= 64;
		colspan-=1;
		current = 0;
		achieveTable.add("Achievements:").colspan(colspan).expandX().center().row();

		add(charTable).width(Gdx.graphics.getWidth()/2-C.UI.SPACING).expandY().colspan(2).fill();
		add(pane).width(Gdx.graphics.getWidth()/2-C.UI.SPACING).expandY().colspan(2).fill();
		row();
		defaults().space(C.UI.SPACING/4,C.UI.SPACING/4,C.UI.SPACING/4,C.UI.SPACING/4);
		defaults().pad(C.UI.SPACING/4,C.UI.SPACING/4,C.UI.SPACING/4,C.UI.SPACING/4);	
		add(back).height(C.UI.BTN_BACK_HEIGHT).width(C.UI.BTN_BACK_WIDTH).left().fill().bottom();
		add(statsUp).height(C.UI.BTN_BACK_HEIGHT).width(C.UI.BTN_BACK_WIDTH).right().fill().bottom();
		add().height(C.UI.BTN_BACK_HEIGHT).width(C.UI.BTN_BACK_WIDTH).left().fill().bottom();
		add().height(C.UI.BTN_BACK_HEIGHT).width(C.UI.BTN_BACK_WIDTH).left().fill().bottom();
		statsUp.setVisible(false);
		setPosition(0,-Gdx.graphics.getHeight());
	}

	@Override
	public void setVisible(boolean visible){
		super.setVisible(visible);
		if (!visible)
			setPosition(0,0);
		else
			setPosition(0,-Gdx.graphics.getHeight());	
	}

	public void update(){
		Player p = GameState.player;
		if (p == null)
			return;
		name.setText(p.name);
		level.setText(String.valueOf(p.getLevel()));
		exp.setText(String.valueOf(p.getExp()));
		if (p.getStat().skillPoints != 0){
			skillp.setColor(Color.GREEN);
			statsUp.clearActions();
			statsUp.setVisible(true);
			statsUp.addAction(forever(sequence(color(Color.GREEN,1f),color(Color.WHITE,1f))));
		} else {
			skillp.setColor(Color.WHITE);
			statsUp.clearActions();
			statsUp.setVisible(false);
			if (statsUpDialog.isVisible()){
				statsUpDialog.hide();
				statsUpDialog.setVisible(false);
			}
		}
		
		skillp.setText(String.valueOf(p.getStat().skillPoints));

	
		money.setText(String.valueOf(p.getStat().money));
		
		hp.setText(p.getHp() + " / " + p.getMaxHp());
		speed.setText(String.valueOf(p.getVelocity()));
		def.setText(String.valueOf(p.getStat().defence));
//		evasion.setText(String.valueOf(p.getStat().evasion));
		str.setText(String.valueOf(p.getStat().strength));
		agil.setText(String.valueOf(p.getStat().agility));
		endur.setText(String.valueOf(p.getStat().endurance));
		
		shots.setText(p.getStat().shots + " / " + p.getStat().hits);
		int rte = p.getStat().shots==0 ? 0 : Math.round((p.getStat().hits/(p.getStat().shots/100f)));
		rate.setText(String.valueOf(rte));
		killedDied.setText(String.valueOf(p.getStat().kills)+ "/" + String.valueOf(p.getStat().dies));	
		earned.setText(String.valueOf(p.getStat().earned));
		
		strUp.setVisible(p.getStat().skillPoints != 0);
		agilUp.setVisible(p.getStat().skillPoints != 0);
		endurUp.setVisible(p.getStat().skillPoints != 0);
	}

	class AchieveImage extends Image {
		
		Achievement achieve;
		ScheduledFuture<?> future;
		
		AchieveDesc desc;
		
		public AchieveImage(Achievement c){
			achieve = c;
			desc = new AchieveDesc();
			desc.setSize(256, 64);
			desc.set(achieve);
			desc.setVisible(false);
			GameState.getInstance().addActor(desc);

			addListener(new InputListener(){
				
				public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
					if (!isOpened)
						return;
//						//TODO show tooltip, hide after 10 sec, or if exit;
					if (desc.isVisible())
						return;
					initDesc(event.getListenerActor().localToStageCoordinates(new Vector2(0,0)));
				}
				
				public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
					if (!isOpened)
						return;
					if (desc.isVisible())
						return;
					initDesc(event.getListenerActor().localToStageCoordinates(new Vector2(x,y)));
				}

				public void exit (InputEvent event, float x, float y, int pointer, Actor toActor) {
					if (!isOpened){
						desc.addAction(sequence(fadeOut(1),visible(false)));
						return;
					}

					if (AchieveImage.this.hit(x, y, true) == AchieveImage.this){
						return;
					}
					if (toActor == AchieveImage.this)
						return;
//					if (future != null && future.isDone())
//						return;
					if (future != null && !future.isDone())
						future.cancel(false);
					if (!desc.isVisible())
						return;
					desc.addAction(sequence(fadeOut(1),visible(false)));
				}
			});
		}

		protected void initDesc(Vector2 pos) {
			desc.setVisible(false);
			desc.getColor().a = 0;
			desc.setPosition(pos.x-32, pos.y-32);
			if (desc.getX()+desc.getWidth() > Gdx.graphics.getWidth())
				desc.setPosition(Gdx.graphics.getWidth()-desc.getWidth(),  pos.y-32);
			desc.setZIndex(100000);
			desc.addAction(sequence(visible(true),fadeIn(1)));
			if (future != null)
				future.cancel(true);
			future = ThreadPoolManager.schedule(new Runnable(){
				@Override
				public void run() {
					desc.addAction(sequence(fadeOut(1),visible(false)));
					desc.remove();
				}}, 10000);
		}
	}
	
	static class AchieveDesc extends Actor{
		
		Achievement achieve;
		Label name,desc;
		static TextureRegion texture = ResourceManager.getImage("bar_back");
		
		public AchieveDesc(){
			name = new Label("", C.UI.SKIN);
			desc = new Label("", C.UI.SKIN);
			desc.setWrap(true);
		}
		
		void set(Achievement a){
			achieve = a;
			name.setText(achieve.name);
			name.pack();
			desc.setText(achieve.desc);
			desc.pack();
		}
		
		@Override
		public void setSize(float width,float height){
			super.setSize(width, height);
			name.setWidth(width);
			desc.setWidth(width);
		}

		@Override
		public void setPosition(float x,float y){
			super.setPosition(x, y);
			name.setPosition(x, y+getHeight()-name.getHeight());
			desc.setPosition(x, y+getHeight()-name.getHeight()-desc.getHeight());
		}
		public void draw (SpriteBatch batch, float parentAlpha) {
			Color old = batch.getColor();
			batch.setColor(getColor());
			batch.draw(texture, getX(), getY(), getWidth(), getHeight());
			name.getColor().a = getColor().a;
			desc.getColor().a = getColor().a;
			name.draw(batch, parentAlpha);
			desc.draw(batch, parentAlpha);
			batch.setColor(old);
		}
	}
	
	public void hide(){
		isOpened = false;
		addAction(sequence(moveTo(0, -Gdx.graphics.getHeight(), 1),visible(false)));
	}

	public void unhide() {
		addAction(sequence(visible(true),moveTo(0, 0, 1),run(new Runnable(){

			@Override
			public void run() {
				isOpened = true;
			}})));
		update();
	}
	
	public void addAchieve(Achievement achive){
		Image img = new Image();
		img.setSize(64, 64);
		img.setDrawable(new TextureRegionDrawable(ResourceManager.getImage(achive.image)));
		achieveTable.add(img).top().left().width(64).height(64);
		Table t = new Table(C.UI.SKIN);
		t.top();
//		t.debug();
		Label name = (Label) t.add(achive.name).expandX().left().getWidget();
		achive.setNameColor(name);
		t.row();
		Label desc = (Label) t.add(achive.desc).fill().expand().getWidget();
		desc.setWrap(true);
//		t.add(achive.getCondition()).fill();
		achieveTable.add(t).center().fill().expandX().row();
	}

	public void loadAchieves() {
		if (C.PROFILE.save.achieved == null)
			return;
		for(Achievement a : C.PROFILE.save.achieved){
			if (a != null)
				addAchieve(a);
		}
		
	}
	
}
