package com.zombie.logic.object.live;

import com.badlogic.gdx.math.MathUtils;
import com.manager.TimeManager;
import com.zombie.C;
import com.zombie.state.GameState;

public class CrawlingZombie extends Zombie {

	boolean isHoldPlayer = false;
	int holdingTime = 1000;
	long holdingStarted;
	
	public CrawlingZombie(float x, float y, String image, int w, int h) {
		super(x, y, image, w, h);
	}

	public CrawlingZombie(float x, float y, String image) {
		this(x, y, image, 52, 24);
	}
	
	protected void stepSound(){
		
	}
	
	protected boolean attack() {
		boolean result = super.attack();
		if (!result)
			return result;
		if (TimeManager.getLongTime() < holdingStarted+holdingTime*2)
			return true;
		isHoldPlayer = true;
		holdingStarted = TimeManager.getLongTime();
		return true;
	}
	
	@Override
	public void update(float delta) {
		super.update(delta);
		if (isDead() || wait || body == null)
			return;
		setTransform(body.getWorldCenter().x*C.BOX_TO_WORLD,body.getWorldCenter().y*C.BOX_TO_WORLD, getA());
		if (isHoldPlayer){
			Player p = GameState.player;
			if (distanceToObject(p) > Math.min(getW(), getH())/2+2){
				float angl = p.getAngleToObject(this);
				float x1 = getVelocity()*2*MathUtils.cos(MathUtils.degRad*angl);
				float y1 = getVelocity()*2*MathUtils.sin(MathUtils.degRad*angl);
				p.body.applyForceToCenter(x1,y1,true);
			}
		}
		if (TimeManager.getLongTime() > holdingStarted+holdingTime)
			isHoldPlayer = false;
	}
	
}
