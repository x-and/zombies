package com.zombie.logic.object;

import java.util.concurrent.ScheduledFuture;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation2D;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.manager.ResourceManager;
import com.manager.TimeManager;
import com.physics.BodyFactory;
import com.physics.Physics;
import com.zombie.C;
import com.zombie.achieve.AchieveSystem;
import com.zombie.effect.BloodEffect;
import com.zombie.effect.ImageEffect;
import com.zombie.effect.ScoreEffect;
import com.zombie.logic.AnimationHandler;
import com.zombie.logic.GameWorld;
import com.zombie.logic.ai.AI;
import com.zombie.logic.ai.AIState;
import com.zombie.logic.ai.NullAI;
import com.zombie.logic.ai.action.Action;
import com.zombie.logic.ai.action.ActionMove;
import com.zombie.logic.ai.action.ActionPath;
import com.zombie.logic.ai.action.ActionType;
import com.zombie.logic.enums.AnimType;
import com.zombie.logic.enums.ObjectType;
import com.zombie.logic.item.Weapon;
import com.zombie.logic.knownlist.LiveKnownList;
import com.zombie.logic.object.interfaces.Hitable;
import com.zombie.logic.object.interfaces.Searchable;
import com.zombie.logic.object.stat.LiveStat;
import com.zombie.logic.object.vehicle.Vehicle;
import com.zombie.logic.quest.Quest;
import com.zombie.state.GameState;
import com.zombie.util.Cam;
import com.zombie.util.Rnd;
import com.zombie.util.SoundUtils;
import com.zombie.util.Utils;

public abstract class LiveObject extends PhysicObject implements Hitable{


	protected long lastStep = TimeManager.getLongTime()+Rnd.nextInt(1000);
	protected int hitTime = 750;
	protected long deathTime = -1;
	protected long lastHit;

	public String building = null;
	protected boolean isDead = false;
	protected boolean isWalking = false;	
	
	public ScheduledFuture<?> reload = null;

	public Vehicle vehicle;
	protected AI ai;

	protected Animation current;
	protected AnimationHandler animHandler;
	private Weapon weapon, previousWeapon;
	
	public LiveObject(float x, float y) {
		super(x, y);
		getKnownList().setAutoUpdate(500);
		type = ObjectType.LIVE;
		setSize(28,28);
		setVelocity(0.5f);
	}

	public LiveObject(float x, float y,String img) {
		this(x, y);
		setImage(img);
	}
	
	public LiveObject(float x, float y,String img,int w,int h) {
		this(x, y,img);
		setSize(w,h);
	}
	
	abstract protected void onHit(GameObject damager);
	
	@Override
	public void update(float delta) {
		if (isDead())
			return;
		super.update(delta);
		if (vehicle != null)
			Physics.task(new Runnable(){
				public void run() {
					body.setTransform(vehicle.body.getPosition(), body.getAngle());
				}});
	}
	
	// ������������ �������� � ��������.
	protected boolean performAction() {
		if (getAI().updateNeeded)
			getAI().update();
		Action act = getAI().action;
		if (act.type == ActionType.STAND || act.type == ActionType.WAIT){
			setAnimation(AnimType.STAND);
			return true;
		} 
		else if (act.type == ActionType.MOVE || act.type == ActionType.FLEE || act.type == ActionType.FOLLOW || act.type == ActionType.PATH){
			if (getAI().state == AIState.INTEREST || getAI().state == AIState.NO_TARGET)
				setWalking(true);
			else 
				setWalking(false);
			
			if (act.done || act.interrupted)
				return true;
			GameObject target = act.target;
			setAnimation(AnimType.MOVE);
			if (act.type == ActionType.MOVE){
				//other checks in ai
				act.checkCondition(getAI(), this);
				Vector2 coords  = ((ActionMove)act).coords;
				setA(MathUtils.radDeg*MathUtils.atan2(coords.y-getY(), coords.x-getX()));
			} else if (act.type == ActionType.FLEE){
				setA(180+getAngleToObject(target));
			} else if (act.type == ActionType.FOLLOW){
				setA(getAngleToObject(target));
			} else if (act.type == ActionType.PATH){
				Vector2 coords  = ((ActionPath)act).coords;
				setA(MathUtils.radDeg*MathUtils.atan2(coords.y-getY(), coords.x-getX()));
			}
			float x1 = getVelocity()*MathUtils.cos(MathUtils.degRad*getA());
			float y1 = getVelocity()*MathUtils.sin(MathUtils.degRad*getA());
			body.applyForceToCenter(x1,y1,true);
			stepSound();
		}
		return false;
	}

	private void setWalking(boolean b) {
		if (isWalking == b)
			return;
		isWalking = b;
		changeAnimation();
	}

	protected void setImage(String image) {
		this.image = image;
		animHandler = new AnimationHandler(image);
		current = animHandler.stand;
	}
	
	public void setMaxHp(int i) {
		if (i <= 0)
			return;
		getStat().maxHp = i;
		setHp(i);
	}
	
	public void hitted(float value, GameObject damager){
		if (getHp() <= 0 || isDead()){
			return;
		}
		value -= getStat().defence;
		if (value <= 0)
			value = 1;
		
		if (damager == GameState.player)
			GameState.player.getStat().hits++;
		setHp((int) (getHp()-value));
		onHit(damager);
		getAI().onHit(damager);
		
		if (quests != null)
			for(Quest q : quests)
				q.onHit(getStat().damage,this);
		
		if (getHp() <= 0)
			doDie(damager);
		
		addEffect((int) value,damager);
	}
	
	protected void playSound() {
		SoundUtils.playSound(ResourceManager.getSound("punch_"+Rnd.nextInt(3)),pos.current,this);
	}

	private void addEffect(int damage,GameObject damager) {
		System.out.println("addEffect");
		int count = 2 + damage/15;
		if (count > 12)
			count = 12;
		for(int i = 0 ; i< count;i++){
			BloodEffect effect = new BloodEffect();
			effect.position.set(getPos().current);
			effect.upSpeed += -4f + 6*Rnd.nextFloat();
			effect.angle = (float) damager.getAngleToObject(this) + (-60+Rnd.nextInt(120));
			effect.velocity = 0.2f + 0.2f*Rnd.nextFloat()+damage/100f*Rnd.nextFloat();
			GameWorld.addEffect(effect);
		}
		playSound();
	}

	public void doDie(GameObject killer){
		isDead = true;
		deathTime = TimeManager.getLongTime();
		setAnimation(AnimType.DIE);
		if (killer == GameState.player){
			GameState.player.getStat().kills++;
			GameState.player.setExp(GameState.player.getExp()
					+ (getMaxHp()/15));
			int money = getMaxHp()/5*getLevel() + getStat().defence;
			money = Math.round(money*getVelocity()*2);
			GameState.player.getStat().money+= money;
			GameState.player.getStat().earned+= money;
			ScoreEffect eff = new ScoreEffect();
			eff.count = money;
			eff.position.set(pos.current);
			eff.color = Color.GREEN;
			GameWorld.addEffect(eff);
			AchieveSystem.onKill();
		}
		if (killer.type == ObjectType.LIVE)
			((LiveObject) killer).getAI().onKill(this);

		doDrop();
		getAI().doDie(killer);
		
		if (quests != null)
			for(Quest q : quests)
				q.onKill(this);
	}

	@Override
	public boolean isDead(){
		return isDead;
	}

	@Override
	public void createBody() {
		if (getW() == getH())
			body = BodyFactory.createNpcCircle(getX(), getY(), getW()/2-getW()/8);
		else
			body = BodyFactory.createNpcBox(getX(), getY(), getW()-getW()/8,getH()-getW()/8); 
		body.setUserData(this);
	}
	
	public int getHp() {
		return getStat().hp;
	}
	
	public int getLevel() {
		return getStat().level;
	}

	public void setHp(int hp) {
		getStat().hp = hp;
		if (getStat().hp < 0)
			getStat().hp = 0;
	}

	public int getMaxHp() {
		return getStat().maxHp;
	}
	
	protected void stepSound() {
		if (lastStep+stepTime() < TimeManager.getLongTime()){
			lastStep = TimeManager.getLongTime();
			String sound = Utils.getSoundForTile(this);
			SoundUtils.playSound(ResourceManager.getSound(sound+Rnd.nextInt(4)),0.4f,1f+(-0.1f+0.2f*Rnd.nextFloat()), 0, false,pos.current,this);
		}
	}
	
	protected int stepTime(){
		return 400;
	}

	public int getDamage() {
		return getStat().damage;
	}

	public void setDamage(int damage) {
		getStat().damage = damage;
	}

	public LiveStat getStat(){
		if (stats == null)
			stats = new LiveStat(this);
		return (LiveStat) stats;
	}
	
	public AI getAI(){
		if (ai == null)
			ai = new NullAI(this);
		return ai;
	}

	public Weapon getWeapon() {
		return weapon;
	}

	public void setWeapon(Weapon weapon) {
		this.weapon = weapon;
	}

	public Weapon getPreviousWeapon() {
		return previousWeapon;
	}

	public void setPreviousWeapon(Weapon previousWeapon) {
		this.previousWeapon = previousWeapon;
	}
	
	public LiveKnownList getKnownList() {
		if (knownlist == null)
			knownlist = new LiveKnownList(this);
		return (LiveKnownList) knownlist;
	}
	
	public float getVelocity(){
		return super.getVelocity()*(isWalking?0.5f:1f);
	}
	
	public void vehicleEntered(Vehicle v,boolean isDriver){
		vehicle = v;
		if (isDriver)
			vehicle.setDriver(this);
		setBodyActive(false);
	}

	public void vehicleExited(Vehicle v) {
		vehicle.exited(this);
		vehicle = null;
		setBodyActive(true);
		setTransform(v.getX(), v.getY(), getA());
	}
	
	public boolean inVehicle(){
		return vehicle != null;
	}
	
	protected void addToBackground() {
		ImageEffect eff = new ImageEffect();
		eff.image = ((Animation2D) current).getFrame(((Animation2D) current).frameCount()-1);
		eff.setBounds(getX()-getW()/2, getY()-getH()/2, getW(),getH());
		eff.angle = getA();
		GameWorld.addEffect(eff);
	}
	
	@Override
	public void draw(SpriteBatch batch, ShapeRenderer shapeBatch) {
		TextureRegion region = null;
		if (getAnimation() == AnimType.DIE){
			region = current.getKeyFrame(TimeManager.getLongTime()-deathTime);
		} else if (getAnimation() == AnimType.ATTACK){
			region = current.getKeyFrame(TimeManager.getLongTime()-lastHit);
		} else
			region = current.getKeyFrame(TimeManager.getLongTime());
		int w = region.getRegionWidth();
		int h = region.getRegionHeight();
		
		batch.draw(region, getX()-w/2, getY()-h/2, w/2, h/2, w, h, 1, 1, getA());
	}
	
	public void setAnimation(AnimType animation) {
		super.setAnimation(animation);
		changeAnimation();
	}

	// base animations:
	// move,walk,stand,die,melee attack
	// other must be implemented in extending classes
	protected void changeAnimation() {
		if (getAnimation() == AnimType.STAND)
			current = animHandler.stand;
		else if (getAnimation() == AnimType.MOVE/* || getAnimation() == AnimType.FLEE*/){
			if (isWalking)
				current = animHandler.move[0];
			else
				current = animHandler.move[1];
		} else if (getAnimation() == AnimType.DIE)
			current = animHandler.die;
		else if (getAnimation() == AnimType.ATTACK){
			current = animHandler.melee[Rnd.randomInt(animHandler.melee.length)];
		} else
			current = animHandler.stand;

	}
	
	SearchingActor search;
	protected boolean searching;
	
	public void startSearching(GameObject target) {
		searching = true;
		search = new SearchingActor(target);
		GameState.getInstance().addActor(search);
	}
	

	protected void searchingCanceled() {
		searching = false;
		search.remove();
	}
	
	public class SearchingActor extends Actor{
		
		TextureRegion front,back;
		public float percent = 1;
		String text = "Searching...";
		int current = 0,max = 2000;
		GameObject object;
		
		public SearchingActor(GameObject target){
			float x = target.getX()-Cam.offsetX;//*;(1/GameState.zoom);
			float y = target.getY()-Cam.offsetY;//*(1/GameState.zoom);
			x+=Gdx.graphics.getWidth()/2;
			y+=Gdx.graphics.getHeight()/2;
			x-=50;
			setPosition(x,y);
			setSize(100,32);
			object = target;
			front = ResourceManager.getImage("hp_bar");
			back =  ResourceManager.getImage("bar_back");
		}
		
		@Override
		public void act(float delta){
			current += delta*1000f;
			System.out.println(current);
			percent = max/100f;
			percent = Math.max(0, current/percent/100f);
			
			if (current >= max)
				searchEnded();
		}

		private void searchEnded() {
			remove();
			((Searchable)object).searched(LiveObject.this);
			searching = false;
		}

		public void draw (SpriteBatch batch, float parentAlpha) {
			batch.draw(back, getX(), getY()+8, getWidth(), getHeight()-8);
			batch.draw(front, getX(), getY()+8, getWidth()*percent, getHeight()-8);
			
			C.UI.FONT.setColor(getColor());
			C.UI.FONT.drawMultiLine(batch, text, getX(), getY()+getHeight()/2+8, getWidth(), HAlignment.CENTER);
		}
	}
	
}
