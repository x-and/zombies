package com.zombie.logic.ai;

public abstract class UpdateReason implements Runnable{

	AI ai;
	public UpdateReason(AI ai) {
		this.ai = ai;
	}

}
