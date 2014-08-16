package com.zombie.effect;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.zombie.C;
import com.zombie.C.UI;

public class ScoreEffect extends AbstractEffect{

	public int count = 1;
	public Color color = Color.RED.cpy();
	
	public ScoreEffect(){
		setFullLifeTime(3000);
		position = new Vector2(50,500);
		renderGroup = C.GROUP_LAST;
	}
	
	@Override
	public void update(int delta) {
		setLifeTime(getLifeTime()-delta);
		setY(getY()+0.5f);
		color.a-=0.01f;
	}

	@Override
	public void remove() {
	}

	@Override
	public void draw(SpriteBatch batch, ShapeRenderer shapeBatch) {
		Color old = batch.getColor();
		batch.setColor(color);
		UI.FONT.draw(batch, "+"+count, getX(), getY());
		batch.setColor(old);
	}

	@Override
	public boolean needDraw(Rectangle rect) {
		return true;
	}

}
