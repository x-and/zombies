package com.zombie.effect;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.manager.ResourceManager;
import com.zombie.C;
import com.zombie.logic.GameWorld;
import com.zombie.util.Rnd;
import com.zombie.util.Utils;

public class BloodEffect extends AbstractEffect
{

	public float upSpeed = 64f;	
	public float velocity = 0.5f;
	Color color;
	
	public BloodEffect(){
		renderGroup = C.GROUP_POST_EFFECT;
		color = Color.RED.cpy();
		color.a = 0.4f;
		setFullLifeTime(250L+Rnd.randomInt(-500, 500));
	}
	
	@Override
	public void update(int delta) {
		setLifeTime(getLifeTime()-delta);
		float x1 = velocity*MathUtils.cos(MathUtils.degRad*angle);
		float y1 = velocity*MathUtils.sin(MathUtils.degRad*angle);
		setX(getX()+x1);
		setY(getY()+upSpeed/1000f*delta+y1);
		upSpeed-= 0.2f*delta;
	}

	@Override
	public void draw(SpriteBatch batch, ShapeRenderer shape) {
		batch.end();
		Utils.beginShapeRenderer(shape,ShapeType.Filled);
		Gdx.gl.glEnable(GL10.GL_BLEND);
	    Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		shape.setColor(color);
		shape.circle(getX(), getY(), Rnd.randomInt(1, 3));
		shape.end();
		batch.begin();
	}

	@Override
	public void remove() {
		ScalingImageEffect eff = null;
		try{
		eff = new ScalingImageEffect();
		TextureRegion region = ResourceManager.getImage("meat"+Rnd.nextInt(3));
		eff.image = region;
		
		eff.setBounds(getX()-region.getRegionWidth()/2, getY()-region.getRegionHeight()/2, region.getRegionWidth(), region.getRegionHeight());
		eff.scale = 0.3f;
		eff.maxScale = 1.5f;
		eff.scaleVelocity = -0.025f;
		eff.setFullLifeTime(C.BLOOD_EFFECT_TIME);
		GameWorld.addEffect(eff);
		color = null;
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public boolean needDraw(Rectangle rect){
		Rectangle.tmp.set(getX(), getY(), 4, 4);
		return rect.overlaps(Rectangle.tmp);
	}
}
