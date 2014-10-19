package com.physics;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.esotericsoftware.minlog.Log;
import com.manager.LightManager;
import com.zombie.logic.object.PhysicObject;

/**����� ������ ���. ��� � ��������� ��� 60 ��� � ���*/
public class Physics implements Runnable{

	public static final int hertz = 60;
	public static World world;
	static Queue<Runnable> tasks = new LinkedBlockingQueue<Runnable>();
	Thread thread;
	
	public Physics(){
		thread = new Thread(this);
		thread.setName("Physics thread");
		thread.setPriority(Thread.NORM_PRIORITY+2);
		thread.start();
		init();
	}
	
	public void init() {
		if (world != null)
			world.dispose();
		tasks.clear();
		world = new World(Vector2.Zero,true);
		world.setContactListener(new ContactListener());
		world.setContactFilter(new ContactFilter());
		world.setDestructionListener(new DestructionListener());
		BodyFactory.world = world;
	}

	@Override
	public void run() {
		while(true){
			long started = System.currentTimeMillis();
			update();
			long elapsed = System.currentTimeMillis() - started;
			long sleep = 1000/hertz-elapsed;
			if (sleep < 0)
				sleep = 0;
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				Log.error("physics", e);
			}
		}
	}
	public void update(){
		if (world == null)
			return;
		float time = 1000f/hertz/1000f;
		try{
			LightManager.update(time);
			while(!tasks.isEmpty())
				tasks.poll().run();
			world.step(time, 3, 3);
		} catch (Exception e) {Log.debug("physics", e);}
	}
	
	public static void remove(Body b){
		if (b == null)
			 return;
		task(new BodyDestroyer(b));
	}

	public static void add(final PhysicObject obj) {
		if (obj == null)
			return;
		task(new Runnable(){
			@Override
			public void run() {
				obj.createBody();
			}});
	}
	
	public static void task(Runnable run) {
		tasks.add(run);
	}
	
	public static Physics getInstance(){
		return SingletonHolder._instance;
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {
		protected static final Physics _instance = new Physics();
	}

	static class BodyDestroyer implements Runnable{
		
		Body body;
		BodyDestroyer(Body b){
			body = b;
		}
		@Override
		public void run() {
			world.destroyBody(body);
		}
	}


	
}
