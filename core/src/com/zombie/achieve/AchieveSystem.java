package com.zombie.achieve;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.minlog.Log;
import com.zombie.C;
import com.zombie.Save;
import com.zombie.state.GameState;

public class AchieveSystem {

	public static Map<Class<? extends Achievement>,Array<Achievement>> map;
	public static Array<Achievement> achieved;	
	
	public static void init(){
		map = new HashMap<Class<? extends Achievement>,Array<Achievement>>();
		achieved = new Array<Achievement>();
		addAchieve(new KillAchieve("First kill","achieve","You made your first kill!").set(1).type(AchieveType.Combat));
		addAchieve(new KillAchieve("10 kill","achieve","You made 10 kills!").set(10).type(AchieveType.Combat));
		addAchieve(new KillAchieve("100 kills","achieve","You made 100 kills!").set(100).type(AchieveType.Combat));
		addAchieve(new MoneyAchieve("First money","achieve","You earn your first money!").set(1).type(AchieveType.General));
	}
	
	public static class KillAchieve extends Achievement{
		private static final long serialVersionUID = 1L;
		
		int need;
		
		public KillAchieve(String n,String image, String description) {
			super(n,image, description);
		}

		public Achievement set(int i) {
			need = i;
			return this;
		}

		@Override
		public boolean condition() {
			return GameState.player.getStat().kills >= need;
		}

		@Override
		public String getCondition() {
			return "Kills needed " + need;
		}
	}

	public static class MoneyAchieve extends Achievement{
		private static final long serialVersionUID = 1L;
		
		int need;
		
		public MoneyAchieve(String n,String image, String description) {
			super(n,image, description);
		}

		public Achievement set(int i) {
			need = i;
			return this;
		}

		@Override
		public boolean condition() {
			return GameState.player.getStat().earned >= need;
		}

		@Override
		public String getCondition() {
			return "Earn " + need + " $";
		}
	}	
	
	public static class DamageAchieve extends Achievement{
		private static final long serialVersionUID = 1L;
		
		int need;
		
		public DamageAchieve(String n,String image, String description) {
			super(n,image, description);
		}

		@Override
		public boolean condition() {
			return GameState.player.getStat().damage >= need;
		}

		@Override
		public String getCondition() {
			return null;
		}
	}
	
	static void addAchieve(Achievement ac){
		Class<? extends Achievement> cl = ac.getClass();
		if (!map.containsKey(cl))
			map.put(cl, new Array<Achievement>());
		map.get(cl).add(ac);
	}
	
	public static void onKill(){
		check(map.get(KillAchieve.class));
		check(map.get(MoneyAchieve.class));
	}
	
	static void check(Array<Achievement> array){
		for(Achievement ac : array){
			if (ac.isCompleted)
				continue;
//			System.out.println("ac.name "+ ac.name + "   ac.class " + ac.getClass()  + " achieved.contains() " + achieved.contains(ac, false)) ;

			if (ac.condition() && !achieved.contains(ac, false)){
				ac.isCompleted = true;
				GameState.getInstance().ui.achieve(ac);
				achieved.add(ac);
			}
		}
	}

	public static void load() {
		Save s = C.PROFILE.save;
		if (s.achieved == null)
			return;
		
		achieved = new Array<Achievement>(s.achieved);
		for(Achievement a : s.achieved){
			if (a == null)
				continue;
//			System.out.println("a.name "+ a.name + "   a.class " + a.getClass() );
			for(Achievement b : map.get(a.getClass())){
				if (a.equals(b)){
					b.isCompleted = a.isCompleted;
//					System.out.println("b.isCompleted "+ b.isCompleted );
				}
			}
		}
		Log.info("Achieve System", "Loaded");
	}
	
}
