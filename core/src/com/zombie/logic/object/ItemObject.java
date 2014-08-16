package com.zombie.logic.object;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.zombie.C;
import com.zombie.logic.enums.ItemType;
import com.zombie.logic.enums.ObjectType;
import com.zombie.logic.item.Item;
import com.zombie.state.GameState;
import com.zombie.util.Utils;

public class ItemObject extends PhysicObject {

	public ItemType itemType = ItemType.ITEM;
	public int itemId;
	public long lifeTime;
	float rndSize = 0;
	boolean sizeUp = true;
	protected boolean pickupable = true;
	public ItemObject(float f, float g, Item it) {
		super(f, g);
		itemId = it.id;
		itemType = it.type;
		setSize(it.wh,it.wh);
		lifeTime = 60000;
		type = ObjectType.ETC;
		image = Item.items.get(itemId).image;
	}


	@Override
	public void update(float delta) {
		super.update(delta);
		setA(90);
		lifeTime-=delta;
		if (lifeTime <= 0){
			remove();
			return;
		}
		if (pickupable && GameState.player.distanceToObject(this) < C.TILESIZE){
			GameState.player.pickup(this);
		}
		
		if (sizeUp)
			rndSize+=0.2f;
		else
			rndSize-=0.2f;
		if (sizeUp && rndSize >=5)
			sizeUp = false;
		if (!sizeUp && rndSize <=-5)
			sizeUp = true;
	}

	@Override
	public void draw(SpriteBatch batch, ShapeRenderer shapeBatch) {
		if  (type == ObjectType.EXPLOSIVE){
			super.draw(batch, shapeBatch);
			return;
		}
		if (getTexture() != null){
			setW(getW()-rndSize+2);
			setH(getH()-rndSize+2);
			Utils.pushColor(batch);
			batch.setColor(Color.GREEN);
			batch.draw(getTexture(), getX()-getW()/2, getY()-getH()/2, getW()/2, getH()/2, getW(), getH(), 1, 1, getA(), true);
			setW(getW()+rndSize-2);
			setH(getH()+rndSize-2);
			Utils.popColor(batch);
		}
		
		batch.setColor(Color.WHITE);
		setW(getW()-rndSize);
		setH(getH()-rndSize);
		super.draw(batch, shapeBatch);
		setW(getW()+rndSize);
		setH(getH()+rndSize);
	}

	@Override
	public void createBody() {
	}

}
