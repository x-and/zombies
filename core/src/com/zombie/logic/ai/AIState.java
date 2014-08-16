package com.zombie.logic.ai;

public enum AIState {

	NO_TARGET, 	//нет цели
	FLEING,		//убегает
	ATTACKING,	//атакует
	INTEREST; 	//проявляет интерес (к звуку\соседнему нпц, который сменил статус)
	
	
	public boolean isPeaceful(){
		if (this ==NO_TARGET || this == INTEREST)
			return true;
		return false;
	}
}
