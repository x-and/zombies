package com.zombie.ui.window;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.esotericsoftware.minlog.Log;
import com.manager.ResourceManager;
import com.zombie.C;
import com.zombie.logic.enums.ItemType;
import com.zombie.logic.item.Explosive;
import com.zombie.logic.item.Heal;
import com.zombie.logic.item.Item;
import com.zombie.logic.item.Weapon;
import com.zombie.logic.item.WeaponType;
import com.zombie.state.GameState;

public class Shop extends Table {

	Label money;
	ScrollPane pane;
	Map<Integer,ItemCell> cells;
	
	public Shop(Skin skin) {
		super( skin);
		init();
	}
	Table table;
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha){
		Log.info("Shop", "draw");
		super.draw(batch, parentAlpha);
	}
	
	private void init() {
		cells = new HashMap<Integer,ItemCell>();
		setBackground(new TextureRegionDrawable(ResourceManager.getImage("table_back")));
		setFillParent(true);
//		debug();
		table = new Table(C.UI.SKIN);
		table.setWidth(Gdx.graphics.getWidth());
		table.defaults().pad(C.UI.SPACING/2).space(C.UI.SPACING/2);
//		table.debug();
		initShop(table);

		pane = new ScrollPane(table, C.UI.SKIN);
		pane.setVelocityY(0);
		pane.setSmoothScrolling(true);
		pane.setFadeScrollBars(false);
		pane.setScrollbarsOnTop(true);
		
		add(pane).colspan(6).fill().expand();
		row();
		
		Button back = new TextButton("Back",C.UI.SKIN);
		back.addListener(new ClickListener(){
			public void clicked (InputEvent event, float x, float y) {
				hide();
				GameState.getInstance().ui.unhide();
			}
		});
		back.setSize(C.UI.BTN_BACK_WIDTH, C.UI.BTN_BACK_HEIGHT);
		//.fill().
		add(back).left().height(C.UI.BTN_BACK_HEIGHT).width(C.UI.BTN_BACK_WIDTH).padLeft(C.UI.SPACING/2).padBottom(C.UI.SPACING/2).fill();
		money  = (Label) add("Money: "+ GameState.player.getStat().money).colspan(2).getWidget();
		money.setColor(Color.RED);
		add().colspan(3);
		
		setVisible(false);
		setBounds(0,0, Gdx.graphics.getWidth(), getHeight());
	}
	

	private void initShop(Table table) {
		for(final Item it : Item.shopItems){
			ItemCell cell = new ItemCell(it, C.UI.SKIN);
			cells.put(it.id, cell);
			table.add(cell).fill().expandX().row();
			checkItem(it.id);	
		}
	}

	public void hide(){
		addAction(Actions.sequence(Actions.moveTo(0, -Gdx.graphics.getHeight(), 1),Actions.visible(false)));
		GameState.getInstance().setScrollFocus(null);
	}

	public void unhide() {
		addAction(Actions.sequence(Actions.visible(true),Actions.moveTo(0, 0, 1)));
		checkMoney();
		GameState.getInstance().setScrollFocus(pane);
		GameState.getInstance().setKeyboardFocus(pane);
	}
	
	
	void checkMoney(){
		money.setText("Money: " + GameState.player.getStat().money);
	}
	
	@Override
	public void setVisible(boolean visible){
		super.setVisible(visible);
//		GameState.getInstance().setUpdatePaused(visible);
		if (!visible)
			setPosition(0,0);
		else
			setPosition(0,-Gdx.graphics.getHeight());
	}
	
	class ItemCell extends Table{
		
		Item it;
		Button buy, buyAmmo;
		
		public ItemCell(Item item, Skin skin){
			super(skin);
			it = item;
//			debug();
			setBackground(new TextureRegionDrawable(ResourceManager.getImage("table_back")));

			TextureRegion reg = ResourceManager.getImage(it.image);
			Image image = new Image(reg);

			defaults().pad(C.UI.SPACING/8).space(C.UI.SPACING/8);
			add("").width(64).height(0).padBottom(0).padTop(0).row();
			add(image);
			add(item.name).width(100).center().left();

			if (it.type == ItemType.WEAPON){
				add("Dmg "+ ((Weapon)it).damage+ "\n\nAcc "+ Math.round((100-((Weapon)it).rndAngle))).width(100).center().left();
			
				if (((Weapon)it).weaponType != WeaponType.MELEE)
					add("Ammo "+((Weapon)it).maxAmmo+ "\n\nRate "+(60000/((Weapon)it).hitTime)).width(80).center().left();
				if (((Weapon)it).weaponType != WeaponType.MELEE)
					add(buyAmmo = createButtonBuyAmmo(it)).width(120).right();			
			} else if  (it.type == ItemType.HEAL){
				add("Heal "+ ((Heal)it).heal).width(100).center().left();
			} else if  (it.type == ItemType.EXPLOSIVE){
				Explosive ex = (Explosive) it;
				add("Dmg "+ ex.damage+ "\n\nRadius "+ ex.radius).width(100).center().left();
				if (ex.condition.equalsIgnoreCase("timer")){
					add("Timer \n\n"+ ex.timer/1000f+" sec").width(80).center().left();
				} else if (ex.condition.equalsIgnoreCase("radio")){
					add("Radio \n\ncontrol").width(80).center().left();
				} else if (ex.condition.equalsIgnoreCase("detector")){
					add("Detector \n\nradius " + ex.detectorRadius).width(80).center().left();
				}
			}
			add().expandX();
			add("Price "+ it.price+" $").width(120).left();
			add(buy = createButtonBuy(it)).width(64).right();

		}
		
	}
	
	Button createButtonBuy(final Item it){
		Button buy = new TextButton("Buy", C.UI.SKIN);
		buy.addListener(new ClickListener(){
			int itemId = it.id;
			Item item = it;
			public void clicked (InputEvent event, float x, float y) {
				if (item.price <= 0)
					return;
				if (item.type == ItemType.WEAPON && GameState.player.containsItem(itemId))
					return;
				if (GameState.player.getStat().money >= item.price){
					GameState.player.pickup(item);
					GameState.player.getStat().money-=item.price;
				}
				checkMoney();
				checkItem(it.id);
			}
		});
		return buy;
	}
	
	protected void checkItem(int id) {
		ItemCell cell = cells.get(id);
		if (cell.it.type == ItemType.WEAPON && GameState.player.containsItem(id))
			cell.buy.setDisabled(true);
	}

	Button createButtonBuyAmmo(final Item it){
		final Item ammo = Item.getAmmoForWeapon((Weapon) it);
		Button buyAmmo = new TextButton("Ammo : "+ ammo.price+" $", C.UI.SKIN);
		buyAmmo.addListener(new ClickListener(){
			Item item = ammo;
			public void clicked (InputEvent event, float x, float y) {
				if (item.price <= 0)
					return;
				if (GameState.player.getStat().money >= item.price){
					GameState.player.pickup(item);
					GameState.player.getStat().money-=item.price;
				}
				checkMoney();
			}
		});
		return buyAmmo;
	}

	public void resize(int width, int height) {
		table.setWidth(width);
		table.invalidate();
	}	
	
}
