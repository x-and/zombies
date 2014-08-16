package com.zombie.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.zombie.logic.enums.ObjectType;
import com.zombie.logic.object.GameObject;
import com.zombie.logic.object.LiveObject;

public class Cam {

	public static OrthographicCamera camera2d;
	public static Rectangle view = new Rectangle();
	public static GameObject object;
	public static float zoom = 1f;

	public static float offsetX;
	public static float offsetY;
	
	public static void init(){
		camera2d = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera2d.setToOrtho(false);
	}

	public static void setPosition(Vector3 vector3) {
		camera2d.position.set(vector3);
	}

	public static void update() {
		moveCam();
		camera2d.update();
		setView(offsetX-Gdx.graphics.getWidth()/2*Cam.zoom(), offsetY-Gdx.graphics.getHeight()/2*Cam.zoom(), Gdx.graphics.getWidth()*Cam.zoom(), Gdx.graphics.getHeight()*Cam.zoom());
	}

	public static Matrix4 projection() {
		return camera2d.combined;
	}

	public static float zoom() {
		return camera2d.zoom;
	}

	public static void setView(float x, float y, float w, float h) {
		view.set(x, y, w, h);
	}

	public static void setZoom(float zoom) {
		camera2d.zoom = zoom;
	}
	
	public static void moveCam() {
		if (object == null)
			return;
		float objX = object.getX(),objY = object.getY();
		if (object.type == ObjectType.LIVE && ((LiveObject) object).inVehicle()){
			objX = ((LiveObject) object).vehicle.getX();
			objY = ((LiveObject) object).vehicle.getY();
		}

		float x1,y1;
		x1 = (offsetX-(objX));
		y1 = (offsetY-(objY));
		x1 = Math.round(x1);
		y1 = Math.round(y1);
		offsetX -= x1;
		offsetY -= y1;
		if (zoom <= 0)
			zoom = 1;
		Cam.setZoom(zoom);
		camera2d.position.set(offsetX, offsetY, 0);
	}

	public static void setToOrtho(boolean b, int width, int height) {
		if (camera2d == null)
			init();
		camera2d.setToOrtho(b, width, height);
	}

	public static boolean contains(Vector2 vector) {
		return view.contains(vector);
	}
}
