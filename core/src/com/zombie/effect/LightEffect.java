package com.zombie.effect;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.droidinteractive.box2dlight.Light;

public class LightEffect extends AbstractEffect {

	public Light light;
	public boolean permanent = false;
	long lastTimer = 0;
	float distance = 0;
	public long time = -1;
	public float percent;
	public TextureRegion texture;
	
	public LightEffect(Light light2) {
		light = light2;
		position.set(light.getX(),light.getY());
		distance = light.getDistance();
	}

	@Override
	public void draw(SpriteBatch batch, ShapeRenderer shapeBatch) {
		if (texture == null)
			return;
//		batch.draw(texture, getX(), getY(), 0,0, texture.getRegionWidth(), texture.getRegionHeight(), 1, 1, 0);	
	}

	@Override
	public boolean needDraw(Rectangle rect) {
		return rect.contains(position);
	}

	@Override
	public void update(int delta) {
		if (!permanent)
			setLifeTime(getLifeTime()-delta);
		if (!permanent)
			light.setDistance(light.getDistance()*1.2f);
		else {
			if (time < 0)
				return;
			lastTimer+= time/delta;
			float dist = distance-(distance*percent/2) + (distance*percent)*MathUtils.sin(lastTimer*MathUtils.degRad);
			light.setDistance(dist);
		}
	}

	@Override
	public void remove() {
		light.remove();
	}

}
