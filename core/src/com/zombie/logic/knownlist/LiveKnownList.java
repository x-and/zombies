package com.zombie.logic.knownlist;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.manager.LightManager;
import com.zombie.logic.GameWorld;
import com.zombie.logic.enums.ObjectType;
import com.zombie.logic.object.GameObject;
import com.zombie.logic.object.LiveObject;

public class LiveKnownList extends ObjectKnownList {
	
	int visibleDistance = 350;
	private Map<Integer, LiveObject> visibleObjects;

	
	public LiveKnownList(GameObject obj) {
		super(obj);
		knowDistance = 500;
	}

	public boolean addKnownObject(GameObject object) {
		if (object.type != ObjectType.LIVE)
			return super.addKnownObject(object);
		
		boolean result = false;
		result = super.addKnownObject(object);
		if (result && objectIsVisible((LiveObject) object))
			getVisibleObjects().put(object.hashCode(), (LiveObject) object);
		return result;
	}
	
	public boolean removeKnownObject(GameObject object) {
		if (super.removeKnownObject(object)){
			if (object.type == ObjectType.LIVE)
				getVisibleObjects().remove(object.hashCode());
		}else 
			return false;
		return true;
	}
	
	// ��� �� ��������� ������ ������� ��������
	protected void forgetObjects() {
		super.forgetObjects();
		for (LiveObject object : getVisibleObjects().values()){
			if (!objectIsVisible(object)){
				getVisibleObjects().remove(object.hashCode());
				if (getOwner().getAI().getTarget() == object)
					getOwner().getAI().targetLost();
			}
		}
		
		for(GameObject object : getKnownObjects().values()){
			if (object.type !=ObjectType.LIVE)
				continue;
			if (!getVisibleObjects().containsKey(object.hashCode()))
				if (objectIsVisible((LiveObject) object))
					getVisibleObjects().put(object.hashCode(), (LiveObject) object);
		}
	}
	
	boolean objectIsVisible(LiveObject object){
		float distance = getOwner().dst(object);
		if (!LightManager.pointAtLight(object.getX(), object.getY()))
			distance*=2;
		if (distance < visibleDistance || object.inVehicle())
			if (GameWorld.level.grid.LOSCheck(getOwner(), object)){
				return true;
			}
				
		return false;
	}
	
	public Map<Integer, LiveObject> getVisibleObjects() {
		if (visibleObjects == null)
			visibleObjects = new ConcurrentHashMap<Integer, LiveObject>();
		return visibleObjects;
	}
	

	public LiveObject getOwner() {
		return (LiveObject) owner;
	}

}
