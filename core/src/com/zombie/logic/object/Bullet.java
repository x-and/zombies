package com.zombie.logic.object;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.manager.ResourceManager;
import com.physics.BodyFactory;
import com.zombie.effect.TracerEffect;
import com.zombie.logic.Formulas;
import com.zombie.logic.GameWorld;
import com.zombie.logic.enums.ObjectType;
import com.zombie.logic.item.Weapon;
import com.zombie.logic.object.interfaces.Hitable;
import com.zombie.util.Rnd;


public class Bullet extends PhysicObject {

	public int damage = 10;
	public float density = 0.1f;
	public float damping = 0.1f;
	public long lifeTime = 3000;
	public boolean strongBullet = false;
	public GameObject lastIntersected;
	static TextureRegion texture;
	
	public Bullet(float x, float y, LiveObject own, float angle) {
		this(x,y,own,angle,4,4);
		owner = own;
	}

	public Bullet(float x, float y, LiveObject own, float angle,float width,float height) {
		super(x, y);
		owner = own;
		type = ObjectType.BULLET;
		setA(angle);
		setSize(width,height);
		if (texture == null)
			texture = ResourceManager.getImage("bullet0");
		if (Rnd.nextInt(3)  == 0){
			TracerEffect tracer = new TracerEffect();
			tracer.angle = angle;
			tracer.position.set(pos.current);
			tracer.setFullLifeTime(600L + Rnd.nextInt(300));
			tracer.setOwner(this);
			GameWorld.addEffect(tracer);
		}
	}	
	
	public Bullet(float x, float y, LiveObject object, float angle, boolean b,Color start, Color finish) {
		super(x, y);
		type = ObjectType.BULLET;
		owner = object;
		setA(angle);
		setSize(4,4);
	}

	@Override
	public void update(float delta) {
		super.update(delta);
		lifeTime-=delta*1000;
		if (lifeTime <= 0){
			remove();
			return;
		}
		if (body == null)
			return;
		float x1 = getVelocity()*delta*MathUtils.cos(getA()*MathUtils.degRad);
		float y1 = getVelocity()*delta*MathUtils.sin(getA()*MathUtils.degRad);
		body.applyForceToCenter(x1,y1,true);
	}

	@Override
	public void draw(SpriteBatch batch, ShapeRenderer shapeBatch) {
		if (damage == 0)
			return;
		batch.draw(texture, getX(), getY(), 0, 0, texture.getRegionWidth(), texture.getRegionHeight()/2, 1, 1, getA()+90);
	}

	@Override
	public void createBody() {
		body = BodyFactory.createBulletBox(getX(), getY(), getW(), getH(),getA(),density,damping);
		body.setUserData(this);
		float x1 = 5*getVelocity()*Gdx.graphics.getDeltaTime()*MathUtils.cos(getA()*MathUtils.degRad);
		float y1 = 5*getVelocity()*Gdx.graphics.getDeltaTime()*MathUtils.sin(getA()*MathUtils.degRad);
		body.applyForceToCenter(x1,y1,true);
	}

	public void push(StaticObject obj) {
		super.push(obj, damage);
		lifeTime = 0;
	}

	public void setParams(Weapon weapon) {
		damage = weapon.damage;
		density = weapon.bullet.density;
		damping = weapon.bullet.damping;
		setVelocity(weapon.bullet.velocity);
		strongBullet = weapon.strongBullet;
		image = weapon.bullet.image;
	}

	public void doDamage(Hitable live) {
		if (live == owner)
			return;
		Formulas.calcDamage(this,live);
		int dmg = damage - live.getHp();		
		if (strongBullet && dmg > 0)
			damage = dmg;
		else
			lifeTime = 0;
	}

	public void remove() {
		super.remove();
	}
		
		
}
