package com.zombie.logic.level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTile.BlendMode;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Disposable;
import com.droidinteractive.box2dlight.PointLight;
import com.manager.LightManager;
import com.manager.ResourceManager;
import com.manager.TimeManager;
import com.path.Grid;
import com.path.PathFindingContext;
import com.path.TileBasedMap;
import com.physics.BodyFactory;
import com.physics.Physics;
import com.zombie.C;
import com.zombie.effect.LightEffect;
import com.zombie.logic.Building;
import com.zombie.logic.GameWorld;
import com.zombie.logic.item.Item;
import com.zombie.logic.level.wave.Spawn;
import com.zombie.logic.level.wave.Wave;
import com.zombie.logic.object.Door;
import com.zombie.logic.object.ItemObject;
import com.zombie.logic.object.StaticObject;
import com.zombie.logic.zone.BuildingZone;
import com.zombie.logic.zone.SoundZone;
import com.zombie.logic.zone.Zone;
import com.zombie.state.GameState;
import com.zombie.util.Rnd;

public class Level implements Disposable, TileBasedMap{

	public int 						id = 0;
	public String 					name = "Abandoned City";
	public Map<Integer,Wave> 		waves;
	public Map<Integer,SpawnPoint> 	spawnPoints;
	public Map<Integer,SpawnPoint> 	rndSpawnPoints;
	public Map<Integer,MobDef> 		mobs;
	public List<RandomItemSpawn> 	itemRandomSpawnPoints;
	public TiledMap 				tiledMap;
	public String 					icon;
	public int 						backgroundCount;
	public Grid 					grid;

	public static Level loadLevel(Node node,boolean attribsOnly) {
		Level level = new Level();
		level.icon = node.getAttributes().getNamedItem("icon").getNodeValue();
		level.name = node.getAttributes().getNamedItem("name").getNodeValue();
		level.id = Integer.parseInt(node.getAttributes().getNamedItem("id").getNodeValue());
		if (attribsOnly)
			return level;
		
		level.waves = new HashMap<Integer,Wave>();
		level.spawnPoints = new HashMap<Integer,SpawnPoint>();
		level.rndSpawnPoints = new HashMap<Integer,SpawnPoint>();
		level.mobs = new HashMap<Integer,MobDef>();
		level.itemRandomSpawnPoints = new ArrayList<RandomItemSpawn>();
		
		for(int i = 0; i < node.getChildNodes().getLength();i++ ){
			Node n = node.getChildNodes().item(i);
			if (n.getNodeName().equalsIgnoreCase("waves")) {
				 loadWaves(n,level);
			} else if (n.getNodeName().equalsIgnoreCase("randomSpawn")){
				loadRandomItemSpawn(n,level);
			} else if (n.getNodeName().equalsIgnoreCase("map")){
				TmxMapLoader loader = new TmxMapLoader();
				level.tiledMap = loader.load("data/level/"+n.getAttributes().getNamedItem("resource").getNodeValue());
				TiledMapTileLayer l = (TiledMapTileLayer) level.tiledMap.getLayers().get(0);
				C.MAP_WIDTH = l.getWidth()*C.TILESIZE;
				C.MAP_HEIGHT = l.getHeight()*C.TILESIZE;
				Iterator<TiledMapTile> it = level.tiledMap.getTileSets().getTileSet(0).iterator();
				while(it.hasNext()){
					TiledMapTile tile = it.next();
					if (tile.getProperties().containsKey("sound")){
						tile.setBlendMode(BlendMode.NONE);
					}
				}
				
				loadStaticObjects(level);
				loadLights(level);
				loadStart(level);
				loadBox2d(level);
				loadZones(level);
				loadDoors(level);
				loadBuildings(level);
				level.backgroundCount = Integer.parseInt(n.getAttributes().getNamedItem("backgroundCount").getNodeValue());
			}
		}
		GameWorld.level = level;
		TiledMapTileLayer layer = (TiledMapTileLayer) level.tiledMap.getLayers().get("pathfinding");
		level.grid = new Grid(C.MAP_WIDTH/C.TILESIZE,C.MAP_HEIGHT/C.TILESIZE,layer);
		return level;
	}
	
	private static void loadBuildings(Level level) {
		for (MapObject obj : level.tiledMap.getLayers().get("zones").getObjects()){
			String type = (String) obj.getProperties().get("type");
			if (type.equalsIgnoreCase("building")){
				if (GameState.containsBuilding(obj.getName()))
					continue;
				Building b = new Building(obj.getName());
				b.zone = (BuildingZone) GameWorld.getObjectByName(Zone.class, obj.getName());
				b.zone.building = b;
				b.door = (Door) GameWorld.getObjectByName(Door.class, obj.getName());
				b.floor = (TiledMapTileLayer) level.tiledMap.getLayers().get(obj.getName()+"_floor");
				b.interier = (TiledMapTileLayer) level.tiledMap.getLayers().get(obj.getName()+"_interier");
				b.walls = (TiledMapTileLayer) level.tiledMap.getLayers().get(obj.getName()+"_walls");
				b.exterier = (TiledMapTileLayer) level.tiledMap.getLayers().get(obj.getName()+"_exterier");
				b.roofs = (TiledMapTileLayer) level.tiledMap.getLayers().get(obj.getName()+"_roofs");
				GameState.buildings.add(b);
			}
		}
	}

	private static void loadDoors(Level level) {
		for (MapObject obj : level.tiledMap.getLayers().get("doors").getObjects()){
			RectangleMapObject object = (RectangleMapObject) obj;
			Door door = new Door(object.getRectangle().x+object.getRectangle().width/2,object.getRectangle().y+object.getRectangle().height/2,
									object.getRectangle().width,object.getRectangle().height);
			door.building = object.getName();
			door.name = object.getName();
			door.isLocked = Boolean.parseBoolean((String) object.getProperties().get("isLocked"));
			GameWorld.addObject(door);	
		}
	}

	private static void loadZones(Level level) {
		for (MapObject obj : level.tiledMap.getLayers().get("zones").getObjects()){
			String type = (String) obj.getProperties().get("type");
			Zone zone = null; 
			if (type.equalsIgnoreCase("sound")){
				zone = (SoundZone) GameWorld.getObjectByName(SoundZone.class, obj.getName());
				if (zone == null)
					zone = new SoundZone(obj.getName());
			} else if (type.equalsIgnoreCase("building")){
				zone = (BuildingZone) GameWorld.getObjectByName(BuildingZone.class, obj.getName());
				if (zone == null)
					zone = new BuildingZone(obj.getName());
			}
			Physics.add(zone);
			if (obj instanceof PolygonMapObject)
				zone.setPoly(((PolygonMapObject) obj).getPolygon());
			else if (obj instanceof EllipseMapObject)
				zone.setEllipse(((EllipseMapObject) obj).getEllipse());
			else if (obj instanceof RectangleMapObject)
				zone.setRect(((RectangleMapObject) obj).getRectangle());
			if (zone != null && !GameWorld.containsObject(zone)) 
				GameWorld.addObject(zone);	
		}
	}

	private static void loadBox2d(Level level) {
		for (final MapObject obj : level.tiledMap.getLayers().get("box2d").getObjects()){
			if (obj instanceof RectangleMapObject){
				Physics.task(new Runnable(){

					@Override
					public void run() {
						RectangleMapObject r = (RectangleMapObject) obj;
						BodyFactory.createStaticBox(r.getRectangle().x+r.getRectangle().width/2,
								r.getRectangle().y+r.getRectangle().height/2,r.getRectangle().width,r.getRectangle().height);

					}});
			} else if (obj instanceof PolylineMapObject){
				Physics.task(new Runnable(){
					@Override
					public void run() {
						PolylineMapObject m = (PolylineMapObject) obj;
						float[] xy = m.getPolyline().getVertices();
						BodyFactory.createStaticPolyLine(xy,m.getPolyline().getX(),m.getPolyline().getY());
					}});
			} else if (obj instanceof CircleMapObject){
				Physics.task(new Runnable(){
					@Override
					public void run() {
						CircleMapObject m = (CircleMapObject) obj;
						BodyFactory.createStaticCircle(m.getCircle().x, m.getCircle().y, m.getCircle().radius);
					}});				
			} else if (obj instanceof EllipseMapObject){
				Physics.task(new Runnable(){
					@Override
					public void run() {
						EllipseMapObject m = (EllipseMapObject) obj;
						BodyFactory.createStaticCircle(m.getEllipse().x+m.getEllipse().width/4, m.getEllipse().y+m.getEllipse().width/4, m.getEllipse().width/2);
					}});				
			}
		}
	}

	private static void loadStart(Level level) {
		for (MapObject obj : level.tiledMap.getLayers().get("start").getObjects()){
			Object type = obj.getProperties().get("type");
			if (type.equals("location")){
				C.START_X = (Integer) obj.getProperties().get("x");
				C.START_Y = (Integer) obj.getProperties().get("y");	
			} else if (type.equals("item")){
				float x = (Integer) obj.getProperties().get("x");
				float y = (Integer) obj.getProperties().get("y");
				int id = Integer.parseInt((String) obj.getProperties().get("itemId"));
				ItemObject object = new ItemObject(x, y,Item.getItemById(id));
				GameWorld.addObject(object);
			} else if (type.equals("mob")){
				RectangleMapObject r = (RectangleMapObject) obj;
				SpawnPoint sp = new SpawnPoint();
				sp.id = Integer.parseInt((String) obj.getProperties().get("id"));
				sp.minX = (int) r.getRectangle().x;
				sp.maxX = (int) (r.getRectangle().x+r.getRectangle().width);
				sp.minY = (int) r.getRectangle().y;
				sp.maxY = (int) (r.getRectangle().y+ r.getRectangle().height);
				level.spawnPoints.put(sp.id, sp);
			} else if (type.equals("randomitem")){
				RectangleMapObject r = (RectangleMapObject) obj;
				SpawnPoint sp = new SpawnPoint();
				sp.id = Integer.parseInt((String) obj.getProperties().get("id"));
				sp.minX = (int) r.getRectangle().x;
				sp.maxX = (int) (r.getRectangle().x+r.getRectangle().width);
				sp.minY = (int) r.getRectangle().y;
				sp.maxY = (int) (r.getRectangle().y+ r.getRectangle().height);
				level.rndSpawnPoints.put(sp.id, sp);						
			}
		}
	}

	private static void loadLights(Level level) {
		for (EllipseMapObject obj : level.tiledMap.getLayers().get("lights").getObjects().getByType(EllipseMapObject.class)){
			String c = (String) obj.getProperties().get("color");
			String[] rgba = c.split(",");
			Color color = new Color(Integer.parseInt(rgba[0])/256f,Integer.parseInt(rgba[1])/256f,Integer.parseInt(rgba[2])/256f,Integer.parseInt(rgba[3])/256f);

			LightEffect eff = new LightEffect(new PointLight(LightManager.handler, (int) obj.getEllipse().width, color, obj.getEllipse().width, obj.getEllipse().x+obj.getEllipse().width/2, obj.getEllipse().y+obj.getEllipse().width/2));
			eff.permanent = true;
			if (obj.getProperties().containsKey("blink_time"))
				eff.time = Integer.parseInt((String)obj.getProperties().get("blink_time"));
			if (obj.getProperties().containsKey("blink_percent"))
				eff.percent = Float.parseFloat((String)obj.getProperties().get("blink_percent"));
			if (obj.getProperties().containsKey("image"))
				eff.texture = ResourceManager.getImage((String)obj.getProperties().get("image"));
			if (obj.getProperties().containsKey("static")){
				//FIXME
				eff.light.setStaticLight(true);
			}
			
			String s = (String) obj.getProperties().get("type");
			eff.type = s;
			if (s.equalsIgnoreCase("outdoor"))
				TimeManager.addListener(eff);
			eff.changed(TimeManager.getTime());
				
			GameWorld.addEffect(eff);
		}	
	}

	private static void loadStaticObjects(Level level) {
		for (RectangleMapObject obj : level.tiledMap.getLayers().get("static").getObjects().getByType(RectangleMapObject.class)){
			float d = Integer.parseInt((String) obj.getProperties().get("density"));	
			float a = 0;
			if (obj.getProperties().containsKey("angle"))
				a =  Integer.parseInt((String) obj.getProperties().get("angle"));	
			String mat = (String) obj.getProperties().get("material");
			String geom = (String) obj.getProperties().get("geom");
			String drop = "";
			if (obj.getProperties().containsKey("drop"))
				drop =(String) obj.getProperties().get("drop");
			loadObject(obj.getRectangle().x,obj.getRectangle().y,d,"dynamic", obj.getName(),mat,geom,a,obj.getRectangle().width,obj.getRectangle().height,level,drop);
		}
		for (EllipseMapObject obj : level.tiledMap.getLayers().get("static").getObjects().getByType(EllipseMapObject.class)){
			float d = Integer.parseInt((String) obj.getProperties().get("density"));	
			float a = 0;
			if (obj.getProperties().containsKey("angle"))
				a =  Integer.parseInt((String) obj.getProperties().get("angle"));	
			String mat = (String) obj.getProperties().get("material");
			String geom = (String) obj.getProperties().get("geom");
			String drop = "";
			if (obj.getProperties().containsKey("drop"))
				drop =(String) obj.getProperties().get("drop");
			loadObject(obj.getEllipse().x,obj.getEllipse().y,d,"dynamic", obj.getName(),mat,geom,a,obj.getEllipse().width,obj.getEllipse().height,level,drop);
		}
	}

	private static void loadRandomItemSpawn(Node node,Level level) {
		RandomItemSpawn item = new RandomItemSpawn();
		item.id = Integer.parseInt(node.getAttributes().getNamedItem("id").getNodeValue());
		item.timer = Integer.parseInt(node.getAttributes().getNamedItem("timer").getNodeValue());
		item.chance = Integer.parseInt(node.getAttributes().getNamedItem("chance").getNodeValue());
	
		for(int i = 0; i < node.getChildNodes().getLength();i++ ){
			Node n = node.getChildNodes().item(i);
			if (n.getNodeName().equalsIgnoreCase("spawnPoint")){
				String s = n.getTextContent();
				String[] split = s.split(",");
				for(String sp : split){
					item.spawnPoints.add(level.rndSpawnPoints.get(Integer.parseInt(sp)));
				}
			} else if (n.getNodeName().equalsIgnoreCase("itemId")){
				Item it = Item.getItemById(Integer.parseInt(n.getTextContent()));
				item.drop.add(it);
			}
		}
		level.itemRandomSpawnPoints.add(item);
		item.init();
	}

	private static void loadObject(float x, float y, final float d, final String type, String image, String mat,final String geom, float angle, float w,float h, Level level,String drop) {	
		final StaticObject obj = new StaticObject(x,y);
		obj.image = image;
		obj.material = mat;
		obj.setA(angle);
		obj.setSize(w, h);
		obj.setPhysic(geom,type,d);
		if (drop != ""){
			String[] drops = drop.split(";");
			for(String dr : drops){
				int id = Integer.parseInt(dr.split(":")[0]);
				int chance = Integer.parseInt(dr.split(":")[1]);
				if (Rnd.nextInt(chance) == 0)
					obj.addDrop(Item.getItemById(id));
			}
		}
		obj.setLife();
		GameWorld.addObject(obj);
	}

	private static void loadWaves(Node n, Level level) {
		for(int i = 0; i < n.getChildNodes().getLength();i++ ){
			Node node = n.getChildNodes().item(i);
			if (node.getNodeName().equalsIgnoreCase("wave")) {
				Wave w = new Wave();
				w.id =  Integer.parseInt(node.getAttributes().getNamedItem("id").getNodeValue());
				w.level = level;
				loadSpawns(w,node);
				level.waves.put(w.id, w);
			}
			else if (node.getNodeName().equalsIgnoreCase("mob")){
				loadMobs(level,node);
			}
		}
	}
	
	private static void loadSpawns(Wave w, Node node) {
		for(int i = 0; i < node.getChildNodes().getLength();i++ ){
			Node n = node.getChildNodes().item(i);
			if (n.getNodeName().equalsIgnoreCase("spawn")){
				Spawn sp = new Spawn();
				sp.id = Integer.parseInt(n.getAttributes().getNamedItem("id").getNodeValue());
				sp.time = Integer.parseInt(n.getAttributes().getNamedItem("time").getNodeValue());
				sp.mobId = Integer.parseInt(n.getAttributes().getNamedItem("mobId").getNodeValue());
				sp.spawnPoint = Integer.parseInt(n.getAttributes().getNamedItem("spawnPoint").getNodeValue());
				sp.count = Integer.parseInt(n.getAttributes().getNamedItem("count").getNodeValue());
				w.spawns.put(sp.id, sp);
			}
		}
	}
	
	private static void loadMobs(Level level, Node n) {
		MobDef mob = new MobDef();
		mob.id = Integer.parseInt(n.getAttributes().getNamedItem("id").getNodeValue());
		mob.name = n.getAttributes().getNamedItem("name").getNodeValue();
		mob.image = n.getAttributes().getNamedItem("image").getNodeValue();
		mob.className = n.getAttributes().getNamedItem("class").getNodeValue();
		
		if (n.getAttributes().getNamedItem("size") != null){
			String[] size = n.getAttributes().getNamedItem("size").getNodeValue().split(":");
			mob.width = Integer.parseInt(size[0]);	
			mob.height = Integer.parseInt(size[1]);
		}
		mob.level = Integer.parseInt(n.getAttributes().getNamedItem("lvl").getNodeValue());
		String[] hps = n.getAttributes().getNamedItem("hp").getNodeValue().split(":");
		mob.minHp = Integer.parseInt(hps[0]);
		mob.maxHp = Integer.parseInt(hps[1]);
		String[] dmgs = n.getAttributes().getNamedItem("damage").getNodeValue().split(":");
		mob.minDamage = Integer.parseInt(dmgs[0]);
		mob.maxDamage = Integer.parseInt(dmgs[1]);
		String[] vels = n.getAttributes().getNamedItem("velocity").getNodeValue().split(":");
		mob.minVelocity= Float.parseFloat(vels[0]);
		mob.maxVelocity = Float.parseFloat(vels[1]);
		
		if (n.getAttributes().getNamedItem("defence") != null)
			mob.defence = Integer.parseInt(n.getAttributes().getNamedItem("defence").getNodeValue());	
		if (n.getAttributes().getNamedItem("evasion") != null)
			mob.evasion = Float.parseFloat(n.getAttributes().getNamedItem("evasion").getNodeValue());
		level.mobs.put(mob.id, mob);
	}

	@Override
	public void dispose() {
		tiledMap.dispose();
		grid.dispose();
		grid = null;
		
	}

	@Override
	public int getWidthInTiles() {
		return grid.grid.length;
	}

	@Override
	public int getHeightInTiles() {
		return grid.grid[0].length;
	}

	@Override
	public boolean blocked(PathFindingContext context, int tx, int ty) {
		return !grid.grid[tx][ty].pass;
	}

	@Override
	public float getCost(PathFindingContext context, int tx, int ty) {
		return 1;
	}

}
