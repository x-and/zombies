package com.zombie.logic.ai;

import com.manager.TimeManager;
import com.zombie.C;
import com.zombie.logic.ai.action.Action;
import com.zombie.logic.ai.action.ActionAttack;
import com.zombie.logic.ai.action.ActionFollow;
import com.zombie.logic.ai.action.ActionMove;
import com.zombie.logic.ai.event.Event;
import com.zombie.logic.ai.event.Event.EventType;
import com.zombie.logic.ai.event.SocialEvent;
import com.zombie.logic.ai.event.SoundEvent;
import com.zombie.logic.enums.ObjectType;
import com.zombie.logic.object.GameObject;
import com.zombie.logic.object.LiveObject;
import com.zombie.logic.object.Point;
import com.zombie.util.Rnd;

public class MeleeAI extends FleeAI {

	public MeleeAI(LiveObject owner) {
		super(owner);
		percentHp = 0;
	}

	@Override
	public void onHit(GameObject damager) {
		super.onHit(damager);
//		if (getTarget() == null)
		if (damager.type == ObjectType.LIVE && damager.faction == owner.faction)
			return;

			setTarget(damager);
//		if (action.type == ActionType.WAIT || action.type == ActionType.MOVE)
			actionDone();
	}
	
	// ���� ��� ���� - ��������� �������, ���� �������� ���� ��� - �������� ������.
	// ���� ���� ���� - ��������� ����������, � �������, ��� �������� � ����
	@Override
	public void actionDone() {
		// ��� ���� - �������� ������
		if (getTarget() == null){
			if (getInterest() == null)
				super.actionDone();
			else {
				if (getInterest().dst(owner) <= C.TILESIZE*4){
					setAction(Action.WAIT.copy().setTime(TimeManager.getLongTime()+100));
					setInterest(null);
				} else {
					if (losCheck(getInterest()))
						setAction(new ActionMove(getInterest().getPos().current).setDistance(C.TILESIZE*4).setTime(TimeManager.getLongTime()+1000));
					else
						findPath(getInterest());
				}
			}
		} else {
			if (getTarget().isDead()){
				setTarget(null);
//				action.done = true;
				actionDone();
				return;
			}
			//FIXME for bigger objects
//			if (raycast(C.TILESIZE) == getTarget())
			if (getTarget().dst(owner) <= owner.minDistance(getTarget()))
				setAction(new ActionAttack(getTarget()));
			else {
				if (getTarget().type == ObjectType.LIVE)
					if (((LiveObject) getTarget()).inVehicle()){
						setTarget(((LiveObject)getTarget()).vehicle);
						actionDone();
						return;
					}
				if (losCheck(getTarget()))
					setAction(new ActionFollow(getTarget()));
				else
					findPath(getTarget());
			}
		}
	}


	@Override
	public void handleEvent(Event e) {
		if (e.type == EventType.SOUND){
			SoundEvent ev = (SoundEvent) e;
			// �� �������� �������� �� ����� �� ��������, ������� ��� �����
			if (ev.owner != null)
				if (ev.owner.type == ObjectType.LIVE){
					LiveObject obj  = (LiveObject) ev.owner;
					if (obj.faction == owner.faction)
						return;
				}
			if (Rnd.nextInt(100) <= 70)
				return;
			Point p = new Point(e);
			setInterest(p);
//			action.done = true;
			actionDone();
		} else if (e.type == EventType.SOCIAL){
			SocialEvent ev = (SocialEvent) e;
			if (ev.socialType == SocialEvent.ATTACKING){
				if (Rnd.nextInt(100) >= 70)
					return;
				setInterest(ev.owner);
			}
			
		}
	}
	
	@Override
	public void onKill(GameObject killed) {
		if (getTarget() == killed){
			setTarget(null);
			action.done = true;
		}
	}
	
	public void run(){
		super.run();
		
		if (state == AIState.FLEING || state == AIState.ATTACKING)
			return;
		
		for (LiveObject obj : owner.getKnownList().getVisibleObjects().values()){
			//TODO fractions check
			if (obj.faction != owner.faction){
//			if (obj instanceof Player){
				if (obj.inVehicle())
					setTarget(obj.vehicle);
				else
					setTarget(obj);
//				action.done = true;
				actionDone();
				return;
			}
		}
	}
	
	public String toString(){
		return "AI state: "+ state.toString() + " action: "+ action + " target: "+ getTarget()+ " interest: "+ getInterest();
	}
}
