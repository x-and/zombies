package com.zombie.logic.object.vehicle.v2;

import com.badlogic.gdx.math.Vector2;

public class CarMath {
	public static final float FLT_EPSILON = 1.192092896e-07F;

	public static float clamp(float a, float low, float height){
		return Math.max(low, Math.min(a, height));
	}
	
	public static Vector2 max(Vector2 a, Vector2 b){
		return new Vector2(Math.max(a.x, b.x), Math.max(a.y, b.y));
	}
	
	public static Vector2 min(Vector2 a, Vector2 b){
		return new Vector2(Math.min(a.x, b.x), Math.min(a.y, b.y));
	}
	
	public static Vector2 multiply(float s, Vector2 a) {
		return new Vector2(s * a.x, s * a.y);
	}

	public static Vector2 multiply(Vector2 a, float s) {
		return new Vector2(a.x * s, a.y * s);
	}

	public static Vector2 minus(Vector2 a) {
		a.x = (-a.x);
		a.y = (-a.y);
		return a;
	}

	public static float normalize(Vector2 vector) {
		float length = vector.len();

		if (length < FLT_EPSILON) {
			return 0.0f;
		}
		float invLength = 1.0f / length;
		vector.x = vector.x * invLength;
		vector.y = vector.y * invLength;

		return length;
	}
}
