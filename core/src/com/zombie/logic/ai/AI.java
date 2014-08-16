package com.zombie.logic.ai;

import java.util.concurrent.ScheduledFuture;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.manager.ThreadPoolManager;
import com.manager.TimeManager;
import com.path.NavPath;
import com.path.PathFindingRequest;
import com.physics.Physics;
import com.zombie.C;
import com.zombie.logic.GameWorld;
import com.zombie.logic.ai.action.Action;
import com.zombie.logic.ai.action.ActionFollow;
import com.zombie.logic.ai.action.ActionMove;
import com.zombie.logic.ai.action.ActionPath;
import com.zombie.logic.ai.event.Event;
import com.zombie.logic.ai.event.SocialEvent;
import com.zombie.logic.enums.ObjectType;
import com.zombie.logic.object.GameObject;
import com.zombie.logic.object.LiveObject;
import com.zombie.state.GameState;
import com.zombie.util.Rnd;
import com.zombie.util.Utils;

public abstract class AI implements Runnable {

	public int WAIT_TIMER_MIN = 2000;
	public int WAIT_TIMER_MAX = 5000;
	public int MOVE_RND_DST_MIN = 50;
	public int MOVE_RND_DST_MAX = 300;

	public final LiveObject owner;
	private GameObject target,interest;
	public LiveObject friend;
	
	public AIState state = AIState.NO_TARGET;
	
	ScheduledFuture<?> worker;
	public Action action;
	public Action last;
	public boolean updateNeeded = false;
	public UpdateReason reason = null;
	
	public AI(LiveObject owner){
		this.owner = owner;
		worker = ThreadPoolManager.scheduleAtFixedRate(this, 0,C.TIMER_AI);
	}
	
	public void update(){
//		reason.run();
//		updateNeeded = false;
//		reason = null;
	}
	
	@Override
	public void run() {
		if (GameState.getInstance().isUpdatePaused())
			return;
		if (action == null)
			spawned();
		action.checkCondition(this, owner);
		if (action.done){
			actionDone();
		}
	}
	
	// ������� �� �� ��������� �����
	public abstract void onHit(GameObject damager);
	// ������� �� �� ��������
	public abstract void onKill(GameObject killed);
	// ������� �� �� ������
	public abstract void doDie(GameObject killer);
	// ������� �� �� ���������� ��������
	public abstract void actionDone();
	// ������������� ��� ���������
	public abstract void spawned();
	// ��������� ������� �������
	public abstract void handleEvent(Event e);
	//������ ���� �� ���� ���������. ���������� ������������� ��������� �����, ����� ������ ������� � ����.
	public void targetLost() {

	}
	
	protected void setAction(Action next) {
		last = action;
		action = next;
	}
	
	public void remove(){
		if (worker != null && !worker.isCancelled())
			worker.cancel(true);
	}

	boolean pass = false;
	
	protected void randomMove(final float dst){
		UpdateReason r = new UpdateReason(this){

			@Override
			public void run() {
				float angle = Rnd.nextFloat()*360;
				Vector2 destination = null;
				Vector2 start = new Vector2(owner.body.getPosition());
				while(!pass){
					destination = new Vector2(owner.getPos().current);
					destination.x += dst*MathUtils.cos(MathUtils.degRad*angle);
					destination.y += dst*MathUtils.sin(MathUtils.degRad*angle);				
					destination.scl(C.WORLD_TO_BOX);
					pass = true;
					Physics.world.rayCast(new RayCastCallback(){
			
						@Override
						public float reportRayFixture(Fixture fixture, Vector2 point,
								Vector2 normal, float fraction) {
								pass = false;
							return 0;
						}}, start, destination);
					angle-=15;
				}
				pass = false;
				setAction(new ActionMove(destination.scl(C.BOX_TO_WORLD)));
			}};
		setAction(Action.WAIT.copy());
		action.endTime = TimeManager.getLongTime()+1000;
		Physics.task(r);
//		setUpdateReason(r);
	}

	protected boolean losCheck(GameObject target){
		return GameWorld.level.grid.LOSCheck(owner, target);
	}
	
//	GameObject raycastResult;	
//	
//	protected GameObject raycast(float distance) {
//		raycastResult = null;
//		Vector2 start = new Vector2(owner.body.getPosition());
//		Vector2 dest = new Vector2(start);
//		dest.x += C.WORLD_TO_BOX*distance*MathUtils.cos(MathUtils.degRad*owner.getA());
//		dest.y += C.WORLD_TO_BOX*distance*MathUtils.sin(MathUtils.degRad*owner.getA());				
//
//		Physics.world.rayCast(new RayCastCallback(){
//			
//			@Override
//			public float reportRayFixture(Fixture fixture, Vector2 point,
//					Vector2 normal, float fraction) {
//					if (fixture.isSensor())
//						return 1;
//					if (fixture.getBody().getUserData() instanceof GameObject){
//						raycastResult = (GameObject) fixture.getBody().getUserData();
//						return 0;
//					}
//				return 1;
//			}}, start, dest);
//		
//		return raycastResult;
//	}
	
	protected void findPath(GameObject t) {
		setAction(Action.WAIT.copy());
		action.endTime = TimeManager.getLongTime()+1000;
		PathFindingRequest request = new PathFindingRequest(owner, t){

			@Override
			public void searchEnded(NavPath p) {
				action.done = true;
				p = Utils.optimizePath(p);
				setAction(new ActionPath(p,targetObject));

			}};
		request.find();
	}
	
	public void nextPathNode() {
		GameObject target = ((ActionPath) action).target;
		boolean result = losCheck(target);
		if (!result)
			return;
		setAction(new ActionFollow(target));
	}

	protected void setUpdateReason(UpdateReason updateReason) {
		reason = updateReason;
		updateNeeded = true;
		setAction(Action.WAIT.copy());
		action.endTime = TimeManager.getLongTime()+1000;
	}
	
	public GameObject getTarget() {
		return target;
	}

	public void setTarget(GameObject target) {
		this.target = target;
		if (target != null)
			state = AIState.ATTACKING;
		else 
			state = AIState.NO_TARGET;
		
		if (state == AIState.ATTACKING){
			SocialEvent event = new SocialEvent(this);
			event.socialType = SocialEvent.ATTACKING;
			broadcastEvent(event);
		}
	}

	public GameObject getInterest() {
		return interest;
	}

	public void setInterest(GameObject interest) {
//		System.out.println("setInterest "+ interest);
		this.interest = interest;
		if (interest != null)
			state = AIState.INTEREST;
		else 
			state = AIState.NO_TARGET;
		
		if (state == AIState.INTEREST){
			SocialEvent event = new SocialEvent(this);
			event.socialType = SocialEvent.INTEREST;
			broadcastEvent(event);
		}
	}

	public void broadcastEvent(Event event){
		for(GameObject obj : owner.getKnownList().getKnownObjects().values())
			if (obj.type == ObjectType.LIVE){
				if (((LiveObject) obj).getAI().state.isPeaceful())
					((LiveObject) obj).getAI().handleEvent(event);
			}
	}

}
