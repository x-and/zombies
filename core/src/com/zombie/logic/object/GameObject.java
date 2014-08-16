package com.zombie.logic.object;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.manager.IdManager;
import com.manager.ResourceManager;
import com.manager.TimeManager;
import com.zombie.C;
import com.zombie.Renderable;
import com.zombie.logic.Faction;
import com.zombie.logic.GameWorld;
import com.zombie.logic.Material;
import com.zombie.logic.Position;
import com.zombie.logic.enums.AnimType;
import com.zombie.logic.enums.ObjectType;
import com.zombie.logic.item.Item;
import com.zombie.logic.knownlist.NullKnownList;
import com.zombie.logic.knownlist.ObjectKnownList;
import com.zombie.logic.object.stat.BasicStat;
import com.zombie.logic.quest.Quest;
import com.zombie.util.Rnd;

public abstract class GameObject implements Renderable,Disposable{

	public ObjectType type = ObjectType.ETC;
	private AnimType animation = AnimType.STAND;
	public LiveObject owner;
	private TextureRegion texture;
	
	public Faction faction = Faction.NONE; 
	
	protected Position pos = new Position();
	
	public String material;
	public String image = "";
	public String name = "";
	public int renderGroup = C.GROUP_NORMAL;

	long birthTime = 0;
	private float velocity;
	public boolean wait = false;
	boolean removed = false;
	protected long lastMoveTimer = TimeManager.getLongTime();
	protected ObjectKnownList knownlist;
	protected BasicStat stats;
	protected Array<Quest> quests;
	protected Array<Item> drop;// = new Array<Item>();
	public int objectID;
	
	public GameObject(float x,float y){
		this(IdManager.getNextId());
		setPosition(x,y);
	}
	
	public GameObject(int oID) {
		this();
		objectID = oID;
	}

	public GameObject() {
		birthTime = TimeManager.getLongTime();
	}

	private void setPosition(float x, float y) {
		setPosition(x,y,0);
	}

	private void setPosition(float x, float y, float a) {
		pos.set(x, y, a);
	}

	public void remove() {
		GameWorld.removeObject(this);
		removed = true;
	}

	public void update(float delta){
		pos.updatePosition();
		if (pos.moved())
			lastMoveTimer = TimeManager.getLongTime();
	}

	public boolean isDead(){
		return removed;
	}
	
	public void draw(SpriteBatch batch, ShapeRenderer shapeBatch) {
		if (getTexture() == null)
			if (!image.isEmpty())
				setTexture(ResourceManager.getImage(image));
			else return;
		batch.draw(getTexture(), getX()-getW()/2, getY()-getH()/2, getW()/2, getH()/2, getW(), getH(), 1, 1, getA(), true);
	}

	public float getVelocity() {
		return velocity;
	}

	public void setVelocity(float velocity) {
		this.velocity = velocity;
	}
	

	public float distanceToObject(GameObject obj) {
		return pos.dst(obj);
	}
	

	public float getAngleToObject(GameObject obj) {
		return MathUtils.radDeg*MathUtils.atan2(obj.getY()-getY(), obj.getX()-getX());
	}
	
	public Material getMaterial(){
		return Material.get(material);
	}

	public int getRenderGroup(){
		return renderGroup;
	}
	
	public boolean needDraw(Rectangle rect){
		Rectangle.tmp.set(getX()-getW()/2, getY()-getH()/2, getW()*2, getH()*2);
		return rect.overlaps(Rectangle.tmp);
	}
	
	public boolean intersects(GameObject object) {
		return intersects(object.getX()-object.getW()/2,object.getY()-object.getH()/2,object.getW(),object.getH());
	}
	
	public boolean intersects(float x, float y, float w, float h) {
		if ((x > getX() + getW()) || (x + w < getX()))
			return false;
		if ((y > getY() + getH()) || (y + h < getY()))
			return false;
		return true;
	}

	public AnimType getAnimation() {
		return animation;
	}

	public void setAnimation(AnimType animation) {
		this.animation = animation;
	}
	
	public void push(PhysicObject obj,int damage) {
		float angle = getAngleToObject(obj);
		float x1 = getVelocity()/2*MathUtils.cos(MathUtils.degRad*angle);
		float y1 = getVelocity()/2*MathUtils.sin(MathUtils.degRad*angle);
		obj.body.applyForceToCenter(x1,y1,true);
	}

	public ObjectKnownList getKnownList() {
		if (knownlist == null)
			knownlist = new NullKnownList(this);
		return knownlist;
	}

	public TextureRegion getTexture() {
		return texture;
	}

	public void setTexture(TextureRegion texture) {
		this.texture = texture;
	}

	public BasicStat getStat() {
		return stats;
	}
	
	public float minDistance(GameObject other){
		return (getW()+getH())/4 + (other.getW()+other.getH())/4 ;
	}
	
	public float getX(){
		return pos.getX();
	}
	
	public float getY(){
		return pos.getY();
	}
	
	public float getA(){
		return pos.getA();
	}
	
	public float getH(){
		return pos.getH();
	}
	
	public float getW(){
		return pos.getW();
	}
	
	public void registerQuest(Quest q){
		if (quests == null)
			quests = new Array<Quest>();
		quests.add(q);
	}

	public void addDrop(Item item){
		if (drop == null)
			drop = new Array<Item>();
		if (!drop.contains(item, false))
			drop.add(item);
	}
	
	protected void doDrop() {
		if (drop == null)
			return;
		for(Item it : drop){
			ItemObject item = new ItemObject(getX()+Rnd.randomInt(-32, 32),getY()+Rnd.randomInt(-32, 32), it);
			GameWorld.addObject(item);
		}
		drop.clear();
		drop= null;
	}

	public boolean hasQuest(int id) {
		if (quests == null)
			return false;
		for(Quest q : quests)
			if (q.id == id)
				return true;
		return false;
	}

	public void removeQuest(Quest q) {
		if (quests != null)
			quests.removeValue(q, true);
	}

	public Position getPos() {
		return pos;
	}

	public void setSize(float x, float y) {
		pos.setSize(x,y);
	}
	
	public void setW(float x) {
		pos.setW(x);
	}
	
	public void setH(float y) {
		pos.setH(y);
	}
	
	public void setA(float a) {
		pos.setAngle(a);
	}
	
	public void dispose() {
	}

	public float dst(GameObject object) {
		return pos.dst(object);
	}

	public float getOldX() {
		return pos.getOldX();
	}
	
	public float getOldY() {
		return pos.getOldY();
	}
	
	
//	public boolean equals(Object other){
//		if (other instanceof GameObject)
//			return other.ha
//	}
	
}
