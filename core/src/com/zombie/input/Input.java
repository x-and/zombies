package com.zombie.input;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.InputProcessor;

public abstract class Input implements InputProcessor {

	public static int pointerY;
	public static int pointerX;
	static List<InputProcessor> list = new ArrayList<InputProcessor>();

	public boolean hit = false;
	public boolean playerUp, playerDown, playerLeft, playerRight, isRunning;
	public int playerAngle;
	
	public abstract void update( float f);
	
	@Override
	public boolean keyDown(int keycode) {
		for(InputProcessor input : list)
			if (input.keyDown(keycode))
				return true;
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		for(InputProcessor input : list)
			if (input.keyUp(keycode))
				return true;
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		for(InputProcessor input : list)
			if (input.keyTyped(character))
				return true;
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		for(InputProcessor input : list)
			if (input.touchDown(screenX, screenY, pointer, button))
				return true;
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		for(InputProcessor input : list)
			if (input.touchUp(screenX, screenY, pointer, button))
				return true;
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		for(InputProcessor input : list)
			if (input.touchDragged(screenX, screenY, pointer))
				return true;
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		for(InputProcessor input : list)
			if (input.mouseMoved(screenX, screenY))
				return true;
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		for(InputProcessor input : list)
			if (input.scrolled(amount))
				return true;
		return false;
	}
	
	public static void addInputProcessor(InputProcessor input) {
		list.add(input);
	}

	public static void removeInputProcessor(InputProcessor input) {
		list.remove(input);
	}

	public static Input init() {
		Input input = null;
//		if (Gdx.app.getType() == ApplicationType.Android)
//			input = new AndroidInput();
//		else
			input = new DesktopInput();
		return input;
	}

	public void resetPlayerMove() {
		playerUp = false;
		playerDown = false;
		playerLeft = false;
		playerRight = false;
		hit = false;
		isRunning = false;
	}
	
}
