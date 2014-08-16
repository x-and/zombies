package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.utils.Array;

public class Animation2D extends Animation {

	public Animation2D(float duration,Array<? extends TextureRegion> frames) {
		this(duration, frames, Animation2D.NORMAL);
	}
	
	public Animation2D (float duration, Array<? extends TextureRegion> frames, int playType) {
		super(duration, frames,playType);
	}
	
	public Animation2D (float duration, TextureRegion... frames) {
		super(duration,frames);
	}

	public Animation2D copy(float f) {
		Animation2D copy = new Animation2D(f, keyFrames);
		return copy;
	}
	
	public Animation2D copy(){
		Animation2D copy = new Animation2D(frameDuration, keyFrames);
		return copy;
	}
	
	public int frameCount(){
		return keyFrames.length;
	}
	
	public TextureRegion getFrame(int index){
		return keyFrames[index];
	}


}
