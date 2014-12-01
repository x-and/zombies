package com.zombie.logic.object;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitterBox2DScalable;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.manager.ResourceManager;
import com.manager.ThreadPoolManager;
import com.physics.BodyFactory;
import com.physics.ListQueryCallback;
import com.physics.Physics;
import com.zombie.C;
import com.zombie.KeyInputListener;
import com.zombie.effect.ImageEffect;
import com.zombie.logic.Formulas;
import com.zombie.logic.GameWorld;
import com.zombie.logic.enums.ObjectType;
import com.zombie.logic.item.Explosive;
import com.zombie.logic.object.interfaces.Hitable;
import com.zombie.logic.object.live.Zombie;
import com.zombie.state.GameState;
import com.zombie.util.SoundUtils;
import com.zombie.util.Utils;


public class ExplosiveObject extends ItemObject {

	public int radius = 16;
	public float damage = 100;
	Condition condition;
	float accelY = -4f;
	public boolean blowOnContact = false;
	public float imageAngle = 0;
	private String explodeSound;
	String explodeParticle = "explode_0";
	public float detectorRadius;
	public boolean blow = false;
	
	public ExplosiveObject(float x, float y, Explosive it) {
		super(x, y, it);
		lifeTime = Long.MAX_VALUE;
		type = ObjectType.EXPLOSIVE;
		radius = it.radius;
		damage = it.damage;
		setVelocity(it.velocity);
		setCondition(it);
		blowOnContact = it.blowOnContact;
		explodeSound = it.explodeSound;
		detectorRadius = it.detectorRadius;
		explodeParticle = it.explodeParticle;
		pickupable = false;
	}
	
	public ExplosiveObject(float x, float y, Explosive it,LiveObject owner) {
		this(x, y, it);
		this.owner = owner;
	}

	private void setCondition(Explosive it) {
		if (it.condition.equalsIgnoreCase("timer")){
			condition = new TimerCondition(it.timer,it.beepOnTimer);
		} else if (it.condition.equalsIgnoreCase("radio")){
			condition = new RadioCondition();
		} else if (it.condition.equalsIgnoreCase("detector")){
			condition = new RoundDetectorCondition();
			renderGroup = C.GROUP_PRE_NORMAL;
		}
	}

	@Override
	public void update(float delta) {
		super.update(delta);
		setA(imageAngle);
		if (condition.isCompleted){
			remove();
//			createEffect();
			if (condition instanceof RadioCondition)
				GameState.keyListeners.remove(condition);
			return;
		}
		if (body == null)
			return;
		
		float x1 = getVelocity()*MathUtils.cos(MathUtils.degRad*getA());
		float y1 = getVelocity()*MathUtils.sin(MathUtils.degRad*getA());
		body.applyForceToCenter(x1, y1,true);
		condition.update(delta);
		
		if (blow)
			blow();
	}

	
//	private void createEffect() {
//		int count = 15+Rnd.nextInt(50);
//		for(int i = 0;i < count;i++){
//			float angle = Rnd.nextFloat()*360;
//			float x1 = 3*MathUtils.cos(MathUtils.degreesToRadians*angle);
//			float y1 = 3*MathUtils.sin(MathUtils.degreesToRadians*angle);
//			
//			float velocity = 0.2f+Rnd.nextFloat();
//			Bullet b = new Bullet(getX()+x1,getY()+y1,null,angle,true, new Color(1,0,0, 1), new Color(1,69/255,0,1));
//			b.damage = 0;
//			
//			b.setVelocity(velocity);
//			b.lifeTime = radius/2+Rnd.nextInt(100)*2;
//			GameWorld.addObject(b);
//		}
//	}

	public void blow() {
		condition.isCompleted = true;
		//TODO shaking camera when blow explosive in GameState.viewRect
//		Cam.shake(12,750);

		ParticleEffect em = new ParticleEffect(ResourceManager.getEmitter(explodeParticle));
		ParticleEmitterBox2DScalable emit = Utils.wrapEmitterToBox2d(em.getEmitters().get(0));
		em.getEmitters().clear();
		em.getEmitters().add(emit);
		em.reset();
		em.setPosition(getOldX()-getW()/2, getOldY()-getH()/2);
		GameState.addEmitter(em);
		
		SoundUtils.playSound(ResourceManager.getSound(explodeSound),pos);
		
		Physics.task(new Runnable(){

			@Override
			public void run() {
				ListQueryCallback list = new ListQueryCallback(){
					
					@Override
					public boolean reportFixture(Fixture fixture) {
						if (fixture.getBody().getUserData() == null)
							return true;
						GameObject obj = (GameObject) fixture.getBody().getUserData();
						if (obj instanceof PhysicObject)
							bodyes.add(fixture.getBody());
						return true;
					}
				};
				list.set(getX()-radius, getY()-radius, getX()+radius, getY()+radius);
				list.run();
				
				for(Body body : list.bodyes){
					RayCast raycast = new RayCast(body);
					raycast.run();
					if (!raycast.raycastOk)
						continue;
					GameObject obj = (GameObject) body.getUserData();
					float dmg = damage;
					dmg *= (radius-obj.dst(ExplosiveObject.this))/radius;
					if (obj instanceof LiveObject){
						Formulas.calcDamage(dmg,owner != null? owner : ExplosiveObject.this, (LiveObject) obj);
					} else if (obj instanceof Hitable){
						((Hitable) obj).hitted(dmg, owner != null? owner : ExplosiveObject.this);
					}
				}
				list.bodyes.clear();
			}});
		
		

		ImageEffect eff = new ImageEffect();
		eff.setBounds(getX()-(getW()+damage/16), getY()-(getH()+damage/16), getW()*2+damage/8,getH()*2+damage/8);
		eff.image = ResourceManager.getImage("hole0");
		GameWorld.addEffect(eff);
		
//		PointLight light = new PointLight(LightManager.handler, 32, Light.DefaultColor, 100, getX()+getW()/2, getY()+getH()/2);
//		light.setSoft(true);
//		LightEffect leff = new LightEffect(light);
//		leff.setFullLifeTime(100);
//		GameWorld.addEffect(leff);

	}


	@Override
	public void draw(SpriteBatch batch, ShapeRenderer shapeBatch) {
//		float temp = getA();
//		setA(imageAngle);
		super.draw(batch, shapeBatch);
//		setA(temp);
//		if (getVelocity() == 0 && condition instanceof TimerCondition){
//			g.setColor(Color.green);
//			g.drawString(Math.round(((TimerCondition)condition).timer/1000)+"",pos.x-getW()/2,pos.y-getH()-8);
//		}
//		
//		if (condition instanceof RadioCondition){
//			FontUtils.drawCenter(g.getFont(),"Press SPACE to detonate C4",
//					(int)(GameState.offsetX),(int)GameState.offsetY+Constants.SCREEN_HEIGHT-100,
//					Constants.SCREEN_WIDTH, Color.white);
//		}
//		
//		if (condition instanceof RoundDetectorCondition){
//			g.setColor(Color.white);
//			g.drawOval(pos.x-detectorRadius,pos.y-detectorRadius,detectorRadius*2,detectorRadius*2);
//		}
	}
	
	@Override
	public void createBody() {
		body = BodyFactory.createDynamicCircle(getX(), getY(),getW()/2,getA(), 2f, false, 10f);
		body.setUserData(this);
	}
	
	public abstract class Condition{
		Body body;
		boolean isCompleted = false;
		abstract void update(float delta);
	}
	
	public class TimerCondition extends Condition{
		public int timer = 1000;
		boolean beep = false;
		
		public TimerCondition(int t, boolean beepOnTimer) {
			beep = beepOnTimer;
			timer = t;
		}

		@Override
		void update(float delta) {
			timer-= delta*1000;
			if (timer <= 0)
				blow = true;
			}
	}
	
	public class RadioCondition extends Condition implements KeyInputListener{
		public RadioCondition() {
			GameState.keyListeners.add(this);
		}

		@Override
		void update(float delta) {
		}

		@Override
		public boolean onKeyUp(int key) {
			if (key == Keys.SPACE && !GameState.player.isDead()){
				blow = true;
				return true;
			}
			return false;
		}

	}
	
	public class RoundDetectorCondition extends Condition{
		
		Runnable blower = null;
		public RoundDetectorCondition() {
			//FIXME �������� ������ ��� ��������������� ������� �� ������, � �� ������ ������ ���������
//			CircleShape shape = new CircleShape();
//			shape.m_radius = detectorRadius;
//			Fixture f = body.createFixture(shape, 0f);
//			f.setSensor(true);
		}

		//FIXME make sensor fixture
		@Override
		void update(float delta) {
			if (blower != null)
				return;
			Physics.task(new Runnable(){

				@Override
				public void run() {
					boolean needblow = false;
					ListQueryCallback query = new ListQueryCallback();
					Physics.world.QueryAABB(query, (getX()-detectorRadius)*C.WORLD_TO_BOX,(getY()-detectorRadius)*C.WORLD_TO_BOX,
							(getX()+detectorRadius*2)*C.WORLD_TO_BOX,(getY()+detectorRadius*2)*C.WORLD_TO_BOX);
					for(Body b : query.bodyes){
						if (b.getUserData() instanceof Zombie){
							needblow = true;
							break;
						}	
					}
					query.bodyes.clear();
					query.bodyes = null;
					query = null;

					
					if (needblow)
						blower = (Runnable) ThreadPoolManager.schedule(new Runnable(){

							@Override
							public void run() {
								blow = true;
							}}, 1000);
				}});
		}
	}	
	
	class RayCast implements RayCastCallback, Runnable{
		boolean raycastOk = false;
		Body other;
		RayCast(Body b){
			other = b;
		}
		
		@Override
		public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal,
				float fraction) {
			if (fixture.getBody().getUserData() != null) 
				raycastOk = true;
			return 0;
		}
		@Override
		public void run() {
			if (body.getPosition().dst2(other.getPosition()) > 0.1f)
				Physics.world.rayCast(this, body.getPosition(), other.getPosition());
		}
	}
	
	@Override
	public boolean isDead(){
		return blow;
	}
}
