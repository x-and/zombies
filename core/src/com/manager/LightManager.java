package com.manager;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.box2d.Filter;
import com.droidinteractive.box2dlight.Light;
import com.droidinteractive.box2dlight.RayHandler;
import com.esotericsoftware.minlog.Log;
import com.physics.Physics;

public class LightManager {

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
	
	public static void update(){
		if (handler != null)
			handler.update();
	}

	public static boolean pointAtLight(float x, float y) {
		if (handler == null)
			return false;
		return handler.pointAtLight(x, y);
	}
	
    static ShaderProgram shader;
    
	public static void startAmbientShader(){
//		Gdx.gl20.glEnable(GL20.GL_BLEND);
        shader = ResourceManager.getShader("shadow");
        if (RayHandler.isDiffuse) {
                shader = ResourceManager.getShader("diffuse");
                shader.begin();
//                Gdx.gl20.glBlendFunc(GL20.GL_DST_COLOR, GL20.GL_SRC_COLOR);
                shader.setUniformf("ambient", ambient.r, ambient.g, ambient.b, ambient.a);
        } else {
                shader.begin();
//                Gdx.gl20.glBlendFunc(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
                shader.setUniformf("ambient", ambient.r * ambient.a, ambient.g * ambient.a,
                		ambient.b * ambient.a, 1f - ambient.a);
        }
	}
	
	public static void endAmbientShader(){
		shader.end();
//		Gdx.gl20.glDisable(GL20.GL_BLEND);
	}
	
	
}
