package com.manager;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.box2d.Filter;
import com.droidinteractive.box2dlight.Light;
import com.droidinteractive.box2dlight.RayHandler;
import com.esotericsoftware.minlog.Log;
import com.manager.TimeManager.Time;
import com.manager.TimeManager.TimeChangeListener;
import com.physics.Physics;

public class LightManager  implements TimeChangeListener{

	public static RayHandler handler;
	public static Color ambient = new Color(Color.DARK_GRAY).mul(0.2f);
	
	public static void initLights() {
		ambient.a = 1;
		if (handler != null){
			handler.clear();
			handler.setWorld(Physics.world);
		} else
			handler = new RayHandler(Physics.world); 
		handler.setCulling(true);
		handler.setShadows(true);
		handler.setBlur(true);
		handler.setAmbientLight(ambient);
		RayHandler.isDiffuse = true;
		RayHandler.shaderDiffuse = 10;

		Light.setContactFilter(new Filter());
	}
	
	public static void render(){
		if (handler != null)
			try{
			handler.render();
			} catch(Exception e){Log.error("LightManager", e);}
	}

	public static void setCombinedMatrix(Matrix4 combined) {
		if (handler != null)
			handler.setCombinedMatrix(combined);
	}
	
	static Color to = new Color();
	static float time;
	
	public static void update(float delta){
		if (handler != null)
			handler.update();
		delta*=1000;
		if (time != 0){
			ambient.lerp(to, time/TimeManager.getTimeFor(TimeManager.currentTime));
			time -=delta;
			if (time < 0)
				time = 0;
			System.out.println(time + " _ " + delta);
			handler.setAmbientLight(ambient);
		}
	}

	public static boolean pointAtLight(float x, float y) {
		if (handler == null)
			return false;
		return handler.pointAtLight(x, y);
	}
	
	@Override
	public void changed(Time newtime) {
		if (newtime == Time.DAYTIME){
			to.set(Color.WHITE).mul(0.6f);
		} else if (newtime == Time.DUSKTIME || newtime == Time.DAWNTIME) {
			to.set(Color.DARK_GRAY).mul(0.1f);
		} else {  
			to.set(Color.BLACK).mul(0.05f);
		}
		to.a = 1;
		time = TimeManager.getTimeFor(newtime)/4;
	}
}
