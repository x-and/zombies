package com.manager;

import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.minlog.Log;

public class TimeManager {
	
	// total cycle is 2 min (120 sec)
	public final static long DAYTIME = 120*1000; // in seconds
	public final static long NIGHTTIME = 180*1000; // in seconds	
	public final static long DUSKTIME = 30*1000; // in seconds	
	public final static long DAWNTIME = 30*1000; // in seconds	

	public static enum Time {
		DAYTIME,NIGHTTIME,DUSKTIME,DAWNTIME
	}
	
	public static interface TimeChangeListener{
		void changed(Time newtime);
	}
	
	long time;
	float time2;
	static long nextTime = 1000;
	static Time currentTime;
	private static Array<TimeChangeListener> listeners = new Array<TimeChangeListener>();	
	
	public static void addListener(TimeChangeListener l){
		listeners.add(l);
	}
	
	public static void removeListener(TimeChangeListener l){
		listeners.removeValue(l, true);
	}
	
	public static long getLongTime(){
		return getInstance().time;
	}
	
	public static float getFloatTime(){
		return getInstance().time2;
	}
	
	public void update(float delta){
		time+=delta*1000;
		time2+=delta;
		timeUpdate();
	}
	
	private void timeUpdate() {
		if (nextTime > time)
			return;
		currentTime = getNextTimeOfDay();
		nextTime = time + getTimeValue();
		for(TimeChangeListener listener : listeners)
			listener.changed(currentTime);
	}

	private long getTimeValue() {
		return getTimeFor(currentTime);
	}

	private Time getNextTimeOfDay() {
		if (currentTime == Time.DAYTIME)
			return Time.DUSKTIME;
		else if(currentTime == Time.DUSKTIME)
			return Time.NIGHTTIME;
		else if(currentTime == Time.NIGHTTIME)
			return Time.DAWNTIME;
		return Time.DAYTIME;
	}

	public static TimeManager getInstance(){
		return SingletonHolder._instance;
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {
		protected static final TimeManager _instance = new TimeManager();
	}

	public void init() {
		listeners.clear();
		time = 0;
		time2 = 0;
		currentTime = Time.DAWNTIME;
		nextTime = 100;
		listeners.add(new TimeChangeListener(){

			@Override
			public void changed(Time newtime) {
				Log.info("TimeManager", "ToD changed : " + newtime);
			}});
	}

	public static long getNextTime() {
		return nextTime;
	}

	public static long getTimeFor(Time newtime) {
		if (newtime == Time.DAYTIME)
			return DAYTIME;
		else if(newtime == Time.DUSKTIME)
			return DUSKTIME;
		else if(newtime == Time.NIGHTTIME)
			return NIGHTTIME;
		return DAWNTIME;
	}

	public static Time getTime() {
		return currentTime;
	}
}
