package com.zombie.logic.object;

import com.zombie.logic.ai.event.Event;

public class Point extends GameObject {

	public Event event;
	
	public Point(float x, float y) {
		super(x,y);
	}

	public Point(Event e) {
		super(e.getX(),e.getY());
		event = e;
	}

}
