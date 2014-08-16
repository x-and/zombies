package com.zombie.effect;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class ImageEffect extends AbstractEffect
{
	public float opacity = 1;
	public float actualOpacity = 1;
	public float scale = 1f;
	public Vector2 size = new Vector2();
	public Vector2 origin = new Vector2();
	public TextureRegion image;

	public ImageEffect(){
		setFullLifeTime(60000);
	}
	
	public void setBounds(float x,float y,float w,float h){
		size.set(w, h);
		position.set(x, y);
		origin.set(size).scl(0.5f);
	}
	
	@Override
	public void update(int delta) {
		setLifeTime(getLifeTime()-delta);
		if (getFullLifeTime() >= 10000){
			if (getLifeTime() <= 10000){
				opacity = getLifeTime()/10000f;
				actualOpacity = getLifeTime()/10000f;
			}
		} else {
			float onePercent = getFullLifeTime()/100f;
			float percents = getLifeTime()/onePercent;
			actualOpacity = Math.min(opacity*percents*0.01f, 1);
		}
	}

	@Override
	public void draw(SpriteBatch batch, ShapeRenderer shapeBatch) {
		if (image == null)
			return;
//		Color old = batch.getColor();
		batch.setColor(1, 1, 1, actualOpacity);
		batch.draw(image, getX(), getY(), origin.x, origin.y, getW(),getH(), scale, scale, angle);
		batch.setColor(Color.WHITE);
	}

	@Override
	public void remove() {
		size = null;
		position = null;
		origin = null;
		image = null;
	}

	public void setBounds(float x, float y, TextureRegion image) {
		setBounds(x,y,image.getRegionWidth(),image.getRegionHeight());
		this.image = image;
	}
	
	public boolean needDraw(Rectangle rect){
		Rectangle.tmp.set(getX()-getW()/2, getY()-getH()/2, getW()*2, getH()*2);
		return rect.overlaps(Rectangle.tmp);
	}

	public float getW(){
		return size.x;
	}
	
	public float getH(){
		return size.y;
	}

}
