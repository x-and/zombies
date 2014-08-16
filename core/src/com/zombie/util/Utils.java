package com.zombie.util;

import java.text.SimpleDateFormat;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.ParticleEmitterBox2DScalable;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.path.NavPath;
import com.physics.Physics;
import com.zombie.C;
import com.zombie.logic.GameWorld;
import com.zombie.logic.object.LiveObject;
import com.zombie.state.GameState;

public class Utils {
	
    static SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd hh mm ss a");
    public static StringBuilder sb = new StringBuilder();

	public static StringBuilder build(){
		sb.setLength(0);
		return sb;
	}
	
	
	public static int getLayerIndex(String name, TiledMap map){

		for (int i = 0; i < map.getLayers().getCount();i++)
			if (map.getLayers().get(i).getName().equalsIgnoreCase(name))
				return i;
		return 0;
	}

	public static void beginShapeRenderer(ShapeRenderer shape,	ShapeType type) {
		if (shape.getCurrentType() != type){
			if (shape.getCurrentType() != null)
				shape.end();
			shape.begin(type);
		}
	}

	public static ParticleEmitterBox2DScalable wrapEmitterToBox2d(
			ParticleEmitter em) {
		return new ParticleEmitterBox2DScalable(Physics.world, em);
	}

	public static String getSoundForTile(LiveObject object) {
		int y = (int)object.getY()/C.TILESIZE;
		int x = (int)object.getX()/C.TILESIZE;
		if (object.building != null){
			TiledMapTileLayer layer = GameState.getBuilding(object.building).floor;
			Cell cell = layer.getCell(x,y);
			if (cell != null){
				TiledMapTile tile = cell.getTile();
				String sound = (String) tile.getProperties().get("sound");
				if(sound != null){
					Utils.sb.setLength(0);
					Utils.sb.append("step_").append(sound).append("_");
					return Utils.sb.toString();
				}
			}
		}
		TiledMap map = GameWorld.level.tiledMap;
		for(int i = GameWorld.level.backgroundCount-1 ; i >= 0 ; i--){
			TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(i);
			Cell cell = layer.getCell(x,y);
			if (cell == null)
				continue;
			TiledMapTile tile = cell.getTile();
			String sound = (String) tile.getProperties().get("sound");
			if (sound == null)
				sound = "grass";
			Utils.sb.setLength(0);
			Utils.sb.append("step_").append(sound).append("_");
			return Utils.sb.toString();
		}
		return "step_grass_";
	}
	

	public static NavPath optimizePath(NavPath path) {
		if (path.getLength()== 0 )
			return path;
		NavPath tmp = new NavPath();
		tmp.appendStep(path.getX(0), path.getY(0));
		int x = path.getX(0);
		int y = path.getY(0);;
		for(int i = 1;i < path.getLength()-1;i++){
			if (x == path.getX(i-1) && x == path.getX(i) && x == path.getX(i+1))
				continue;
			if (y == path.getY(i-1) && y == path.getY(i) && y == path.getY(i+1))
				continue;			
			if (x != path.getX(i-1) && x != path.getX(i) && x != path.getX(i+1) && 
				y != path.getY(i-1) && y != path.getY(i) && y != path.getY(i+1))
				continue;
			
			
			x = path.getX(i);
			y = path.getY(i);			
			tmp.appendStep(x, y);
		}
		tmp.appendStep(path.getX(path.getLength()-1), path.getY(path.getLength()-1));
		return tmp;
	}

	static Color temp;
	public static void pushColor(SpriteBatch batch) {
		if (batch == null)
			return;
		temp = batch.getColor();
	}
	
	public static void popColor(SpriteBatch batch) {
		if (batch == null)
			return;
		batch.setColor(temp);
	}
}
