package com.path;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntArray;

public class NavPath {
	private final IntArray stepsX = new IntArray();
	private final IntArray stepsY = new IntArray();

	/**
	 * Get the length of the path, i.e. the number of steps.
	 * 
	 * @return The number of steps in this path
	 */
	public int getLength() {
		return stepsX.size;
	}

	public Vector2 getStep(int index, Vector2 out) {
		out.set(getX(index), getY(index));
		return out;
	}

	public int getX(int index) {
		return stepsX.get(index);
	}

	public int getY(int index) {
		return stepsY.get(index);
	}

	public void appendStep(int x, int y) {
		stepsX.add(x);
		stepsY.add(y);
	}

	public void reverse() {
		stepsX.reverse();
		stepsY.reverse();
	}

	public void clear() {
		stepsX.clear();
		stepsY.clear();
	}
}