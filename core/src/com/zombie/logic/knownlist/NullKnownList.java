package com.zombie.logic.knownlist;

import com.zombie.logic.object.GameObject;

public class NullKnownList extends ObjectKnownList {

	public NullKnownList(GameObject obj) {
		super(obj);
		knowDistance = 0;
	}

	@Override
	public boolean addKnownObject(GameObject object) {
		return false;
	}

	@Override
	public boolean removeKnownObject(GameObject object) {
		return false;
	}

}
