package com.zombie.util;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.physics.Physics;
import com.physics.RunnableListQueryCallback;
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
		if (sound.radius != 0){
			if (position != null){
				SoundQuery query = new SoundQuery();
				query.position = position;
				query.sound = sound;
				query.owner = owner;
				query.set(position.x-sound.radius,  position.y-sound.radius,  position.x+sound.radius,  position.y+sound.radius);
				Physics.task(query);
			}
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
