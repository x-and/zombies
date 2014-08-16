package com.zombie;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public interface Renderable {

	public void draw(SpriteBatch batch, ShapeRenderer shapeBatch);
	
	public int getRenderGroup();

	public boolean needDraw(Rectangle rect);
}
