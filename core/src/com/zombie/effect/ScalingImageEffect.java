package com.zombie.effect;

public class ScalingImageEffect extends ImageEffect {

	public float minScale = 0.1f;
	public float maxScale = 1f;
	public float scaleVelocity = 0.03f;
	
	@Override
	public void update(int delta) {
		super.update(delta);
		if (scaleVelocity != 0){
			scale-= scaleVelocity/delta;
			if (scale < minScale)
				scale = minScale;
			if (scale > maxScale)
				scale = maxScale;
		}
	}
	
//	@Override
//	public void draw(SpriteBatch batch, ShapeRenderer shapeBatch) {
//		Color old = batch.getColor();
//		batch.setColor(1, 1, 1, actualOpacity);
//		batch.draw(image, getX(), getY(), origin.x, origin.y, getW(),getH(), scale, scale, angle);
//		batch.setColor(old);
//	}


}
