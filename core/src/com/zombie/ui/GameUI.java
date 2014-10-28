package com.zombie.ui;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.color;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.repeat;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.manager.ResourceManager;
import com.zombie.C;
import com.zombie.achieve.Achievement;
import com.zombie.logic.item.Item;
import com.zombie.logic.item.Weapon;
import com.zombie.logic.item.WeaponType;
import com.zombie.logic.object.live.Player;
import com.zombie.state.GameState;
import com.zombie.ui.window.AchieveInfo;
import com.zombie.ui.window.Character;
import com.zombie.ui.window.Menu;
import com.zombie.ui.window.Shop;
import com.zombie.util.Utils;

public class GameUI extends UI{

	public Table mainTable,upperTable;
	public HpBar hpBar;
	public MpBar mpBar;
	public WeaponBar weaponBar;
	public boolean hide = false;
	public ExpBar expBar;
	public Label level,vehicleSpeed,vehicleHp;
	Weapon currentWeapon;
	Image charBtn, shopBtn;
	Image menuBtn;
	TextButton slot1,slot2,slot3;
	
	Shop shop;
	public Character character;
	Menu menu;
	public AchieveInfo aInfo;
	
	public void init(){
		if (init)
			return;
		slot1 = new TextButton("1", new TextButtonStyle(C.UI.SKIN.get(TextButtonStyle.class)));
		slot1.addListener(new ClickListener(){
			public void clicked (InputEvent event, float x, float y) {
				if (GameState.player.slots[0] == -1)
					chooseItemForSlot(0,event.getStageX(),event.getStageY());
				else
					useSlot(0);
		}});
		slot1.addListener(new ClickListener(Buttons.RIGHT){
			public void clicked (InputEvent event, float x, float y) {	chooseItemForSlot(0,event.getStageX(),event.getStageY()); }});
		

		slot2 = new TextButton("2",  new TextButtonStyle(C.UI.SKIN.get(TextButtonStyle.class)));
		slot2.addListener(new ClickListener(){
			public void clicked (InputEvent event, float x, float y) {
				if (GameState.player.slots[1] == -1)
					chooseItemForSlot(1,event.getStageX(),event.getStageY());
				else
					useSlot(1);
		}});
		slot2.addListener(new ClickListener(Buttons.RIGHT){
			public void clicked (InputEvent event, float x, float y) {	chooseItemForSlot(1,event.getStageX(),event.getStageY()); }});

		slot3 = new TextButton("3",  new TextButtonStyle(C.UI.SKIN.get(TextButtonStyle.class)));
		slot3.addListener(new ClickListener(){
			public void clicked (InputEvent event, float x, float y) {
				if (GameState.player.slots[2] == -1)
					chooseItemForSlot(2,event.getStageX(),event.getStageY());
				else
					useSlot(2);
		}});
		slot3.addListener(new ClickListener(Buttons.RIGHT){
			public void clicked (InputEvent event, float x, float y) {	chooseItemForSlot(2,event.getStageX(),event.getStageY()); }});
	
		slot1.getLabelCell().bottom();
		slot1.getLabel().setAlignment(Align.bottom, Align.bottom);
		slot1.layout();
		slot2.getLabelCell().bottom();
		slot2.getLabel().setAlignment(Align.bottom, Align.bottom);
		slot2.layout();
		slot3.getLabelCell().bottom();
		slot3.getLabel().setAlignment(Align.bottom, Align.bottom);
		slot3.layout();
		
		aInfo = new AchieveInfo();
		menu = new Menu();
		shop = new Shop(C.UI.SKIN);
		character = new Character("Character",C.UI.SKIN);


		charBtn = new Image(ResourceManager.getImage("icon_char"));
		charBtn.addListener(new ClickListener(){
			public void clicked (InputEvent event, float x, float y) {
				hide();
				character.unhide();
			}
		});

		shopBtn = new Image(ResourceManager.getImage("icon_shop"));
		shopBtn.addListener(new ClickListener(){
			public void clicked (InputEvent event, float x, float y) {
				hide();
				shop.unhide();
			}
		});	
		
		menuBtn = new Image(ResourceManager.getImage("icon_menu"));
		menuBtn.addListener(new ClickListener(){
			public void clicked (InputEvent event, float x, float y) {
				
				if (!menu.isVisible()){
					menu.unhide();
					hide();
				} else { 
					menu.hide();
					unhide();
				}
			}
		});	
		upperTable = new Table(C.UI.SKIN);
		upperTable.debug();
		upperTable.setSize(32*3+4*5, 40);
		upperTable.setPosition(Gdx.graphics.getWidth()-upperTable.getWidth(), Gdx.graphics.getHeight()-upperTable.getHeight());
		upperTable.add(shopBtn).pad(4).width(32).height(32).fill();
		upperTable.add(charBtn).pad(4).width(32).height(32).fill();
		upperTable.add(menuBtn).pad(4).width(32).height(32).fill();

		mainTable = new Table(C.UI.SKIN);
		mainTable.debug();
		mainTable.setSize(Gdx.graphics.getWidth()/3, Gdx.graphics.getHeight()/8);
		mainTable.setPosition(Gdx.graphics.getWidth()/2-mainTable.getWidth()/2, 10);
		mainTable.defaults().padBottom(4);
		mainTable.add(slot1).height(32).width(32).expandX();
		mainTable.add(slot2).height(32).width(32).expandX();
		mainTable.add(slot3).height(32).width(32).expandX();
		mainTable.add(weaponBar = new WeaponBar()).expandX().height(32).row();
		mainTable.add(hpBar = new HpBar()).colspan(4).minHeight(10).maxHeight(16).expand().fill().row();
		mainTable.add(expBar = new ExpBar()).colspan(4).minHeight(6).maxHeight(10).expandX().fill().bottom().padBottom(2).row();
		mainTable.add(mpBar = new MpBar()).colspan(4).minHeight(6).maxHeight(10).expandX().fill().bottom().padBottom(0).row();
		
		
		character.hide();
//		table.setBackground(new TextureRegionDrawable(ResourceManager.getImage("table_back")));
//		table.setBounds(0, Gdx.graphics.getHeight()-55, Gdx.graphics.getWidth(), 55);
//		table.align(Align.top | Align.left);
//		table.defaults().space(C.UI.SPACING/4).pad(C.UI.SPACING/4);
//		table.defaults().center().left();
//		table.debug();
//		level = (Label) table.add("Level: 10").center().spaceBottom(2).padBottom(2).getWidget();
//		table.add(hpBar = new HpBar()).width(200).height(32).spaceBottom(2).padBottom(2);
//		table.add(weaponBar = new WeaponBar()).width(64).height(36).spaceBottom(2).padBottom(2);
//		weapon = (Label) table.add("Pistol \n 9/9").spaceBottom(2).padBottom(2).getWidget();
//		table.add("").expandX().spaceBottom(2).padBottom(2);
//		table.add(slot1).spaceBottom(2).padBottom(2).width(32).height(32).fill();
//		table.add(slot2).spaceBottom(2).padBottom(2).width(32).height(32).fill();
//		table.add(slot3).spaceBottom(2).padBottom(2).width(32).height(32).fill();
//		table.add("").width(16).spaceBottom(2).padBottom(2);
//		table.add(charBtn).spaceBottom(2).padBottom(2).fill();
//		table.add(shopBtn).spaceBottom(2).padBottom(2).fill();
//		table.add(menuBtn).spaceBottom(2).padBottom(2).fill();
//		table.row().spaceBottom(2).padBottom(2);
//		table.add(expBar = new ExpBar()).colspan(12).height(12).fill().spaceTop(0).padTop(0).spaceBottom(2).padBottom(2);;
		weaponChanged(GameState.player.getWeapon());
//		vehicleSpeed = new Label("",C.UI.SKIN);
//		vehicleSpeed.setPosition(10, table.getY()-50);
//		vehicleHp = new Label("",C.UI.SKIN);
//		vehicleHp.setPosition(10, vehicleSpeed.getY()-50);
//		vehicleSpeed.setVisible(false);
//		vehicleHp.setVisible(false);
		init = true;
		
	}
	
	Table slotTable;
	int currentslotTable = -1;
	
	protected void chooseItemForSlot(final int slot, float x, float y) {
		if (GameState.player.items.size == 0)
			return;
		if (slotTable != null) {
			if (currentslotTable == slot && slotTable.isVisible())
				return;
			else
				slotTable.remove();
		}
		currentslotTable = slot;
		slotTable = new Table(C.UI.SKIN);
		slotTable.setSize(256, 32);
//		slotTable.debug();
		slotTable.left();
		slotTable.defaults().pad(4);
		slotTable.setBackground(new TextureRegionDrawable(ResourceManager.getImage("bar_back")));
		Array<Integer> items = new Array<Integer>();
		for (Item it : GameState.player.items)
			if (!items.contains(it.id, false))
				items.add(it.id);
		slotTable.setPosition(x, y-64);
		for(final int i : items){
			Item it = Item.getItemById(i);
			if (!it.isUseable())
				continue;
			Image image = new Image();
			image.setDrawable(new TextureRegionDrawable(ResourceManager.getImage(it.image)));
			image.addListener(new ClickListener(){
				public void clicked (InputEvent event, float x, float y) {
					int itemId = i;
					GameState.player.slots[slot] = itemId;
					slotsChanged();
					slotTable.addAction(Actions.sequence(Actions.fadeOut(0.5f),Actions.visible(false),Actions.run(new Runnable(){

						@Override
						public void run() {
							slotTable.remove();
						}})));
			}});
			slotTable.add(image);
		}
		slotTable.pack();
		slotTable.getColor().a = 0;
		slotTable.addAction(Actions.fadeIn(0.5f));
		GameState.getInstance().addActor(slotTable);
		slotTable.addAction(Actions.sequence(Actions.delay(5),Actions.fadeOut(0.5f),Actions.visible(false),Actions.run(new Runnable(){

			@Override
			public void run() {
				slotTable.remove();
			}})));

	}

	public void useSlot(int num){
		int itemId = GameState.player.slots[num];
		if (itemId != -1)
			GameState.player.use(itemId);
	}
	
	@Override
	public void add(Stage stage) {
		stage.addActor(mainTable);
		stage.addActor(upperTable);
		
		stage.addActor(shop);
		stage.addActor(character);
		menu.add(stage);
		stage.addActor(aInfo);
//		stage.addActor(vehicleSpeed);
//		stage.addActor(vehicleHp);
		stage.addActor(QuestDialog.dialog);
		update(1);
		slotsChanged();
		
		if (C.PROFILE != null)
			character.loadAchieves();
	}

	@Override
	public void remove(Stage stage) {
		mainTable.remove();
		upperTable.remove();
		aInfo.remove();
		shop.remove();
		character.remove();
//		vehicleSpeed.remove();
//		vehicleHp.remove();
		menu.remove();
		QuestDialog.dialog.remove();
		init = false;
	}
	
	
	public void update(float delta){
		if (hide)
			return;
		Player p = GameState.player;
		if (p == null)
			return;
		if (expBar.setExp(p.getExp(), p.needExp(), p.needExp(p.getLevel()-1)))
			expBar.addAction(Actions.repeat(3,Actions.sequence(Actions.color(Color.RED.cpy(), 0.5f),Actions.color(Color.WHITE.cpy(), 0.5f))));

		hpBar.setHp(p.getHp(),p.getMaxHp());
		mpBar.setMp(p.runTime,p.getMaxRunTime());
		
//		if (expBar.setExp(p.getExp(), p.needExp(), p.needExp(p.getLevel()-1))){
//			Utils.build().append("Level: ").append(p.getLevel());
//			level.setText(Utils.sb.toString());
//		}
		
//		weaponBar.setVisible(p.weaponArmed);
		
		weaponBar.weaponArmed  = p.weaponArmed;
//		weapon.setVisible(p.weaponArmed);
		
		
//		if (p.vehicle != null){
//			vehicleSpeed.setVisible(true);
//			vehicleHp.setVisible(true);
//			Utils.build().append("Speed: ").append(p.vehicle.getSpeedKMH()).append("Km/h");
//			vehicleSpeed.setText(Utils.sb.toStqring());
//			Utils.build().append("Health: ").append(p.vehicle.getHp()).append(" / ")
//						.append(p.vehicle.getMaxHp());
//			vehicleHp.setText(Utils.sb.toString());
//		} else {
//			vehicleSpeed.setVisible(false);
//			vehicleHp.setVisible(false);
//		}
	}
	
	public void slotsChanged(){
		int[] slots = GameState.player.slots;
		if (slots[0] != -1) {
			Item item = Item.items.get(slots[0]);
			slot1.getStyle().up = new TextureRegionDrawable(ResourceManager.getImage(item.image));
		}else
			slot1.getStyle().up = C.UI.SKIN.get(TextButtonStyle.class).up;
		if (slots[1] != -1) {
			Item item = Item.items.get(slots[1]);
			slot2.getStyle().up = new TextureRegionDrawable(ResourceManager.getImage(item.image));
		}else
			slot2.getStyle().up = C.UI.SKIN.get(TextButtonStyle.class).up;
		if (slots[2] != -1) {
			Item item = Item.items.get(slots[2]);
			slot3.getStyle().up = new TextureRegionDrawable(ResourceManager.getImage(item.image));
		}else
			slot3.getStyle().up = C.UI.SKIN.get(TextButtonStyle.class).up;
	}
	
	
	class WeaponBar extends Actor{
		
		public boolean weaponArmed = true;
		TextureRegion weapon,back;
		String text = "9/9";
		
		public WeaponBar(){
			weapon = ResourceManager.getImage("weapon0");
			back =  ResourceManager.getImage("bar_back");

		}
		
		public void draw (SpriteBatch batch, float parentAlpha) {
			batch.draw(back, getX(), getY(), getWidth(),getHeight());
			if (!weaponArmed)
				return;
			batch.draw(weapon, getX()+(getWidth()-weapon.getRegionWidth())/2, getY()-(getHeight()-weapon.getRegionHeight())/2+6);
			C.UI.FONT_MINI.setColor(getColor());
			C.UI.FONT_MINI.drawMultiLine(batch, text, getX(), getY()+8, getWidth(), HAlignment.CENTER);

		}
		
		public void weaponChanged(){
			
		}
	}

	public void weaponChanged(Weapon newWeapon) {
		if (currentWeapon == null){
			currentWeapon = newWeapon;
		} else {
			if (currentWeapon != newWeapon){
				currentWeapon = newWeapon;
				if (currentWeapon != null){
					weaponBar.weapon = ResourceManager.getImage(currentWeapon.image);
//					weapon.setText("");
					weaponBar.text = "";
				}else{
					//�������� ��� ��������� ����
//					weapon.setText("");
					weaponBar.text = "";
					
				}
			}
		}

		Utils.sb.setLength(0);
		if (currentWeapon != null && currentWeapon.weaponType != WeaponType.MELEE){
			Utils.sb.append(currentWeapon.ammo).
				 append("/").append(currentWeapon.maxAmmo).
				 append("/").append(currentWeapon.totalAmmo);
			weaponBar.text = Utils.sb.toString();
		}
	}	
	
	public void hide(){
//		mainTable.addAction(moveTo(0, mainTable.getTop(), 1));
		hide = true;
		GameState.getInstance().setUpdatePaused(true);
	}

	public void unhide() {
		GameState.getInstance().setUpdatePaused(false);
//		mainTable.addAction(sequence(moveTo(0, Gdx.graphics.getHeight()-mainTable.getHeight(), 1),run(new Runnable(){
//
//			@Override
//			public void run() {
//				GameState.getInstance().setUpdatePaused(false);
//			}})));
		hide = false;
	}
	
	public void levelGained(){
		level.addAction(repeat(3,sequence(color(Color.GREEN, 0.1f),color(Color.WHITE, 0.1f))));
		expBar.addAction(repeat(3,sequence(color(Color.RED, 0.1f),color(Color.WHITE, 0.1f))));
	}

	public void achieve(Achievement ac) {
		if (aInfo.isVisible())
			aInfo.list.add(ac);
		else
			aInfo.init(ac);
		character.addAchieve(ac);
	}

	@Override
	public void resize(int width, int height) {
		if (!init)
			return;
		QuestDialog.dialog.resize(width, height);
		mainTable.setSize(width/3, height/8);
		mainTable.setPosition(width/2-mainTable.getWidth()/2, 10);
		upperTable.setSize(32*3+4*5, 40);
		upperTable.setPosition(width-upperTable.getWidth(), height-upperTable.getHeight());
		
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
		public int expHave = 100;
		public int expNeed = 100;
		public int expNull = 0;
		float perc;
		public ExpBar(){
			hp = ResourceManager.getImage("exp_bar");
			back =  ResourceManager.getImage("bar_back");
		}
		
		public boolean setExp(int have, int fornextLevel, int forCurrenLevel){
		if (have == expHave)
			return false;
		boolean nextLevel = false;
		if (expNeed != fornextLevel)
			nextLevel = true;
		expHave = have;
		expNeed = fornextLevel;
		expNull = forCurrenLevel;
		int e1 = expHave - expNull;
		int e2 = expNeed - expNull;
		perc = e2/100f;
		perc = e1/perc/100f;
		return nextLevel;
	}

		public void draw (SpriteBatch batch, float parentAlpha) {
			batch.setColor(getColor());
			batch.draw(back, getX(), getY(), getWidth(), getHeight());
			batch.setColor(getColor().cpy().mul(Color.DARK_GRAY));
			batch.draw(back, getX()+1, getY()+1, getWidth()-2, getHeight()-2);
			batch.setColor(getColor());
			batch.draw(hp, getX()+1, getY()+1, getWidth()*perc-2, getHeight()-2);
		}
	}
	
	public static class MpBar extends Actor{
		TextureRegion hp, back;
		public int expHave = 100;
		public int expNeed = 100;
		public int expNull = 0;
		float perc;
		public MpBar(){
			hp = ResourceManager.getImage("mp_bar");
			back =  ResourceManager.getImage("bar_back");
		}
		
		public void setMp(float mp, float maxMp){
			perc = mp/maxMp;
		}

		public void draw (SpriteBatch batch, float parentAlpha) {
			batch.setColor(Color.WHITE);
			batch.draw(back, getX(), getY(), getWidth(), getHeight());
			batch.setColor(Color.BLACK);
			batch.draw(back, getX()+1, getY()+1, getWidth()-2, getHeight()-2);
			batch.setColor(Color.WHITE);
			batch.draw(hp, getX()+1, getY()+1, getWidth()*perc-2, getHeight()-2);
		}
	}	
}
