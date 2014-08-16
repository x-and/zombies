package com.zombie.effect;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.manager.ResourceManager;
import com.physics.Physics;
import com.zombie.C;
import com.zombie.logic.GameWorld;
import com.zombie.logic.object.Bullet;
import com.zombie.util.Rnd;
import com.zombie.util.SoundUtils;

public class RicochetEffect extends AbstractEffect implements RayCastCallback {

	float normalAngle, velocity;
	
	float alphaFinish  = 0.5f;
	public Color color;
	Vector2 newPosition = new Vector2();
	float vx,vy;
	static TextureRegion texture;

	/*dont add this effect, it adds automatically */
	public RicochetEffect(final Body b1, final Body b2,Bullet b){
		position = new Vector2(b1.getPosition().x,b1.getPosition().y);
		position.scl(C.BOX_TO_WORLD);
		vx = b1.getLinearVelocity().x;
		vy = b1.getLinearVelocity().y;
		angle = (float) b.getA();
		velocity = b.getVelocity();
		setFullLifeTime(100);
		color = new Color(Color.WHITE);
		color.a = alphaFinish;
		if (b1.getPosition().dst(b2.getPosition())<0.1f){
			setFullLifeTime(0);
			return;
		}
		Physics.task(new Runnable(){
			@Override
			public void run() {
				Physics.world.rayCast(RicochetEffect.this, b1.getPosition(), b2.getPosition());
			}});
		if (texture == null)
			texture = ResourceManager.getImage("bullet0");
	}
	
	@Override
	public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal,
			float fraction) {

		normalAngle = (float) Math.toDegrees(Math.atan2(point.y-(point.y+normal.y), point.x-(point.x+normal.x)));
		if (normalAngle < 0)
			normalAngle = normalAngle+360;
		
		float a = normalAngle-(angle-normalAngle);

		if (Math.abs(a) < 15)
			return 0;

		angle = a;
		angle += Rnd.randomInt(-10, 10);
		newPosition.set(point);
		newPosition.scl(C.BOX_TO_WORLD);
		position.set(newPosition);
		SoundUtils.playSound(ResourceManager.getSound("ric_"+ Rnd.nextInt(2)),position);
		GameWorld.addEffect(this);
		return 0;
	}

	@Override
	public void update(int delta) {
		if (newPosition.x == 0 && newPosition.y == 0){
			setLifeTime(-1);
			return;
		}

		setLifeTime(getLifeTime()-delta);
		float x1 = vx*delta/1000f*MathUtils.cos(MathUtils.degRad*angle);
		float y1 = vy*delta/1000f*MathUtils.sin(MathUtils.degRad*angle);
		newPosition.x+=x1*C.BOX_TO_WORLD;
		newPosition.y+=y1*C.BOX_TO_WORLD;

		float onePercent = getFullLifeTime()/100f;
		float percents = getLifeTime()/onePercent;
		alphaFinish = percents*0.01f/2;
		
		color.a  = 	alphaFinish;
	}

	@Override
	public void draw(SpriteBatch batch, ShapeRenderer shape) {
		if (getLifeTime()<=0)
			return;
		Color oldColor = batch.getColor();
		batch.setColor(color);
		batch.draw(texture, getX(), getY(), 0,0, 2, position.dst(newPosition), 1, 1, angle+90);
		batch.setColor(oldColor);
	}

	@Override
	public void remove() {

	}

	@Override
	public boolean needDraw(Rectangle rect) {
		return true;
	}
}
