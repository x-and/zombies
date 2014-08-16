package com.zombie.util.state.transition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.zombie.util.state.State;

public class FadeInTransition implements Transition {
	private Color color;
	private int fadeTime = 1000;
	ShapeRenderer render;
	
	public FadeInTransition() {
		this(Color.BLACK, 1000);
	}
	
	public FadeInTransition(Color color) {
		this(color, 1000);
	}
	
	public FadeInTransition(Color color, int fadeTime) {
		this.color = new Color(color);
		this.color.a = 1;
		this.fadeTime = fadeTime;
		render = new ShapeRenderer();
	}
	
	public boolean isComplete() {
		return (color.a <= 0);
	}

	public void postRender() {
		Gdx.gl.glEnable(GL10.GL_BLEND);
	    Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		render.begin(ShapeType.Filled);
		render.setColor(color.r, color.g, color.b, color.a);
		render.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		render.end();
		Gdx.gl.glDisable(GL10.GL_BLEND);
	}
	
	public void update(float delta) {
		color.a -= delta*1000 * (1.0f / fadeTime);
		if (color.a < 0) {
			color.a = 0;
		}
	}

	public void preRender() {
	}

	public void init(State firstState, State secondState) {
	}

	@Override
	public void complete() {
		render.dispose();
		render = null;
		color = null;
	}
}
