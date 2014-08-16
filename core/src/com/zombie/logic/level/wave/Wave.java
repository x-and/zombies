package com.zombie.logic.level.wave;

import java.util.HashMap;
import java.util.Map;

import com.zombie.logic.level.Level;

public class Wave {

	public Map<Integer, Spawn> spawns = new HashMap<Integer,Spawn>();
	public int id;
	public Level level;
}
