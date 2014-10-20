package com.zombie.logic.quest;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.zombie.logic.item.Item;
import com.zombie.logic.object.GameObject;
import com.zombie.logic.object.LiveObject;
import com.zombie.logic.zone.Zone;
import com.zombie.ui.window.QuestDialog;

//����� �������� ������ - ����������� � ���������. ����� ����� ����� ����� �������� ��� ������ ��� �������.
public abstract class Quest {

	public String name;
	public int id;
	public int stateId = 0;
	Array<GameObject> registered = new Array<GameObject>();
	
	public Quest(int questId, String n){
		id = questId;
		name = n;
	}
	
	public abstract void init();
	
	public abstract void finish();

	public abstract boolean isFinished();
	
	public abstract void stateChanged();
	
	public abstract void onKill(GameObject killed);
	
	public abstract void onSpawn(LiveObject spawned);
	
	public abstract void onHit(int damage, GameObject hitted);
	
	public abstract void onPickup(Item item);
	
	public abstract void onZoneEnter(Zone zone);
	
	public abstract void onZoneExited(Zone zone);

	protected void showText(Drawable image, String title, String text){
		QuestDialog.dialog.setText(image, title, text);
		QuestDialog.dialog.unhide();
	}
	
	protected void unregister(){
		for(GameObject obj : registered)
			obj.removeQuest(this);
	}
}
