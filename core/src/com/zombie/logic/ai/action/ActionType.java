package com.zombie.logic.ai.action;

public enum ActionType {
	WAIT, 	//ждать на месте
	STAND,	//стоять, время от времени двигаясь в случайном направлении
	MOVE,	//двигаться
	FOLLOW, //преследовать
//	FOLLOW_FRIEND,	//преследовать дружественный объект
//	FOLLOW_TARGET,	//преследовать цель
	ATTACK,	//атаковать
	FLEE,	//убегать
	PATH	//двигаться по проложенному пути
}
