package com.zombie.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.MathUtils;
import com.zombie.ZombieGame;
import com.zombie.state.GameState;
import com.zombie.util.Cam;

public class DesktopInput extends Input {

	static boolean[] pressed = new boolean[256];
	static boolean[] mouseButtons = new boolean[3];
	
	public static boolean isPressed(int key){
		return pressed[key];
	}
	
	public static boolean isMousePressed(int key){
		return mouseButtons[key];
	}
	
	@Override
	public boolean keyDown(int keycode) {
		pressed[keycode] = true;
		return super.keyDown(keycode);
	}

	@Override
	public boolean keyUp(int keycode) {
		pressed[keycode] = false;
		return super.keyUp(keycode);
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		boolean b = super.touchDown(screenX, screenY, pointer, button);
		if (b)
			return true;
		pointerX = screenX;
		pointerY = screenY;
		mouseButtons[button] = true;
		if (button == Buttons.LEFT)
			hit = true;
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		boolean b = super.touchUp(screenX, screenY, pointer, button);
		if (b)
			return true;
		pointerX = screenX;
		pointerY = screenY;
		mouseButtons[button] = false;
		if (button == Buttons.LEFT)
			hit = false;
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		pointerX = screenX;
		pointerY = screenY;
		return super.touchDragged(screenX, screenY, pointer);
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		for(InputProcessor input : list)
			if (input.mouseMoved(screenX, screenY))
				return true;
		pointerX = screenX;
		pointerY = screenY;
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		for(InputProcessor input : list)
			if (input.scrolled(amount))
				return true;	
		return false;
	}

	@Override
	public void update(float f) {
		if (ZombieGame.getInstance().getCurrentState() != GameState.getInstance())
			return;
		//FIXME when camera center is not player - player's angle is bugged
		float rads = MathUtils.atan2((Cam.offsetY-Gdx.graphics.getHeight()/2+pointerY)-GameState.player.getY(), (Cam.offsetX-Gdx.graphics.getWidth()/2+pointerX)-GameState.player.getX());
//		float rads = MathUtils.atan2(pointerY-GameState.player.getY(), pointerX-GameState.player.getX());

		playerAngle = (int) (360 - rads* MathUtils.radDeg);
		if (playerAngle > 360)
			playerAngle -=360;
		if (playerAngle < 0)
			playerAngle +=360;
		
		playerUp = isPressed(Keys.W);
		playerDown = isPressed(Keys.S);
		playerLeft = isPressed(Keys.A);
		playerRight = isPressed(Keys.D);
		isRunning = isPressed(Keys.SHIFT_LEFT);
	}
}
