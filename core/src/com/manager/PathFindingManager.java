package com.manager;

import com.badlogic.gdx.utils.AtomicQueue;
import com.esotericsoftware.minlog.Log;
import com.path.AStarPathFinder;
import com.path.NavPath;
import com.path.PathFinder;
import com.path.PathFindingRequest;
import com.zombie.C;
import com.zombie.logic.level.Level;


//TODO увеличить размер сетки в 2-4 раза (более точный путь)
public class PathFindingManager implements Runnable{

	static int sleep = 10;
	static Thread thread;	
	static PathFinder finder;
	AtomicQueue<PathFindingRequest> searchQueue;
	
	PathFindingManager(){
		searchQueue = new AtomicQueue<PathFindingRequest>(100) ;
		thread = new Thread(this);
		thread.setName("PathFinding Thread");
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
		Log.info("pathfinding", "init completed");
	}
	
	public static void init(Level level){
		finder = new AStarPathFinder(level, 150, true);
		getInstance().searchQueue = new AtomicQueue<PathFindingRequest>(100) ;
	}

	@Override
	public void run() {
		while(true){
			try{
				PathFindingRequest req = null;
				int count = 10;
				while((req = searchQueue.poll()) != null){
					NavPath path = new NavPath();
					finder.findPath(req.requester, (int)req.requester.getX()/C.TILESIZE, (int)req.requester.getY()/C.TILESIZE, (int)req.pos.x/C.TILESIZE, (int)req.pos.y/C.TILESIZE, path);
					req.searchEnded(path);
					count--;
					if (count <= 0)
						break;
				}
				Thread.sleep(sleep);
			} catch(Exception e){
				Log.error("pathfinding", e);
			}
		}
	}
	
	public static void request(PathFindingRequest request) {
		getInstance().searchQueue.put(request);
	}
	
	public static PathFindingManager getInstance(){
		return SingletonHolder._instance;
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {
		protected static final PathFindingManager _instance = new PathFindingManager();
	}

}
