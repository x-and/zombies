package com.zombie.effect;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.manager.ResourceManager;
import com.zombie.logic.object.GameObject;

public class TracerEffect extends AbstractEffect{

	public Color color;
	private GameObject owner;
	float alphaStart  = 1.0f;
	float alphaFinish  = 1.0f;
	static TextureRegion texture;
	Vector2 position2 = new Vector2();
	
	float startLifeTime, finishLifeTime;
	
	public TracerEffect(){
		color = new Color(Color.WHITE);
		color.a = 0.5f;
		if (texture == null)
			texture = ResourceManager.getImage("bullet0");
	}
	
	@Override
	public void update(int delta) {
		setLifeTime(getLifeTime() - delta);
		if (getOwner().isDead()){
			setLifeTime(-1);
			return;
		}
		position2.set(getOwner().getPos().current);

		float onePercent = getFullLifeTime()/100f;
		float percents = getLifeTime()/onePercent;
		onePercent = getFullLifeTime()/100f;
		percents = getLifeTime()/onePercent;
		alphaFinish = percents*0.01f/2;
		color.a = alphaFinish;
	}

	@Override
	public void draw(SpriteBatch batch, ShapeRenderer shapeBatch) {
		if (getLifeTime()<=0)
			return;
		Color oldColor = batch.getColor();
		batch.setColor(color);
		batch.draw(texture, position2.x, position2.y, 0,0, 2, position.dst(position2), 1, 1, angle+90);
		batch.setColor(oldColor);
	}

	public GameObject getOwner() {
		return owner;
	}

	public void setOwner(GameObject owner) {
		this.owner = owner;
	}

	@Override
	public void remove() {
	}

	@Override
	public boolean needDraw(Rectangle rect) {
		return true;
	}

}
