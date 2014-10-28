package com.zombie.logic;

import static com.zombie.C.GROUP_COUNT;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.w3c.dom.Node;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.minlog.Log;
import com.manager.LightManager;
import com.manager.PathFindingManager;
import com.manager.QuestManager;
import com.manager.TimeManager;
import com.physics.BodyFactory;
import com.physics.Physics;
import com.zombie.C;
import com.zombie.Renderable;
import com.zombie.effect.AbstractEffect;
import com.zombie.logic.enums.ObjectType;
import com.zombie.logic.level.Level;
import com.zombie.logic.object.GameObject;
import com.zombie.logic.object.LiveObject;
import com.zombie.logic.object.PhysicObject;
import com.zombie.logic.quest.Quest;
import com.zombie.state.GameState;
import com.zombie.util.XMLUtils;

public class GameWorld {

	public static Level level;
	public static ConcurrentHashMap<Integer,GameObject> objects = new ConcurrentHashMap<Integer,GameObject>();
	public static Map<Integer,List<Renderable>> renderGroups = new ConcurrentHashMap<Integer,List<Renderable>>(C.GROUP_COUNT,1f);
	public static List<AbstractEffect> effects = new ArrayList<AbstractEffect>();

	public static void update(float delta){
		for(GameObject obj : objects.values())
			obj.update(delta);

		int delta2 = Math.round(delta*1000);
       	for(int i = 0; i < effects.size();i++) {
       		AbstractEffect e = effects.get(i);
       		e.update(delta2);
    		if (e.getLifeTime() <= 0){
    			removeEffect(e);
    		}	
       	}
       	TimeManager.getInstance().update(delta);
	}	

	public static void init(int id){
		Log.info("world", "Init started");
		GameState.getInstance().gameStarted = false;
		for(GameObject obj : objects.values())
			removeObject(obj);
		createRenderGroups();
		objects.clear();
		effects.clear();
		
		if (level != null)
			level.dispose();

		Physics.getInstance().init();
		TimeManager.getInstance().init();
		LightManager.initLights();
		GameState.buildings.clear();
		loadLevel(id);
		Physics.task(new Runnable(){
			@Override
			public void run() {
				BodyFactory.setupDimension(C.MAP_WIDTH, C.MAP_HEIGHT);
			}});

		QuestManager.levelLoaded();
		PathFindingManager.init(level);
		GameState.getInstance().startLevel(level);

		Log.info("world", "Level \""+level.name+"\" init complete");
	}
	
	private static GameWorld instance;	
	
	public static GameWorld getInstance(){
		if (instance == null)
				instance = new GameWorld();
		return instance;
	}
	
	private static void loadLevel(int id){
		Node first = XMLUtils.getNodeForStream(Gdx.files.internal("data/data/level.xml").read());
		for(int i = 0; i < first.getChildNodes().getLength();i++ ){
			Node n = first.getChildNodes().item(i);	
			if (n.getNodeName().equalsIgnoreCase("level")){
				if (id == Integer.parseInt(n.getAttributes().getNamedItem("id").getNodeValue()))
					Level.loadLevel(n,false);
			}
		}
	}

	public static void addEffect(final AbstractEffect eff) {
		effects.add(eff);
		Gdx.app.postRunnable(new RenderableAdder(eff,true));	
	}

	public static void removeEffect(AbstractEffect eff) {
		effects.remove(eff);
		Gdx.app.postRunnable(new RenderableAdder(eff,false));
		eff.remove();
	}
	public static GameObject getObjectByName(Class<? extends GameObject> c,String name){
		for(GameObject obj : objects.values())
			if (c.isInstance(obj) && obj.name.equalsIgnoreCase(name))
				return obj;
		return null;
	}
	
	public static void addObject(GameObject object) {
		objects.put(object.hashCode(), object);
		Gdx.app.postRunnable(new RenderableAdder(object,true));	
		if (object instanceof PhysicObject)
			Physics.add((PhysicObject) object);
		if (object.type == ObjectType.LIVE){
			((LiveObject) object).getAI().spawned();
			for(Quest q : QuestManager.quests.values()){
				if (!q.isFinished())
					q.onSpawn((LiveObject) object);
			}
		}
	}

	public static void removeObject(final GameObject object) {
		objects.remove(object.hashCode());
		Gdx.app.postRunnable(new RenderableAdder(object,false));
		Physics.task(new Runnable(){
			public void run() {
				object.dispose();
			}});
		if (object instanceof LiveObject)
			((LiveObject) object).getAI().remove();
	}
	
	private static void addToRenderGroup(Renderable r){
		if (r == null)
			return;
		if (!renderGroups.containsKey(r.getRenderGroup()))
			renderGroups.put(r.getRenderGroup(), new ArrayList<Renderable>());
		if (!renderGroups.get(r.getRenderGroup()).contains(r))
			renderGroups.get(r.getRenderGroup()).add(r);
	}
	
	private static void removeFromRenderGroup(Renderable r){
		if (r == null)
			return;
		if (renderGroups.containsKey(r.getRenderGroup()))
			renderGroups.get(r.getRenderGroup()).remove(r);
	}
	
	private static void createRenderGroups() {
		renderGroups.clear();
		for(int i = 0; i < GROUP_COUNT;i++)
			renderGroups.put(i, new ArrayList<Renderable>());
	}

	private static class RenderableAdder implements Runnable {
		
		Renderable renderable;
		boolean add = true;
		RenderableAdder(Renderable r, boolean add){
			renderable = r;
			this.add = add;
		}
		@Override
		public void run() {
			if (add)
				addToRenderGroup(renderable);
			else
				removeFromRenderGroup(renderable);
		}
	}

	public static boolean containsObject(GameObject object) {
		return objects.containsKey(object.hashCode());
	}
	
}
