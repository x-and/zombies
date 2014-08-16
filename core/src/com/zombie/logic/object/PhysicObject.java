package com.zombie.logic.object;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.manager.ThreadPoolManager;
import com.physics.Physics;
import com.zombie.C;

public abstract class PhysicObject extends GameObject {

	public Body body;
	
	public PhysicObject(){
	}
	
	public PhysicObject(int oID) {
		super(oID);
	}

	public PhysicObject(float x, float y) {
		super(x,y);
	}

	public abstract void createBody();
	
	public float getBodyX(){
		if (body == null)
			return pos.getX();
		return body.getPosition().x*C.BOX_TO_WORLD;
	}
	
	public float getBodyY(){
		if (body == null)
			return pos.getY();
		return body.getPosition().y*C.BOX_TO_WORLD;
	}

	public float getBodyA() {
		if (body == null)
			return pos.getA();
		return body.getAngle()*MathUtils.radDeg;
	}
	
	@Override
	public void update(float delta) {
		super.update(delta);
		pos.set(getBodyX(),getBodyY(),getBodyA());
	}

	
	public void dispose() {
		super.dispose();
		if (body != null){
			Physics.remove(body);
			body.setUserData(null);
			body = null;
		}
	}
	
	public void setBodyActive(final boolean b) {
		Physics.task(new Runnable(){

			@Override
			public void run() {
				body.setActive(b);
			}});
	}
	
	public void setBodyAngle(float angle) {
		setTransform(getBodyX(),getBodyY(),angle);
	}
	
	public void setTransform(float x, float y, float a) {
		Physics.task(new Transform(body,x*C.WORLD_TO_BOX,y*C.WORLD_TO_BOX,a));
	}

	//TODO смещать с текущих координат на новые постепенно, за 5-10 тиков
	public void setTransform(float x, float y, float a, boolean isSync) {
		if (body == null)
			return;
		if (!isSync)
			setTransform(x,y,a);
		else
			Physics.task(new Translate(body,x*C.WORLD_TO_BOX,y*C.WORLD_TO_BOX,a));
	}
	
	private class Transform implements Runnable{
		float x,y,a;
		Body body;
		Transform(Body b,float x, float y, float a){
			body = b;
			this.x = x;
			this.y = y;
			this.a = a;
		}
		@Override
		public void run() {
			if (body != null)
				body.setTransform(x, y, a*MathUtils.degRad);
		}
	}
	
	private class Translate implements Runnable{
		float x,y,a;
		Body body;
		int times = 30;
		Translate(Body b,float x, float y, float a){
			body = b;
			this.x = x - body.getPosition().x;
			this.y = y - body.getPosition().y;
			this.a = a*MathUtils.degRad - body.getAngle();
		}
		@Override
		public void run() {
			if (body != null)
				body.setTransform(body.getPosition().x + x/30,body.getPosition().y +  y/30,body.getAngle() + a/30);
			times--;
			if (times > 0)
				ThreadPoolManager.schedule(new Runnable(){
					@Override
					public void run() {
						Physics.task(Translate.this);
					}}, 10);
		}
	}


}
