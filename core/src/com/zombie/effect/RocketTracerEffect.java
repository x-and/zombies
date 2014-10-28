package com.zombie.effect;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.droidinteractive.box2dlight.ConeLight;
import com.droidinteractive.box2dlight.Light;
import com.manager.LightManager;
import com.zombie.C;
import com.zombie.logic.object.ExplosiveObject;
import com.zombie.state.GameState;

public class RocketTracerEffect extends AbstractEffect{

	public ExplosiveObject owner;
	public ParticleEffect effect;
	Light light;

	public RocketTracerEffect(ParticleEffect particleEffect, ExplosiveObject owner){
		this.owner = owner;
		light = new ConeLight(LightManager.getHandler(), 12, Color.YELLOW, 120, owner.getX(), owner.getY(), owner.imageAngle-180, 15);
		renderGroup = C.GROUP_PRE_NORMAL;
		effect = particleEffect;
		effect.setPosition(owner.getX(), owner.getY());
		GameState.addEmitter(effect);
	}	

	public ExplosiveObject getOwner() {
		return owner;
	}

	public void setOwner(ExplosiveObject owner) {
		this.owner = owner;
	}

	@Override
	public void update(int delta) {
		if (owner.isDead())
			setLifeTime(-1);
		light.setPosition(owner.getPos().current);
		light.setDirection(owner.imageAngle-180);
		float x1 = 4*MathUtils.cos((angle)*MathUtils.degRad);
		float y1 = 4*MathUtils.sin((angle)*MathUtils.degRad);
		effect.setPosition(owner.getX()+owner.getW()/2-x1, owner.getY()+owner.getH()/2-y1);
	}
	
	@Override
	public void remove() {
		effect.getEmitters().get(0).getEmission().setActive(false);
		effect.getEmitters().get(0).duration = 0;
		light.remove();
	}

	@Override
	public void draw(SpriteBatch batch, ShapeRenderer shapeBatch) {
	}

	@Override
	public boolean needDraw(Rectangle rect) {
		return false;
	}

}
