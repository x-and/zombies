package com.zombie.state;

import static com.zombie.C.GROUP_LAST;
import static com.zombie.C.GROUP_NORMAL;
import static com.zombie.C.GROUP_POST_EFFECT;
import static com.zombie.C.GROUP_POST_NORMAL;
import static com.zombie.C.GROUP_PRE_EFFECT;
import static com.zombie.C.GROUP_PRE_NORMAL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRendererScaled;
import com.badlogic.gdx.utils.Array;
import com.droidinteractive.box2dlight.ConeLight;
import com.droidinteractive.box2dlight.Light;
import com.manager.LightManager;
import com.manager.ResourceManager;
import com.manager.ThreadPoolManager;
import com.manager.TimeManager;
import com.path.Grid;
import com.path.NavPath;
import com.physics.Physics;
import com.zombie.C;
import com.zombie.C.APP;
import com.zombie.C.UI;
import com.zombie.KeyInputListener;
import com.zombie.Renderable;
import com.zombie.ZombieGame;
import com.zombie.achieve.AchieveSystem;
import com.zombie.effect.TextEffect;
import com.zombie.input.Input;
import com.zombie.logic.Building;
import com.zombie.logic.GameWorld;
import com.zombie.logic.enums.ObjectType;
import com.zombie.logic.level.Level;
import com.zombie.logic.level.wave.Spawn;
import com.zombie.logic.level.wave.Wave;
import com.zombie.logic.level.wave.WaveStarter;
import com.zombie.logic.object.GameObject;
import com.zombie.logic.object.interfaces.Searchable;
import com.zombie.logic.object.interfaces.Useable;
import com.zombie.logic.object.live.Player;
import com.zombie.logic.object.vehicle.Car;
import com.zombie.logic.object.vehicle.Vehicle;
import com.zombie.ui.ActionPicker;
import com.zombie.ui.GameUI;
import com.zombie.util.Cam;
import com.zombie.util.Utils;
import com.zombie.util.state.BasicGameState;
import com.zombie.util.state.State;

public class GameState extends BasicGameState {

	private static GameState instance = new GameState();
	public static Player player;
	public ShapeRenderer shapeBatch;
	private Box2DDebugRendererScaled boxrender = new Box2DDebugRendererScaled();
	public static OrthogonalTiledMapRenderer mapRenderer;
	public Wave currentWave;
	public static int zombies;
	
	private static List<ParticleEffect> emitters = new ArrayList<ParticleEffect>();
	public static List<KeyInputListener> keyListeners = new ArrayList<KeyInputListener>();
	
	public long waveStarted;
	public boolean waitNewWave;
	public boolean gameStarted = false;
	public GameUI ui;

	TiledMapTileLayer grass;
	TiledMapTileLayer upper;
	TiledMapTileLayer roads;
	TiledMapTileLayer[] background;
	
	public static Array<Building> buildings = new Array<Building>(false,10);
	
	public static GameState getInstance(){
		return instance;
	}
	
	@Override
	public int getID() {
		return C.STATE_GAME;
	}

	public GameState(){
		super(Gdx.graphics.getWidth(),Gdx.graphics.getHeight(),true,new SpriteBatch(3000));
	}

	@Override
	public void init() {
		shapeBatch = new ShapeRenderer();
		ui = new GameUI();
	}
	
	public void startLevel(Level level){
		Cam.init();
		ZombieGame.input.resetPlayerMove();
		ResourceManager.stopAllSounds();
		AchieveSystem.init();
		if (C.PROFILE != null)
			AchieveSystem.load();
		Cam.setPosition(new Vector3());
		if (mapRenderer != null)
			mapRenderer.dispose();
		mapRenderer = new OrthogonalTiledMapRenderer(level.tiledMap,1,getSpriteBatch());
		player  = new Player(C.START_X, C.START_Y);
		if (C.PROFILE != null)
			C.PROFILE.save.loadPlayer(player);
		GameWorld.addObject(player);
		currentWave = null;
		ThreadPoolManager.schedule(new WaveStarter(0), 5000L);
		zombies = 0;
		grass = (TiledMapTileLayer) GameWorld.level.tiledMap.getLayers().get("grass");
		upper = (TiledMapTileLayer) GameWorld.level.tiledMap.getLayers().get("upperlayer");
		background = new TiledMapTileLayer[GameWorld.level.backgroundCount];
		for(int i = 0;i<GameWorld.level.backgroundCount;i++)
			background[i]=(TiledMapTileLayer) GameWorld.level.tiledMap.getLayers().get(i);
		
//		for(int i =0;i < 6;i++){
//			FogEffect eff = new FogEffect(1f+2*Rnd.nextFloat());
//			eff.position.x = Rnd.nextInt(C.MAP_WIDTH);
//			eff.position.y = Rnd.nextInt(C.MAP_HEIGHT);
//			GameWorld.addEffect(eff);
//		}

		gameStarted = true;	
		player.light = new ConeLight(LightManager.getHandler(), 80, Light.DefaultColor, 400, player.getX(), player.getY(), 0, 25);
		Cam.object = player;
	}
	
	@Override
	public void render() {
		if (!gameStarted)
			return;
		Cam.update();
		
		getSpriteBatch().totalRenderCalls = 0;
		getSpriteBatch().setProjectionMatrix(Cam.projection());
		shapeBatch.setProjectionMatrix(Cam.projection());
		LightManager.setCombinedMatrix(Cam.projection());
		mapRenderer.setView(Cam.camera2d);
		getSpriteBatch().setColor(Color.WHITE);
		getSpriteBatch().begin();
		//background
		for(TiledMapTileLayer layer : background)
			mapRenderer.renderTileLayer(layer);

		for (Building b : buildings){
			if (b.drawInside || b.insideAlpha != 0)
				b.drawInterior(getSpriteBatch(), mapRenderer);
		}
		getSpriteBatch().setColor(Color.WHITE);
		
		for(Renderable r : GameWorld.renderGroups.get(GROUP_PRE_EFFECT))
			if (r.needDraw(Cam.view))
				r.draw(getSpriteBatch(), shapeBatch);
		
		for(ParticleEffect em : emitters)
			em.draw(getSpriteBatch(), Gdx.graphics.getDeltaTime());
		//grass
		mapRenderer.renderTileLayer(grass);

		//pre_normal
		for(Renderable r : GameWorld.renderGroups.get(GROUP_PRE_NORMAL))
			if (r.needDraw(Cam.view))
				r.draw(getSpriteBatch(), shapeBatch);
		
		// normal - objects	
		for(Renderable r : GameWorld.renderGroups.get(GROUP_NORMAL))
			if (r.needDraw(Cam.view))
				r.draw(getSpriteBatch(), shapeBatch);
		// post_normal
		
		
		for (Building b : buildings){
			if (!b.drawInside || b.outsideAlpha != 0)
				b.drawExterior(getSpriteBatch(), mapRenderer);
		}
		
		getSpriteBatch().setColor(Color.WHITE);
		
		for(Renderable r : GameWorld.renderGroups.get(GROUP_POST_NORMAL))
			if (r.needDraw(Cam.view))
				r.draw(getSpriteBatch(), shapeBatch);
		
		getSpriteBatch().end();
		LightManager.render();
		getSpriteBatch().begin();
		
		getSpriteBatch().setColor(LightManager.getAmbient());
//		LightManager.handler.ambientStart();
		for (Building b : buildings){
			if (!b.drawInside || b.outsideAlpha != 0)
				b.drawRoofs(getSpriteBatch(), mapRenderer);
		}
//		LightManager.handler.ambientEnd();
		// post_effects
		for(Renderable r : GameWorld.renderGroups.get(GROUP_POST_EFFECT))
			if (r.needDraw(Cam.view))
				r.draw(getSpriteBatch(), shapeBatch);

		getSpriteBatch().setColor(LightManager.getAmbient());
		getSpriteBatch().enableBlending();

 		mapRenderer.renderTileLayer(upper);
 		
		getSpriteBatch().setColor(Color.WHITE);
		// last		
		
		for(Renderable r : GameWorld.renderGroups.get(GROUP_LAST))
			r.draw(getSpriteBatch(), shapeBatch);

		if (shapeBatch.getCurrentType() != null)
			shapeBatch.end();

		if (APP.DEBUG)
			drawDebugData();
		
		getSpriteBatch().end();
		//FIXME stage.draw() producing 30+ render calls!!!
		draw();
//		Table.drawDebug(this);

		if (player.isDead()){
			getSpriteBatch().begin();
			C.UI.FONT_BIG.drawMultiLine(getSpriteBatch(), "Game Over", 0, Gdx.graphics.getHeight()/2, Gdx.graphics.getWidth(), HAlignment.CENTER);
			getSpriteBatch().end();
		}
//		System.out.println("rendercalls " + getSpriteBatch().totalRenderCalls);
	}

	
	
	private void drawDebugData() {
		boxrender.render(Physics.world, Cam.projection());

		Color d = new Color(Color.RED);
		d.a = 0.3f;
		Utils.beginShapeRenderer(shapeBatch, ShapeType.Filled);
		Grid g = GameWorld.level.grid;
		for(int x = 0; x < g.grid.length;x++)
			for(int y = 0; y < g.grid[0].length;y++){
				if (!g.grid[x][y].pass)
					shapeBatch.setColor(d);
				else
					continue;
				shapeBatch.rect(x*C.TILESIZE, y*C.TILESIZE, C.TILESIZE, C.TILESIZE);	
			}
		shapeBatch.end();	

		getSpriteBatch().setProjectionMatrix(getCamera().combined);
		StringBuilder builder = Utils.sb;
		builder.setLength(0);
		builder.append("Total objects: ").append(GameWorld.objects.size()).append(" \n").
//				append("Total effects: ").append(GameWorld.searchQueue.toString()).append(" \n").
				append("FPS: ").append(Gdx.graphics.getFramesPerSecond());
//		getSpriteBatch().begin();
		UI.FONT.drawMultiLine(getSpriteBatch(), builder.toString(),
			0,Gdx.graphics.getHeight()-100);
//		getSpriteBatch().end();
	}

	@Override
	public void update(float delta) {
		if (!gameStarted)
			return;
		GameWorld.update(delta);		
		updateWave(delta);
		ui.update(delta);
		
		for(int i = 0; i < emitters.size();i++){
			ParticleEffect em = emitters.get(i);
			if (em.isComplete())
				emitters.remove(i);
		}
		
		for (Building b : buildings){
			b.update(delta);
		}
	}

//	public void postUpdate(float delta) {
//		Physics.getInstance().update();
//	}
	
	public void updateWave(float f) {
       	if (currentWave!= null){
       		long t = TimeManager.getLongTime()-waveStarted;
       		for(Spawn s: currentWave.spawns.values()){
       			if (s.time < t  && !s.spawned)
       				s.spawn(currentWave);
       		}
       		boolean allSpawned = true;
       		for(Spawn s: currentWave.spawns.values())
       			if (!s.spawned){
       				allSpawned = false;
       			}
       		if (allSpawned && !waitNewWave && zombies < 10){
       			if (GameWorld.level.waves.containsKey(currentWave.id+1)){
       				ThreadPoolManager.getInstance().scheduleGeneral(new WaveStarter(currentWave.id+1), 10000L);
	       			TextEffect eff = new TextEffect();
	       			eff.text = "Next wave starts in 10 sec.";
	       			eff.position.set(350,-270);
	       			eff.position.add(Cam.offsetX-Gdx.graphics.getWidth()/2, Cam.offsetY+Gdx.graphics.getHeight()/2);
	       			GameWorld.addEffect(eff);
	       			waitNewWave = true;
       			}
       		}
       	}
	}
	
	@Override
	public void enter(State from) {
		Input.addInputProcessor(this);
		if (!ui.init)
			ui.init();
		ui.add(this);
	}

	@Override
	public void leave(State to) {
		Input.removeInputProcessor(this);
		ui.remove(this);
	}

	@Override
	public void resize(int width, int height) {
		Cam.setToOrtho(false, width, height);
		setViewport(width,height, true);
		if (ui != null && ui.init){
			ui.remove(this);
			ui.resize(width, height);
			ui.init();
			ui.add(this);
		}
	}
	
	//FIXME zoom
	@Override
	public boolean scrolled(int amount) {
		if (!super.scrolled(amount) && C.APP.DEBUG){
			
			if (amount > 0)
				Cam.zoom+= 0.05f;
			else
				Cam.zoom-= 0.05f;
			return true;
		}
		return false;
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (super.touchUp(screenX, screenY, pointer, button))
			return true;
		if (player.isDead())
			return true;
		if (ActionPicker.t != null && ActionPicker.t.hit(screenX, screenY, false) == null){
			ActionPicker.t.remove();
			ActionPicker.t = null;
		}
		if (button == Buttons.MIDDLE){
			Cam.zoom = 1;
			return true;
		}
		if (button == Buttons.RIGHT){
			float x = Cam.offsetX-Gdx.graphics.getWidth()/2+screenX;
			float y = Cam.offsetY-Gdx.graphics.getHeight()/2 + Gdx.graphics.getHeight()-screenY;
			GameObject target = null;
			for(GameObject obj : GameWorld.objects.values()){
				if (obj.intersects(x, y, 8, 8)){
					target = obj;
					break;
				}
			}
			if (target != null)
				addActor(ActionPicker.get(target));
			
//			if (target instanceof LiveObject){
//				LiveObject o = (LiveObject) target;
//			}
			
			return true;
		}	
		
		return false;
	}
	
	@Override
	public void pause(){
		super.pause();
	}
	
	@Override
	public void resume(){
		super.resume();
		if (ui.hide)
			this.pauseUpdate();
	}
	
	
	public Map<Integer,NavPath> pathes = new ConcurrentHashMap<Integer,NavPath>();
	
	@Override	
	public boolean keyUp(int keyCode) {
		if (super.keyUp(keyCode))
			return true;

		// non player controls
		if (keyCode == Keys.F12){
			APP.DEBUG = !APP.DEBUG;
			return true;
		}
		if (keyCode == Keys.F1){
			LightManager.getHandler().setAmbientLight(1);
			LightManager.getHandler().setBlur(true);
			LightManager.getHandler().setBlurNum(1);
			player.light.setSoft(true);
			player.light.setSoftnessLength(0);
			return true;
		}
		if (keyCode == Keys.F3){
			GameWorld.addObject(new Car(50, 100, player.getPos().current, (float) Math.PI, 12, 30, 40));
			return true;
		}
		for (KeyInputListener in : keyListeners)
			if (in.onKeyUp(keyCode))
				return true;
		
		// player controls
		if (player.isDead())
			return false;
		
		if (keyCode == Keys.NUM_1){
			ui.useSlot(0);
			return true;
		}
		if (keyCode == Keys.NUM_2){
			ui.useSlot(1);
			return true;
		}
		if (keyCode == Keys.NUM_3){
			ui.useSlot(2);
			return true;
		}
		if (keyCode == Keys.F2){
			player.light.setActive(!player.light.isActive());
			return true;
		}
		if (keyCode == Keys.SPACE){
			if (player.vehicle != null)
				player.vehicleExited(player.vehicle);
			else
			for (GameObject obj : player.getKnownList().getKnownObjects().values()){
				if (obj.distanceToObject(player) > (obj.getW()+obj.getH())/2)
					continue;
				if (obj instanceof Useable){
					((Useable) obj).use();
					break;
				}
				if (obj instanceof Searchable){
					player.startSearching(obj);
//					((Searchable) obj).searched(player);
					break;
				}
				if (obj.type == ObjectType.VEHICLE){
//					if (obj.distanceToObject(player) < (obj.getW()+obj.getH())){
						player.vehicleEntered((Vehicle) obj, true);
						break;
					}
			}
			return true;
		}
		if (keyCode == Keys.F){
			player.itemPosition = player.items.indexOf(player.getItemById(11),true);
			player.use();
			return true;
		}

		if (keyCode == Keys.R){
			player.reload();
			return true;
		}
		if (keyCode == Keys.T){
			if (player.weaponArmed)
				player.disarm();
			else
				player.arm();
			return true;
		}		
		if (keyCode == Keys.Q){
			if (!player.weaponArmed || (player.reload !=null && !player.reload.isDone()))
				return false;
			int n = player.weapons.indexOf(player.getWeapon(),true);
			if (n == 0)
				player.setWeapon(player.weapons.get(player.weapons.size-1));
			else
				player.setWeapon(player.weapons.get(n-1)); 
			return true;
		}
		if (keyCode == Keys.E){
			if (!player.weaponArmed || (player.reload !=null && !player.reload.isDone()))
				return false;
			int n = player.weapons.indexOf(player.getWeapon(),true);
			if (n == player.weapons.size-1)
				player.setWeapon(player.weapons.get(0));
			else
				player.setWeapon(player.weapons.get(n+1)); 
			return true;
		}
		return false;
	}

	public static void addEmitter(ParticleEffect em) {
		emitters .add(em);
		em.start();
	}

	public static boolean containsBuilding(String name) {
		for (Building b : buildings)
			if (b.name.equals(name))
				return true;
		return false;
	}

	public static Building getBuilding(String name) {
		for (Building b : buildings)
			if (b.name.equals(name))
				return b;
		return null;
	}
}
