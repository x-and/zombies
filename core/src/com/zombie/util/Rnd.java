package com.zombie.util;

import java.util.Random;

public class Rnd {

	static Random rnd = new Random();
	
	public static int randomInt(int min, int max) {
		int c = max - min;
		return min+rnd.nextInt(c);
	}
	
	public static int randomInt(int max) {
		return rnd.nextInt(max);
	}

	public static float nextFloat() {
		return rnd.nextFloat();
	}

	public static int nextInt(int i) {
		return rnd.nextInt(i);
	}

	public static boolean nextBoolean() {
		return rnd.nextBoolean();
	}
}
