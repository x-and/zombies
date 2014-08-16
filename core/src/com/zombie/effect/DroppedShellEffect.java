package com.zombie.effect;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.manager.ResourceManager;
import com.zombie.logic.GameWorld;
import com.zombie.util.Rnd;
import com.zombie.util.SoundUtils;

public class DroppedShellEffect extends AbstractEffect
{
	
	boolean up = true;
	float upSpeed = 64f;
	TextureRegion image = ResourceManager.getImage("shell");
	
	@Override
	public void update(int delta) {
		setLifeTime(getLifeTime()-delta);
		angle+= 512f/1000f*delta;
		setX(getX()+10f/1000f*delta);
		setY(getY()+ upSpeed/1000f*delta);
		upSpeed-= 0.1f*delta;
	}

	@Override
	public void remove() {
		ImageEffect eff = new ImageEffect();
		eff.setBounds(getX(), getY(), image.getRegionWidth(), image.getRegionHeight());
		eff.angle = angle;
		eff.image = image;
		GameWorld.addEffect(eff);
		SoundUtils.playSound(ResourceManager.getSound("shell_"+Rnd.nextInt(4)),position);
	}

	@Override
	public void draw(SpriteBatch batch, ShapeRenderer shapeBatch) {
		batch.draw(image, getX(), getY(), image.getRegionWidth()/2, image.getRegionHeight()/2, image.getRegionWidth(), image.getRegionHeight(), 1, 1, angle);
	}

	public boolean needDraw(Rectangle rect){
		Rectangle.tmp.set(getX(), getY(), image.getRegionWidth(), image.getRegionHeight());
		return rect.overlaps(Rectangle.tmp);
	}
}
