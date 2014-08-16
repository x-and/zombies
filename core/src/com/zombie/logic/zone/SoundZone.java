package com.zombie.logic.zone;

import com.badlogic.gdx.audio.Sound;
import com.manager.ResourceManager;
import com.zombie.C.APP;
import com.zombie.util.SoundUtils;

public class SoundZone extends Zone {

	long soundId;
	private String soundID;

	Sound sound;
	float volume = 0f;
	
	public SoundZone(float x, float y) {
		super(x, y);
	}
	
	public SoundZone(String soundName) {
		this(0,0);
		name = soundName;
		setSoundID(soundName);
	}

	@Override
	public void update(float delta){
		super.update(delta);
		if (!containsPlayer && volume > 0){
			volume -= 0.01f;
			if (sound != null)
				sound.setVolume(soundId, APP.SND_VOL*volume*.5f);
		}
		if (containsPlayer && volume < 1){
			volume += 0.01f;
			if (sound != null)
				sound.setVolume(soundId, APP.SND_VOL*volume*.5f);
		}
	}
	

	void playerEntered(){
		super.playerEntered();
		soundId = SoundUtils.loopSound(ResourceManager.getSound(soundID),null);
		containsPlayer = true;
	}
	
	void playerExited(){
		super.playerExited();
		volume = 1;
		containsPlayer = false;	
	}

	public String getSoundID() {
		return soundID;
	}

	public void setSoundID(String soundId) {
		this.soundID = soundId;
		sound = ResourceManager.getSound(soundID).sound;


	}
}
