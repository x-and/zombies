package com.zombie.logic.quest;

import com.badlogic.gdx.math.Rectangle;
import com.zombie.C;
import com.zombie.logic.GameWorld;
import com.zombie.logic.enums.ObjectType;
import com.zombie.logic.item.Item;
import com.zombie.logic.object.GameObject;
import com.zombie.logic.object.LiveObject;
import com.zombie.logic.zone.Zone;

public class FirstQuest extends Quest {

	int killedCount = 0;
	int needKills = 3;
	Zone z;
	
	public FirstQuest(int questId,String name) {
		super(questId,name);
	}

	@Override
	public void init() {
		showText(null,name,"You need to kill 3 crawling zombies.");
//		z = new Zone(0, 0);
//		z.setEllipse(new Ellipse(57.5f*C.TILESIZE,(ZombieWorld.level.getHeightInTiles()-33.5f)*C.TILESIZE,64,64));
//		ZombieWorld.addObject(z);
		
		z = new Zone(0, 0);
		z.setRect(new Rectangle(57.5f*C.TILESIZE,(GameWorld.level.getHeightInTiles()-29.5f)*C.TILESIZE,10*C.TILESIZE,64));
		z.registerQuest(this);
		GameWorld.addObject(z);
	}

	@Override
	public void finish() {
		//remove quest from all registered objects
		unregister();
		// TODO add reward
		// TODO add text 
		showText(null,name,"You killed 3 crawling zombies. Your reward: 1000 credits.");
//		System.out.println("finish() " + this);
		registered.clear();
		GameWorld.removeObject(z);
		z = null;
	}

	@Override
	public void stateChanged() {
		stateId++;
		if (stateId == 1)
			showText(null,name,"Back to the guns store for reward.");
		if (stateId == 2)
			finish();
	}

	@Override
	public void onKill(GameObject killed) {
		if (killed.type == ObjectType.LIVE){
			if (killed.hasQuest(id))
				killedCount++;
			registered.removeValue(killed, true);
			if (killedCount>= needKills)
				stateChanged();
		}
//		System.out.println("onKill(GameObject killed " + killed);

	}

	@Override
	public void onHit(int damage, GameObject hitted) {

	}

	@Override
	public void onPickup(Item item) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onZoneEnter(Zone zone) {
		if (stateId == 1)
			stateChanged();
	}

	@Override
	public void onZoneExited(Zone zone) {
	}

	@Override
	public void onSpawn(LiveObject spawned) {
		if (spawned.getStat().mobDefId == 3){
			spawned.registerQuest(this);
			registered.add(spawned);
		}
	}

	@Override
	public boolean isFinished() {
		return stateId == 1;
	}

}
