package com.zombie.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.zombie.input.Input;
import com.zombie.logic.enums.ObjectType;
import com.zombie.logic.object.GameObject;
import com.zombie.logic.object.LiveObject;

public class Cam {

	public static OrthographicCamera camera2d;
	public static Rectangle view = new Rectangle();
	public static GameObject object;

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
		camera2d.zoom = MathUtils.clamp(camera2d.zoom+zoom, 0.75f, 1.5f);
	}
	
	public static void moveCam() {
		float objX = offsetX,objY = offsetY;
		if (object == null){
			if (Input.pointerX <= 20){
				objX  = -20 + offsetX;
			} else if (Input.pointerX >= Gdx.graphics.getWidth() - 20){
				objX  = 20 + offsetX;
			}
			if (Input.pointerY <= 20){
				objY  = 20 + offsetY;
			} else if (Input.pointerY >= Gdx.graphics.getHeight() - 20){
				objY  = -20 + offsetY;
			}			
			if (objX  == 0 && objY == 0 )
				return;
		} else {
			objX = object.getX();
			objY = object.getY();
			if (object.type == ObjectType.LIVE && ((LiveObject) object).inVehicle()){
				objX = ((LiveObject) object).vehicle.getX();
				objY = ((LiveObject) object).vehicle.getY();
			}
		}
		float x1 = Math.round((offsetX-(objX)));
		float y1 = Math.round((offsetY-(objY)));
		offsetX -= x1/8;
		offsetY -= y1/8;
		camera2d.position.set(Math.round(offsetX), Math.round(offsetY), 0);
	}

	public static void setToOrtho(boolean b, int width, int height) {
		if (camera2d == null)
			init();
		camera2d.setToOrtho(b, width, height);
	}

	public static boolean contains(Vector2 vector) {
		return view.contains(vector);
	}

	public static void zoom(int amount) {
		if (amount > 0)
			setZoom(0.05f);
		else
			setZoom(-0.05f);
	}
}
