package com.zombie.effect;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.manager.ResourceManager;
import com.zombie.C;

public class FogEffect extends AbstractEffect {

	boolean moveLeft = false;
	TextureRegion fog;
	float scale = 1;
	
	public FogEffect(float scale){
		this.scale = scale;
		renderGroup = C.GROUP_LAST;
		fog = ResourceManager.getImage("clouds");
	}
	
	@Override
	public void update(int delta) {
		if (moveLeft)
			setX(getX()-delta*0.01f);
		else
			setX(getX()+delta*0.01f);
		
		if (getX() < -fog.getRegionWidth()*scale)
			moveLeft = false;
		if (getX() > C.MAP_WIDTH+fog.getRegionWidth())
			moveLeft = true;
	}

	@Override
	public void draw(SpriteBatch batch, ShapeRenderer shapeBatch) {
		batch.draw(fog, getX(), getY(),
				fog.getRegionWidth()/2, fog.getRegionHeight()/2,
				fog.getRegionWidth(), fog.getRegionHeight(), scale, scale, 0);
	}
	
	@Override
	public void remove() {
	
	}
	
	
	
	public boolean needDraw(Rectangle rect){
		Rectangle.tmp.set(getX(), getY(), fog.getRegionWidth(), fog.getRegionHeight());
		return rect.overlaps(Rectangle.tmp);
	}
}
