package com.physics;

import com.manager.ThreadPoolManager;


public abstract class RunnableListQueryCallback extends ListQueryCallback implements Runnable{

	@Override
	public void isDone(){
		super.isDone();
		ThreadPoolManager.execute(this);
	}
}
