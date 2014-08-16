package com.manager;

import java.util.HashMap;
import java.util.Map;

import com.zombie.logic.quest.FirstQuest;
import com.zombie.logic.quest.Quest;
/**содержит все квесты уровня. При перезапуске квесты перезагружаются. */
public class QuestManager {

	public static Map<Integer,Quest> quests = new HashMap<Integer,Quest>();
	
	public static void levelLoaded(){
		quests.clear();
		addQuest(new FirstQuest(0,"Training"));
	}

	public static void addQuest(Quest q) {
		quests.put(q.id, q);
		q.init();	
	}
}
