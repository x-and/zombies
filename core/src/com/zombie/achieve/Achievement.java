package com.zombie.achieve;

import java.io.Serializable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public abstract class Achievement implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public String image,desc;
	public boolean isCompleted = false;
	AchieveType type = AchieveType.General;
	public String name;
	
	public Achievement(String n,String image, String description){
		name = n;
		this.image = image;
		desc = description;
	}

	public Achievement(String n,String image, String description,AchieveType type){
		this(n,image,description);
		this.type = type;
	}
	
	public Achievement type(AchieveType t){
		type = t;
		return this;
	}
	
	public abstract boolean condition();

	public void setNameColor(Label name) {
		switch(type){
			case Combat:
				name.setColor(Color.RED);
				break;
			case Peace:
				name.setColor(Color.GREEN);
				break;
			case General:
				name.setColor(Color.BLUE);
				break;
		}
		
	}
	
	@Override
	public boolean equals(Object obj){
		if (obj instanceof Achievement){
			return name.equalsIgnoreCase(((Achievement) obj).name);
		}
		return super.equals(obj);
	}
	
	public abstract String getCondition();
	
}
