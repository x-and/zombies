package com.zombie.logic.knownlist;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import com.manager.ThreadPoolManager;
import com.zombie.logic.GameWorld;
import com.zombie.logic.object.GameObject;

public class ObjectKnownList {
	
	protected GameObject owner;
	private Map<Integer, GameObject> knownObjects;

	public int knowDistance = 500;
	public transient ScheduledFuture<?> updateTask;

	public ObjectKnownList(GameObject obj) {
		owner = obj;
	}

	public boolean addKnownObject(GameObject object) {
		if (object == null || object == getOwner()) {
			return false;
		}
		if (knowsObject(object)) {
			return false;
		}
		if (object.distanceToObject(getOwner()) > knowDistance) {
			return false;
		}
		getKnownObjects().put(object.hashCode(), object);
		return true;
	}

	public final boolean knowsObject(GameObject object) {
		return getOwner() == object
				|| getKnownObjects().containsKey(object.hashCode());
	}

	public void removeAllKnownObjects() {
		getKnownObjects().clear();
	}

	public boolean removeKnownObject(GameObject object) {
		if (object == null)
			return false;

		return (getKnownObjects().remove(object.hashCode()) != null);
	}

	public synchronized void updateKnownObjects() {
		try {
			findCloseObjects();
			forgetObjects();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void findCloseObjects() {
		for (GameObject obj : GameWorld.objects.values()) {
			if (obj == null)
				continue;
			if (obj == owner)
				continue;
			addKnownObject(obj);
			if (obj.getKnownList() != null)
				obj.getKnownList().addKnownObject(getOwner());
		}
	}

	protected void forgetObjects() {
		if (knownObjects == null || knownObjects.size() == 0)
			return;
		for (GameObject obj : getKnownObjects().values()) {
			if (obj.distanceToObject(getOwner()) > knowDistance)
				removeKnownObject(obj);
			if (obj.isDead())
				removeKnownObject(obj);
		}
	}

	public GameObject getOwner() {
		return owner;
	}

	public void setOwner(GameObject owner) {
		this.owner = owner;
	}

	public Map<Integer, GameObject> getKnownObjects() {
		if (knownObjects == null)
			knownObjects = new ConcurrentHashMap<Integer, GameObject>();
		return knownObjects;
	}

	public void setKnownObjects(Map<Integer, GameObject> knownObjects) {
		this.knownObjects = knownObjects;
	}

	public void setAutoUpdate(int time) {
		if (updateTask == null) {
			updateTask = ThreadPoolManager.scheduleAtFixedRate(
							new KnownListAsynchronousUpdateTask(owner), time,
							time);
		}
	}

	public void removeAutoUpdate() {
		if (updateTask != null) {
			updateTask.cancel(true);
		}
	}

	public static class KnownListAsynchronousUpdateTask implements Runnable {
		private GameObject _obj;

		public KnownListAsynchronousUpdateTask(GameObject obj) {
			_obj = obj;
		}

		public void run() {
			if (_obj != null && !_obj.isDead()) {
				if (GameWorld.objects.containsKey(_obj.hashCode()))
					_obj.getKnownList().updateKnownObjects();
			} else
				ThreadPoolManager.getInstance().removeRunnable(this);
		}
	}

}
