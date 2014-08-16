package com.path;

import com.badlogic.gdx.math.Vector2;
import com.manager.PathFindingManager;
import com.zombie.logic.object.GameObject;
import com.zombie.logic.object.LiveObject;

public abstract class PathFindingRequest {

	public LiveObject requester;
	public GameObject targetObject;
	public Vector2 pos;
	
	public PathFindingRequest(LiveObject requester, Vector2 target){
		this.requester = requester;
		this.pos = target;
	}
	
	public PathFindingRequest(LiveObject owner, GameObject t) {
		this(owner,new Vector2(t.getPos().current));
		targetObject = t;
	}

	public abstract void searchEnded(NavPath path);

	public void find() {
		PathFindingManager.request(this);
	}

}
