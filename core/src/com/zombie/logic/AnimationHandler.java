package com.zombie.logic;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation2D;
import com.manager.ResourceManager;

public class AnimationHandler {

	public Animation2D[] move;
	public Animation2D stand;
	public Animation2D die;
	public Animation2D[] melee;
	public Map<String,Animation2D> animations;
	public AnimationHandler(String image){
		move = new Animation2D[3];
		//walk
		move[0] = ResourceManager.getAnimation(image+"_move").copy(ResourceManager.getAnimation(image+"_move").frameDuration*2);
		move[0].setPlayMode(Animation.LOOP);
		//run
		move[1] = ResourceManager.getAnimation(image+"_move");
		//shift
		move[2] = ResourceManager.getAnimation(image+"_move").copy(ResourceManager.getAnimation(image+"_move").frameDuration*0.75f);		
		move[2].setPlayMode(Animation.LOOP);
		//stand
		stand = ResourceManager.getAnimation(image+"_stand");
		//die
		die = ResourceManager.getAnimation(image+"_die").copy();
		die.setPlayMode(Animation2D.NORMAL);
		
		int count = 2;
		while(ResourceManager.getAnimation(image+"_melee"+count) != null)
			count++;
		melee = new Animation2D[count];
		
		for(int i = 0; i < count;i++){
			melee[i] = ResourceManager.getAnimation(image+"_melee"+i);
			melee[i].setPlayMode(Animation.NORMAL);
		}
	}
	
	public void loadAdditionalAnimations(String image){
		animations = new HashMap<String,Animation2D>();
		String[] names = {"_onehand","_twohand","_move_onehand","_move_twohand","_rocket"};
		
		for(String s : names){
			Animation2D anim = ResourceManager.getAnimation(image+s);
			if (anim == null)
				continue;
			animations.put(s.substring(1), anim);
		}
	}
	
}
