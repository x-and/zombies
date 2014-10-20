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

	private RayHandler handler;
	private Color ambient = new Color(Color.DARK_GRAY).mul(0.2f);
	
	public static RayHandler getHandler(){
		return getInstance().handler;
	}
	
	public static void initLights() {
		getAmbient().a = 1;
		if (getHandler() != null){
			getHandler().clear();
			getHandler().setWorld(Physics.world);
		} else
			setHandler(new RayHandler(Physics.world)); 
		getHandler().setCulling(true);
		getHandler().setShadows(true);
		getHandler().setBlur(true);
		getHandler().setAmbientLight(getAmbient());
		RayHandler.isDiffuse = true;
		RayHandler.shaderDiffuse = 10;

		Light.setContactFilter(new Filter());
	}
	
	private static void setHandler(RayHandler rayHandler) {
		getInstance().handler = rayHandler;
	}

	public static void render(){
		if (getHandler() != null)
			try{
				getHandler().render();
			} catch(Exception e){Log.error("LightManager", e);}
	}

	public static void setCombinedMatrix(Matrix4 combined) {
		if (getHandler() != null)
			getHandler().setCombinedMatrix(combined);
	}
	
	static Color to = new Color();
	static float time,totaltime;
	
	public static void update(float delta){
		if (getHandler() != null)
			getHandler().update();
		delta*=1000;
		if (time != 0){
			getAmbient().lerp(to, 1-time/totaltime);
			time -=delta;
			if (time < 0)
				time = 0;
			getHandler().setAmbientLight(getAmbient());
		}
	}

	public static boolean pointAtLight(float x, float y) {
		if (getHandler() == null)
			return false;
		return getHandler().pointAtLight(x, y);
	}
	
	@Override
	public void changed(Time newtime) {
		if (newtime == Time.DAYTIME)
			to.set(Color.WHITE).mul(0.6f);
		else if (newtime == Time.DUSKTIME || newtime == Time.DAWNTIME)
			to.set(Color.BLACK).add(0.2f,0.2f,0.2f,0.2f);
		else  
			to.set(Color.BLACK).add(0.05f,0.05f,0.05f,0.05f);
		to.a = 1;
		if (newtime == Time.DUSKTIME || newtime == Time.DAWNTIME)
			totaltime = time = TimeManager.getTimeFor(newtime);
		else 
			totaltime = time = TimeManager.getTimeFor(newtime)/8;
	}
	
	public static LightManager getInstance(){
		return SingletonHolder._instance;
	}

	public static Color getAmbient() {
		return getInstance().ambient;
	}

	public static void setAmbient(Color ambient) {
		getInstance().ambient = ambient;
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {
		protected static final LightManager _instance = new LightManager();
	}

}
