package com.zombie.logic.object.live;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.manager.ResourceManager;
import com.manager.TimeManager;
import com.physics.Physics;
import com.zombie.C;
import com.zombie.logic.Faction;
import com.zombie.logic.Formulas;
import com.zombie.logic.ai.AI;
import com.zombie.logic.ai.MeleeAI;
import com.zombie.logic.ai.action.Action;
import com.zombie.logic.ai.action.ActionAttack;
import com.zombie.logic.ai.action.ActionType;
import com.zombie.logic.enums.AnimType;
import com.zombie.logic.object.GameObject;
import com.zombie.logic.object.LiveObject;
import com.zombie.logic.object.interfaces.Hitable;
import com.zombie.state.GameState;
import com.zombie.util.Rnd;
import com.zombie.util.SoundUtils;

public class Zombie extends LiveObject {
	
	int result = 0;	
	
	public Zombie(float x, float y){
		this(x,y,"zombie1");
	}
	
	public Zombie(float x, float y, String image){
		this(x,y,image,28,28);
	}
	
	public Zombie(float x, float y, String image, int w, int h){
		super(x,y,image);
		faction = Faction.ZOMBIE;
		name="Zombie";
		setSize(w,h);
		GameState.zombies++;
	}

	@Override
	protected boolean performAction(){
		if (super.performAction())
			return true;
		Action act = getAI().action;
		if (act.type == ActionType.ATTACK){
			ActionAttack attack = (ActionAttack) act;
			if (!attack.started){
				if (attack())
					attack.started = true;
			} else {
				if (getAI().getTarget() != null)
					setA(getAngleToObject(getAI().getTarget()));
			}
		}
		return true;
	}
	
	@Override
	public void update(float delta) {
		super.update(delta);
		if (isDead()){
			if (current.isAnimationFinished(TimeManager.getLongTime()-deathTime)){
				addToBackground();
				remove();
				GameState.zombies--;
			}
			return;
		}
		performAction();
		if (getAnimation() == AnimType.ATTACK){
			if (current != null && current.isAnimationFinished((TimeManager.getLongTime()-lastHit)))
				hit();
			
		}
	}

	protected boolean attack() {
		if (TimeManager.getLongTime() < lastHit+hitTime)
			return false;
		lastHit = TimeManager.getLongTime();
		setAnimation(AnimType.ATTACK);
//		if (Utils.rnd.nextBoolean())
//			attackAnim = attackAnimations[0];
//		else
//			attackAnim = attackAnimations[1];
		return true;
	}
	
	// �������� ���� �������. ���������� �������� �� STAND
	void hit(){
		final float x1 = (getW()+getH())*MathUtils.cos(getA()*MathUtils.degRad);
		final float y1 = (getW()+getH())*MathUtils.sin(getA()*MathUtils.degRad);

		Physics.task(new Runnable(){

			@Override
			public void run() {
				Physics.world.rayCast(new RayCastCallback(){

					@Override
					public float reportRayFixture(Fixture fixture, Vector2 point,
							Vector2 normal, float fraction) {

						if (fixture.getBody().getUserData() instanceof Hitable){
							Formulas.calcDamage(getDamage(), Zombie.this, (Hitable) fixture.getBody().getUserData());
							result = 1;
						}
//						else if (fixture.getBody().getUserData() instanceof Hitable){
//							fixture.getBody().getUserData().hitted(getDamage(),Zombie.this)
////							push((StaticObject) fixture.getBody().getUserData(),getDamage());
//							result = 2;
//						}
						return 0;
					}}, body.getPosition(),  body.getPosition().cpy().add(x1*C.WORLD_TO_BOX, y1*C.WORLD_TO_BOX));	
			}});

		setAnimation(AnimType.STAND);
		getAI().action.done = true;
		getAI().actionDone();
	}

	@Override
	protected void onHit(GameObject damager) {
		if (Rnd.nextBoolean())
			playSound();
	}
	
	public AI getAI(){
		if (ai == null)
			ai = new MeleeAI(this);
		return ai;
	}
	
	protected void playSound() {
		SoundUtils.playSound(ResourceManager.getSound("zombie_"+Rnd.nextInt(6)),pos.current,this);
	}

}
