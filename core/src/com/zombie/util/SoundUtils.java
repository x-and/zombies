package com.zombie.util;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.esotericsoftware.minlog.Log;
import com.physics.Physics;
import com.physics.RunnableListQueryCallback;
import com.zombie.C;
import com.zombie.C.APP;
import com.zombie.SoundInfo;
import com.zombie.logic.Position;
import com.zombie.logic.ai.AIState;
import com.zombie.logic.ai.event.SoundEvent;
import com.zombie.logic.object.GameObject;
import com.zombie.logic.object.LiveObject;

public class SoundUtils {

	public static long playSound(SoundInfo sound,Position position){
		return playSound(sound,1,1,position.current,null);
	}	

	public static long playSound(SoundInfo sound, Vector2 position) {
		return playSound(sound,1,1,position,null);
	}
	
	public static long playSound(SoundInfo sound,Vector2 position,GameObject owner){
		return playSound(sound,1,1,position,owner);
	}
	
	public static long playSound(SoundInfo sound,float volume,float pitch,Vector2 position,GameObject owner){
		return playSound(sound,volume,pitch,0, false,position,owner);
	}
	
	public static long playSound(SoundInfo sound,float volume,float pitch,float pan,boolean loop,Vector2 position,GameObject owner){
		if (!APP.SND_ENABLED)
			return -1;
		if (sound == null)
			return -1;
		if (position != null) {
			if (sound.radius != 0){
				SoundQuery query = new SoundQuery();
				query.position = position;
				query.sound = sound;
				query.owner = owner;
				query.set(position.x-sound.radius,  position.y-sound.radius,  position.x+sound.radius,  position.y+sound.radius);
				Physics.task(query);
			}
			
			if (sound.spaceSystem){
				float camX = Cam.offsetX;
				float camY = Cam.offsetY;
				if (Math.abs(camX - position.x) > Cam.view.width/4){
					volume= MathUtils.clamp(C.MAP_WIDTH/2/Math.abs(position.dst(camX, camY))/C.TILESIZE*2,0,1f);
				}
				if (Math.abs(camX - position.x) < Cam.view.width/4)
					pan = 0;
				else {
					if (camX - position.x < 0)
						pan = MathUtils.clamp(1-(C.MAP_WIDTH/Math.abs(position.x - camX)/C.TILESIZE), 0, 1f);
					else
						pan = -MathUtils.clamp(1-(C.MAP_WIDTH/Math.abs(position.x - camX)/C.TILESIZE), 0, 1f);
				}
			}
			Log.debug("SoundUtils","  owner : " + (owner == null ? "null" : owner.toString()) + " position : " +position + " volume : " + volume + "  pan : " + pan);
		}			

		if (loop)
			return sound.sound.loop(APP.SND_VOL*volume,pitch,pan);
		return sound.sound.play(APP.SND_VOL*volume,pitch,pan);
	}	

	public static long loopSound(SoundInfo sound,Vector2 position){
		return playSound(sound,1,1,0, true,position,null);
	}
	
	public static class SoundQuery extends RunnableListQueryCallback{

		SoundInfo sound;
		Vector2 position;
		GameObject owner;
		
		@Override
		public void run() {
			SoundEvent event = new SoundEvent();
			event.radius = sound.radius;
			event.position = position;
			event.owner = owner;
			LiveObject own = null;
			if (owner instanceof LiveObject)
				own = (LiveObject) owner;
			for(Body b : bodyes){
				if (b.getUserData() == null)
					continue;
				if (b.getUserData() instanceof LiveObject){
					LiveObject live = (LiveObject) b.getUserData();
					if (own != null && own.faction == live.faction)
						continue;

					if (live.getAI().state == AIState.NO_TARGET){
						live.getAI().handleEvent(event);
					}
				}
			}
		}
	}

	

}
