package com.zombie.logic.object;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.manager.ResourceManager;
import com.path.Grid;
import com.physics.BodyFactory;
import com.zombie.C;
import com.zombie.effect.MovingImageEffect;
import com.zombie.effect.TextEffect;
import com.zombie.logic.GameWorld;
import com.zombie.logic.enums.ObjectType;
import com.zombie.logic.item.Item;
import com.zombie.logic.object.interfaces.Hitable;
import com.zombie.logic.object.interfaces.Searchable;
import com.zombie.logic.object.live.Player;
import com.zombie.logic.object.stat.BasicStat;
import com.zombie.util.Rnd;
import com.zombie.util.SoundUtils;

public class StaticObject extends PhysicObject implements Hitable,Searchable{

	boolean empty = false;
	
	

	public StaticObject(float x, float y) {
		super(x, y);
		type = ObjectType.STATIC;
		stats = new BasicStat(this);
	}

	@Override
	public void update(float delta) {
		super.update(delta);
		if (isDead())
			return;
		if (getStat().hp <= 0){
			remove();
			return;
		}
		pos.setAngle(MathUtils.radDeg*body.getAngle());

		if (body.isAwake())
			return;
		checkGrid();
//		if (TimeManager.getLongTime() - lastMoveTimer < 1000){
//			checkGrid();
//			lastMoveTimer-=1000;
//		}
	}
	
	// TODO better che
	protected void checkGrid(){
		Grid grid = GameWorld.level.grid;
		int oldx = 0,oldy = 0,x = 0,y = 0;
		
		oldx = (int) (pos.getOldX()/C.TILESIZE);
		oldy = (int) (pos.getOldY()/C.TILESIZE);
		x = (int) (getX()/C.TILESIZE);
		y = (int) (getY()/C.TILESIZE);
		grid.grid[oldx][oldy].pass = true;
		grid.grid[x][y].pass = removed;
	}

	protected void toParts(float velocity,TextureRegion image){
		for(int i = 0;i<8;i++){
			int width = Rnd.randomInt(4,(int) (image.getRegionWidth()/4));
			int height = Rnd.randomInt(4,(int) (image.getRegionHeight()/4));
			int x = Rnd.randomInt(2, (int) (image.getRegionWidth()-width-2));
			int y = Rnd.randomInt(2, (int) (image.getRegionHeight()-height-2));
			MovingImageEffect eff = new MovingImageEffect();
			eff.setFullLifeTime(60000);
			eff.velocity = velocity + 0.1f*Rnd.nextFloat();
			eff.aVelocity = 1.4f + 0.4f*Rnd.nextFloat();
			if (Rnd.nextBoolean())
				eff.aVelocity = -eff.aVelocity;
			eff.stopTime = 50+ Rnd.nextInt(150);
			float x1 = getX() + Rnd.randomInt(-6,12);
			float y1 = getY() + Rnd.randomInt(-6,12);
			TextureRegion region = new TextureRegion(image.getTexture());
			region.setRegion(image,x,y, width,height);
			eff.image =	region;
			eff.setBounds(x1, y1, width, height);
			eff.angle =	Rnd.nextFloat()*360f;
			eff.angle2 = eff.angle;
			GameWorld.addEffect(eff);
		}
	}

	public void setLife() {
		getStat().setMaxHp((int) (getW()*getH()/5));
	}

	@Override
	public void hitted(float value, GameObject damager) {
		getStat().hp-= value;
		SoundUtils.playSound(getMaterial().getSound(),pos);
		damager.push(this,(int) value);
		if (getStat().hp <= 0)
			doDie(damager);
	}

	@Override
	public void doDie(GameObject killer) {
		toParts(0.04f,ResourceManager.getImage(image));
		doDrop();
		SoundUtils.playSound(ResourceManager.getSound(getMaterial().dieSound),pos);
		Grid grid = GameWorld.level.grid;
		int x = (int) (getX()/C.TILESIZE);
		int y = (int) (getY()/C.TILESIZE);
		grid.grid[x][y].pass = true;
	}

	@Override
	public int getHp() {
		return getStat().hp;
	}

	@Override
	public void searched(LiveObject searcher) {
		if (drop == null){
			TextEffect eff = new TextEffect("Empty");
			eff.setX(getX()-16);
			eff.setY(getY());
			eff.setFullLifeTime(3000);
			eff.allTextShownTime=0;
			eff.symbolShowTime = 0;
			GameWorld.addEffect(eff);
		} else {
			for(Item d : drop){
				((Player) searcher).pickup(d);
			}
			drop.clear();
		}
	}

	@Override
	public void createBody() {
		if (geom.equalsIgnoreCase("box")){
			if (physType.equalsIgnoreCase("dynamic"))
				body = BodyFactory.createDynamicBox(getX(),getY(),getW(),getH(),getA(),density, false, 0.5f,10f);
			else
				body = BodyFactory.createStaticBox(getX(),getY(),getW(),getH(),getA());
		} else if  (geom.equalsIgnoreCase("circle")){
			float radius = getW()/2;
			if (physType.equalsIgnoreCase("dynamic"))
				body = BodyFactory.createDynamicCircle(getX(),getY(),radius,getA(),density,false, 10f);
		}
		body.setUserData(this);
	}
	String geom, physType;
	float density;
	
	public void setPhysic(String geom, String type, float density) {
		this.geom = geom;
		physType = type;
		
	}
}
