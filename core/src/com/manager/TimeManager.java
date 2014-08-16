package com.manager;

public class TimeManager {
	
	long time;
	float time2;
	
	public static long getLongTime(){
		return getInstance().time;
	}
	
	public static long getFloatTime(){
		return getInstance().time;
	}
	
	public void update(float delta){
		time+=delta*1000;
		time2+=delta;
	}
	
	public static TimeManager getInstance(){
		return SingletonHolder._instance;
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {
		protected static final TimeManager _instance = new TimeManager();
	}
}
