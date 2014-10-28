package com.zombie.logic.object.live;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.color;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import java.util.Arrays;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter.ScaledNumericValue;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.utils.Array;
import com.droidinteractive.box2dlight.ConeLight;
import com.droidinteractive.box2dlight.Light;
import com.manager.LightManager;
import com.manager.ResourceManager;
import com.manager.ThreadPoolManager;
import com.manager.TimeManager;
import com.physics.Physics;
import com.zombie.C;
import com.zombie.ZombieGame;
import com.zombie.effect.DroppedShellEffect;
import com.zombie.effect.LevelUpEffect;
import com.zombie.effect.LightEffect;
import com.zombie.effect.RocketTracerEffect;
import com.zombie.effect.TextEffect;
import com.zombie.logic.Faction;
import com.zombie.logic.Formulas;
import com.zombie.logic.GameWorld;
import com.zombie.logic.Reload;
import com.zombie.logic.enums.AnimType;
import com.zombie.logic.enums.ItemType;
import com.zombie.logic.enums.ObjectType;
import com.zombie.logic.item.Ammo;
import com.zombie.logic.item.Explosive;
import com.zombie.logic.item.Heal;
import com.zombie.logic.item.Item;
import com.zombie.logic.item.Weapon;
import com.zombie.logic.item.WeaponType;
import com.zombie.logic.object.Bullet;
import com.zombie.logic.object.ExplosiveObject;
import com.zombie.logic.object.GameObject;
import com.zombie.logic.object.ItemObject;
import com.zombie.logic.object.LiveObject;
import com.zombie.logic.object.interfaces.Hitable;
import com.zombie.logic.object.stat.PlayerStat;
import com.zombie.logic.object.vehicle.Vehicle;
import com.zombie.state.GameState;
import com.zombie.util.Rnd;
import com.zombie.util.SoundUtils;

public class Player extends LiveObject {

	public Array<Weapon> weapons = new Array<Weapon>();
	public Array<Item> items = new Array<Item>();
	public int[] slots = new int[3];
	public int itemPosition = -1;
	public boolean insideBuilding = false;
	public String buildingName = "";
	
	public float runTime;
	boolean isRunning = false;
	public boolean weaponArmed = false;

	int moveTick = 0;
	int calculatedMaxHp = 0;
	float calculatedVelocity = 0;
	
	Vector2 shotPosOneHanded, shotPosTwoHanded,shotPosRocket;
	Vector2 shotPosition = new Vector2();
	
	long lastUse = 0;
	public Light light;
	
	public Player(float x, float y){
		super(x,y,"player");
		faction = Faction.HUMAN;
		type = ObjectType.LIVE;
		setSize(28,28);
		pickup(Weapon.getWeaponById(0).clone());
		Arrays.fill(slots,-1);
		super.setVelocity(0.7f);
		calcVelocity();
		calcDefence();
		calcHealth();
		shotPosOneHanded = new Vector2(32,8);
		shotPosTwoHanded = new Vector2(36,4);
		shotPosRocket = new Vector2(36,8);
		runTime = getMaxRunTime();
		animHandler.loadAdditionalAnimations(image);
	}

	public void upAgility(){
		if (getStat().skillPoints < 1)
			return;
		getStat().agility+=1;
		setVelocity(super.getVelocity() + getStat().agility/50f*1.1f);
		getStat().evasion=getStat().agility*0.1f*1.1f; // 1f = 100%  0.1 = 10% 0.01 = 1% 
		if (getStat().evasion > 0.3f)
			getStat().evasion = 0.3f;
		getStat().skillPoints--;
	}
	
	void calcVelocity(){
		setVelocity(super.getVelocity() + getStat().agility/50f*1.1f);
	}
	
	void calcDefence(){
		getStat().defence=getStat().strength/2-5;
	}
	
	void calcHealth(){
		setMaxHp((int) (super.getMaxHp() + (getStat().endurance*getStat().endurance)/6) - 10);
	}
	
	public void upStrength(){
		if (getStat().skillPoints < 1)
			return;
		getStat().strength+=1;
		getStat().defence=getStat().strength/2-5;
		getStat().skillPoints--;
	}	
	
	public void upEndurance(){
		if (getStat().skillPoints < 1)
			return;
		getStat().endurance+=1;
		setMaxHp((int) (super.getMaxHp() + (getStat().endurance*getStat().endurance)/6) - 10);
		getStat().skillPoints--;
	}		

	public int getMaxRunTime(){
		return 10;
	}	
	
	public int getMeleeAttack(){
		if(getWeapon() == null)
			return 2 + getStat().strength/2;
		else
			return getWeapon().damage + getStat().strength/3;
	}

	// in msec
	public int getMeleeAttackSpeed(){
		return getWeapon() == null ? 500 : getWeapon().hitTime;// + 100/getStat().agility*getStat().strength*300;
	}
	
	public int getMaxHp(){
		if (calculatedMaxHp == 0)
			return super.getMaxHp();
		return calculatedMaxHp;
	}

	public void setMaxHp(int newHp){
		calculatedMaxHp = newHp;
	}
	
	public float getVelocity(){
		if (calculatedVelocity == 0)
			if (isRunning)
				return super.getVelocity()*1.5f;
			else
				return super.getVelocity();
		if (isRunning)
			return calculatedVelocity*1.5f;
		return calculatedVelocity;
	}

	public void setVelocity(float velocity){
		calculatedVelocity = velocity;
	}
	
	@Override
	public void update(float delta) {
		super.update(delta);
		if (getAnimation() == AnimType.ATTACK)
			if (current != null && current.isAnimationFinished((TimeManager.getLongTime()-lastHit)))
				setAnimation(AnimType.STAND);
		
		if (searching && getAnimation() != AnimType.STAND)
			searchingCanceled();
		if (getAnimation() == AnimType.DIE){
			if (current.isAnimationFinished(TimeManager.getLongTime()-deathTime)){
				addToBackground();	
				remove();
				light.setActive(false);
//				dieAnim = null;
			}
			return;
		}
		
		if (getAnimation() != AnimType.ATTACK)
			setAnimation(AnimType.STAND);

		if (ZombieGame.input.isRunning && !isRunning)
			startRun();
		if (!ZombieGame.input.isRunning && isRunning)
			stopRun();
		
		if (vehicle != null){
			vehicle.control();
			return;
		}
		
		if (ZombieGame.input.playerUp)
			body.applyForceToCenter(0, getVelocity(), true);
		else if (ZombieGame.input.playerDown)
			body.applyForceToCenter(0, -getVelocity(), true);
		if (ZombieGame.input.playerLeft)
			body.applyForceToCenter(-getVelocity(),0, true);
		else if (ZombieGame.input.playerRight)
			body.applyForceToCenter(getVelocity(),0, true);
		
		
		if (getAnimation() != AnimType.ATTACK && (ZombieGame.input.playerUp||ZombieGame.input.playerDown||ZombieGame.input.playerLeft||ZombieGame.input.playerRight)){
			setAnimation(AnimType.MOVE);
			stepSound();
		}
		if (!isRunning)
			runTime = Math.min(runTime+delta/2, getMaxRunTime());

		if (isRunning){
			runTime-=delta*2;			
			if (runTime <= 0)
				stopRun();
		}
			
		moveTick+=delta;
		if (moveTick >= 1000)
			moveTick = 0;
		
		setA(ZombieGame.input.playerAngle);
		
		light.setPosition(getX(),getY());
		light.setDirection(getA());

		if (ZombieGame.input.hit)
			hit();		
	}

	protected int stepTime(){
		return isRunning? 320 : 400;
	}
	
	private void stopRun() {
		if (runTime <= 0)
			runTime = 0;
		isRunning = false;
	}

	private void startRun() {
		if (runTime < getMaxRunTime()/5f)
			return;
		isRunning = true;
	}

	public int getExp() {
		return getStat().exp;
	}

	public void setExp(int exp) {
		getStat().exp = exp;
		checkForLevel();
	}

	private void checkForLevel() {
		if (getStat().exp > needExp())
			levelUp();
	}
	
	public int needExp(){
		return needExp(getLevel());
	}
	
	public int needExp(int level){
		if (level == 0)
			return 0;
		return 10*level + level*level*20;
	}

	private void levelUp() {
		getStat().level++;
		getStat().skillPoints+= 2 + getLevel()/2;
		LevelUpEffect eff = new LevelUpEffect();
		eff.setFullLifeTime(3000);
		GameWorld.addEffect(eff);
		if (getLevel() == 1){
			TextEffect eff2 = new TextEffect();
			eff2.text = "Level up! To raise stats press C";
			eff2.position = new Vector2(300,260);
			GameWorld.addEffect(eff2);
		}
		GameState.getInstance().ui.levelGained();
	}

	/**���� ������ �������� ��� - ��������� �� ����� ���������� ����� � �� ��������� �� ���� � �������*/
	private void hit() {
		if (!weaponArmed || getWeapon().weaponType == WeaponType.MELEE)
			closeAttack();
		else
			rangedAttack();
	}
	
	private void closeAttack() {
		int hitTime = getMeleeAttackSpeed();
		if (TimeManager.getLongTime() < lastHit+hitTime)
			return;
		if (getAnimation() == AnimType.MOVE)
			return;
		if (getWeapon() != null)
			SoundUtils.playSound(ResourceManager.getSound(getWeapon().shootSound),pos.current,this);

		setAnimation(AnimType.ATTACK);

//		else
//			Utils.playSound(ResourceManager.getSound("punch");
		
		lastHit = TimeManager.getLongTime();			
		final float x1 = 36*MathUtils.cos(getA()*MathUtils.degRad);
		final float y1 = 36*MathUtils.sin(getA()*MathUtils.degRad);
		
		result = 0;
		Physics.task(new Runnable(){

			@Override
			public void run() {
				Physics.world.rayCast(new RayCastCallback(){
				@Override
				public float reportRayFixture(Fixture fixture, Vector2 point,
						Vector2 normal, float fraction) {
					if (fixture.isSensor()) return 1;
					if (fixture.getBody().getUserData() instanceof LiveObject){
						Formulas.calcDamage(getMeleeAttack(), Player.this, (LiveObject) fixture.getBody().getUserData());
						result = 1;
					} else if (fixture.getBody().getUserData() instanceof Hitable){
						((Hitable) fixture.getBody().getUserData()).hitted(getMeleeAttack(), Player.this);
//						push((StaticObject) fixture.getBody().getUserData(),getMeleeAttack());
						result = 2;
					}
						
					if (getWeapon() != null){
						switch(result){
							case 0:
								SoundUtils.playSound(ResourceManager.getSound(getWeapon().getMissSound()),pos.current,Player.this);
								break;
							case 1:
								SoundUtils.playSound(ResourceManager.getSound(getWeapon().getShootSound()),pos.current,Player.this);
								break;
							case 2:
								//FIXME sounds for statics
								SoundUtils.playSound(ResourceManager.getSound(getWeapon().getMissSound()),pos.current,Player.this);
								break;
						}
					}
					
					return 0;
				}}, body.getPosition(),  body.getPosition().cpy().add(x1*C.WORLD_TO_BOX, y1*C.WORLD_TO_BOX));
			}});

	}
	
	int result = 0;

	private void rangedAttack() {
		if (TimeManager.getLongTime() < lastHit+getWeapon().hitTime || getWeapon().ammo <=0)
			return;
		if (getWeapon().weaponType == WeaponType.ROCKET && getAnimation() == AnimType.MOVE)
			return;
		if (reload != null)
			return;
		if (getWeapon().ammo > 0)
			getWeapon().ammo--;
		if (getWeapon().ammo == 0 && getWeapon().totalAmmo != 0)
			reload();
		SoundUtils.playSound(ResourceManager.getSound(getWeapon().shootSound),pos.current,this);	
		getStat().shots++;
		lastHit = TimeManager.getLongTime();	
		GameState.getInstance().ui.weaponChanged(getWeapon());
		
		shotPosition.set(getWeapon().twoHanded? shotPosTwoHanded : shotPosOneHanded);
		float x1 = shotPosition.x*MathUtils.cos(MathUtils.degRad*(getA()));
		float y1 = shotPosition.x*MathUtils.sin(MathUtils.degRad*(getA()));
		float x2 = shotPosition.y*MathUtils.cos(MathUtils.degRad*(getA()-90));
		float y2 = shotPosition.y*MathUtils.sin(MathUtils.degRad*(getA()-90));

		shotPosition.set(x1+x2,y1+y2);
		shotPosition.x += getX();
		shotPosition.y += getY();

		if (getWeapon().weaponType == WeaponType.ROCKET){
			ExplosiveObject obj = new ExplosiveObject(shotPosition.x,shotPosition.y,(Explosive)Item.getItemById(getWeapon().bullet.explosiveId),this);
			obj.setA(getA());
			obj.imageAngle = getA();
			GameWorld.addObject(obj);
			
			ParticleEffect em = new ParticleEffect(ResourceManager.getEmitter(getWeapon().particle_smoke));
			em.reset();
			ScaledNumericValue val = em.getEmitters().get(0).getAngle();
			val.setHigh(val.getHighMin()+getA()+90, val.getHighMax()+getA()+90);
			em.setPosition(shotPosition.x,shotPosition.y);
			GameState.addEmitter(em);
			
			em = new ParticleEffect(ResourceManager.getEmitter(getWeapon().particle_shot));
			ParticleEmitter emit = em.getEmitters().get(0);

			emit.getAngle().setHigh(emit.getAngle().getHighMin()+getA()-90,emit.getAngle().getHighMax()+getA()-90);
			emit.getAngle().setLow(emit.getAngle().getLowMin()+getA()-90,emit.getAngle().getLowMax()+getA()-90);
			em.reset();
			em.setPosition(shotPosition.x,shotPosition.y);
			GameState.addEmitter(em);
			
			RocketTracerEffect eff = new RocketTracerEffect(new ParticleEffect(ResourceManager.getEmitter("smoke_2")),obj);
			GameWorld.addEffect(eff);
			return;
		}
		
		ParticleEffect em = new ParticleEffect(ResourceManager.getEmitter(getWeapon().particle_smoke));
		em.reset();
		em.setPosition(shotPosition.x,shotPosition.y);
		GameState.addEmitter(em);
		
		int count = 1;
		if (getWeapon().weaponType == WeaponType.SHOTGUN)
			count = 5+Rnd.nextInt(2);
		
		for(int i = 0; i < count;i++){
			float rndAngle = (-getWeapon().rndAngle/2+Rnd.nextInt((int) (getWeapon().rndAngle+1)));
			if (isRunning)
				rndAngle*=3;
			Bullet b = new Bullet(shotPosition.x,shotPosition.y,this,getA()+rndAngle);
			b.setParams(getWeapon());
			GameWorld.addObject(b);
			em = new ParticleEffect(ResourceManager.getEmitter(getWeapon().particle_shot));
			ParticleEmitter emit = em.getEmitters().get(0);

			emit.getAngle().setHigh(getA()+rndAngle,getA()+rndAngle);
			emit.getAngle().setLow(getA()+rndAngle,getA()+rndAngle);
			em.reset();
			em.setPosition(shotPosition.x,shotPosition.y);
			GameState.addEmitter(em);
		}
		DroppedShellEffect shellEffect = new DroppedShellEffect();
		shellEffect.angle = 360*Rnd.nextFloat();
		shellEffect.position.set(getX()+Rnd.randomInt(-4, 8),getY()+Rnd.randomInt(-4, 8));
		shellEffect.setFullLifeTime(1000L);
		GameWorld.addEffect(shellEffect);
		
		Physics.task(new Runnable(){

			@Override
			public void run() {
				Light light = new ConeLight(LightManager.getHandler(), 32, Light.DefaultColor, 50, getX(), getY(), getA(), 30);
				light.setSoftnessLength(3);
				light.setSoft(true);
				LightEffect eff = new LightEffect(light);
				eff.setFullLifeTime(100);
				GameWorld.addEffect(eff);
			}});
	}

	public void reload() {
		if (getWeapon() == null)
			return;
		if (getWeapon().weaponType == WeaponType.MELEE)
			return;
		if (isDead())
			return;
		if (getWeapon().ammo == getWeapon().maxAmmo)
			return;
		if (reload != null && !reload.isDone())
			return;
		if (getWeapon().totalAmmo == 0)
			return;
		reload = ThreadPoolManager.schedule(new Reload(this), getWeapon().reloadTime);
		SoundUtils.playSound(ResourceManager.getSound(getWeapon().clipoutSound),pos.current);
	}
	
	@Override
	protected void onHit(GameObject damager) {
		playSound();
		GameState.getInstance().ui.hpBar.addAction(sequence(color(Color.RED,0.1f),color(Color.WHITE,0.1f),color(Color.RED,0.1f),color(Color.WHITE,0.1f)));
	}

	public boolean containsWeapon(int id){
		for(Weapon w : weapons)
			if (w.id == id)
				return true;
		return false;
	}
	
	public Weapon getWeapon(int id){
		for(Weapon w : weapons)
			if (w.id == id)
				return w;
		return null;
	}
	
	public void pickup(ItemObject item) {
		pickup(item.itemId);
		item.lifeTime = 0;
	}
	
	private void pickup(int itemId) {
		pickup(Item.getItemById(itemId));
	}

	public void pickup(Item item) {
		SoundUtils.playSound(ResourceManager.getSound("item_pickup"), getPos());
		if (item.type == ItemType.WEAPON){
			if (!containsWeapon(item.id)){
				weapons.add(Weapon.getWeaponById(item.id).clone());
				for(int i = 0; i < items.size;i++){
					Item it = items.get(i);
					if (it instanceof Ammo){
						if (((Ammo) it).weaponId == item.id){
							getWeapon(item.id).totalAmmo += it.count*((Ammo)it).ammo;
							items.removeValue(it, true);
//							items.remove(it);
						}
					}
				}
			} else
				getWeapon(item.id).totalAmmo += 
					Weapon.getWeaponById(item.id).maxAmmo;
			return;
		}
		if (item.type == ItemType.AMMO){
			Ammo ammo = (Ammo) Item.items.get(item.id);
			if (!containsWeapon(ammo.weaponId)){
				if (containsItem(ammo.id))
					getItemById(ammo.id).count++;
				else
					items.add(ammo.clone());
			} else {
				getWeapon(ammo.weaponId).totalAmmo+=ammo.ammo;
				GameState.getInstance().ui.weaponChanged(getWeapon());
			}
		} else if (item.type == ItemType.HEAL){
			Heal heal =  (Heal) Item.items.get(item.id);
			if (containsItem(heal.id))
				getItemById(heal.id).count++;
			else
				items.add(heal.clone());
		} else if  (item.type == ItemType.EXPLOSIVE){
			Explosive exp = (Explosive) Item.items.get(item.id);
			if (containsItem(exp.id))
				getItemById(exp.id).count++;
			else
				items.add(exp.clone());
		}
		
		if (item.isUseable()) {
			int slot  = -1;
			for (int i = 0;i < slots.length;i++){
				if (slots[i] == item.id){
					slot = -1;
					break;
				}
				if (slots[i] == -1){
					slot = i;
					break;
				}	
			}
			if (slot != -1){
				slots[slot] = item.id;
				GameState.getInstance().ui.slotsChanged();
			}
		}
	}	
	
	public Item getItemById(int id) {
		for( Item it : items)
			if (it.id == id)
				return it;
		return null;
	}

	public boolean containsItem(int id) {
		for(Item it : items)
			if (it.id == id)
				return true;
		for(Weapon w : weapons)
			if (w.id == id)	
				return true;
		return false;
	}

	public void use(){
		if (itemPosition == -1)
			return;
		
		Item it = items.get(itemPosition);
		use(it);
	}
	
	public void use(int itemId){
		use(getItemById(itemId));
	}

	
	private void use(Item it) {
		if (isDead())
			return;
		if (it == null)
			return;
		if (lastUse+1000 > TimeManager.getLongTime())
			return;
		lastUse = TimeManager.getLongTime();

		if (it.type == ItemType.HEAL){
			if (getHp() == getMaxHp())
				return;
			setHp(getHp()+((Heal)it).heal);
			if (getHp() > getMaxHp())
				setHp(getMaxHp());
			it.count--;
		} else if (it.type == ItemType.EXPLOSIVE){
			ExplosiveObject obj = new ExplosiveObject(getX(),getY(),(Explosive) it, this);
			obj.setA(getA());
			GameWorld.addObject(obj);
			obj.setTransform(getX(),getY(), getA());
			it.count--;
		}
		
		if (it.count <= 0){
			items.removeValue(it, false);
//			items.remove(it);
			itemPosition = -1;
		}
		if (items.size < itemPosition-1){
			itemPosition = -1;
		}
		
		if (it.count <= 0) {
			for (int i = 0;i < slots.length;i++){
				if (slots[i] == it.id){
					slots[i] = -1;
				}	
			}
			GameState.getInstance().ui.slotsChanged();

		}
	}

	public void setWeapon(Weapon weapon) {
		super.setWeapon(weapon);
		GameState.getInstance().ui.weaponChanged(weapon);
	}
	
	public void arm(){
		weaponArmed = true;
		if (getPreviousWeapon() != null)
			setWeapon(getPreviousWeapon());
		else
			setWeapon(weapons.get(0));
	}
	
	public void disarm(){
		weaponArmed = false;
		setPreviousWeapon(getWeapon());
		setWeapon(null);
	}
	
	public void setStat(PlayerStat s){
		stats = s;
		stats.owner = this;
	}
	
	@Override
	public PlayerStat getStat(){
		if (stats == null)
			stats = new PlayerStat(this);
		return (PlayerStat) stats;
	}

	public void vehicleEntered(Vehicle v,boolean isDriver){
		super.vehicleEntered(v, isDriver);
		light.setActive(false);
	}

	

	
	protected void changeAnimation() {
		if (getWeapon() == null || getWeapon().weaponType == WeaponType.MELEE)
			super.changeAnimation();
		else {
			if (getAnimation() == AnimType.STAND || getAnimation() == AnimType.ATTACK){
				if (getWeapon().weaponType == WeaponType.ROCKET)
					current = animHandler.animations.get("rocket");
				else if (getWeapon().twoHanded)
					current = animHandler.animations.get("twohand");
				else 
					current = animHandler.animations.get("onehand");
			} else if (getAnimation() == AnimType.MOVE)
				if (getWeapon().weaponType == WeaponType.ROCKET)
					super.changeAnimation();
				else if (getWeapon().twoHanded)
					current = animHandler.animations.get("move_twohand");
				else
					current = animHandler.animations.get("move_onehand");
			else super.changeAnimation();
		}
//		
//		if (getAnimation() == AnimType.STAND || getAnimation() == AnimType.ATTACK){
//			if (getWeapon() != null && getWeapon().weaponType == WeaponType.STANDART){
//				if (getWeapon().twoHanded)
//					current = animHandler.animations.get("twohand");
//				else
//					current = animHandler.animations.get("onehand");
//			} else
//				super.changeAnimation();
//		} else
//			super.changeAnimation();
	}

	
}
