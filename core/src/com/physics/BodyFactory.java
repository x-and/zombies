package com.physics;

import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ShortArray;
import com.zombie.C;

public class BodyFactory {

	public static World world;
	
	static PolygonShape poly = new PolygonShape();
	static CircleShape circle = new CircleShape();
	
	static BodyDef bodyDef(){
		BodyDef def = new BodyDef();
		return def;
	}
	
	static BodyDef bodyDef(BodyType type){
		BodyDef def = bodyDef();
		def.type = type;
		return def;
	}
	
	static BodyDef bodyDef(float x,float y, float a,BodyType type){
		BodyDef def = bodyDef(type);
		def.position.set(x,y);
		def.angle = a;
		return def;
	}
	 
	public static Body createNpcBox(float x, float y, float w, float h){
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.angularDamping = 10f;
		bodyDef.linearDamping = 10f;
		bodyDef.position.set(x*C.WORLD_TO_BOX, y*C.WORLD_TO_BOX);
		Body b = world.createBody(bodyDef);
//		PolygonShape shape = new PolygonShape();
		poly.setAsBox(w/2*C.WORLD_TO_BOX, h/2*C.WORLD_TO_BOX);
		b.createFixture(poly, 2);
//		shape.dispose();
		return b;
	}	
	
	public static Body createNpcCircle(float x, float y, float radius){
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.angularDamping = 10f;
		bodyDef.linearDamping = 10f;
		bodyDef.position.set(x*C.WORLD_TO_BOX, y*C.WORLD_TO_BOX);
		Body b = world.createBody(bodyDef);
//		CircleShape shape = new CircleShape();
		circle.setRadius(radius*C.WORLD_TO_BOX);
		b.createFixture(circle, 2);
//		shape.dispose();
		return b;
	}

	public static Fixture addStaticCircle(Body b,float x, float y, float radius) {
		b.setTransform(x*C.WORLD_TO_BOX, y*C.WORLD_TO_BOX, 0);
		circle.setRadius(radius*C.WORLD_TO_BOX);
		circle.setPosition(new Vector2(x*C.WORLD_TO_BOX,y*C.WORLD_TO_BOX));
		return b.createFixture(circle, 0);
	}
	
	public static Fixture addStaticBox(Body b, float x, float y, float w, float h,float angle){
		poly.setAsBox(w/2*C.WORLD_TO_BOX, h/2*C.WORLD_TO_BOX,new Vector2(x*C.WORLD_TO_BOX,y*C.WORLD_TO_BOX), 0);
		return b.createFixture(poly, 0);
	}
	
	public static Body createStaticCircle(float x, float y, float radius) {
		Body b = world.createBody(bodyDef(x*C.WORLD_TO_BOX, y*C.WORLD_TO_BOX,0,BodyType.StaticBody));
		circle.setRadius(radius*C.WORLD_TO_BOX);
		b.createFixture(circle, 0);
		return b;
	}
	
	public static Body createStaticBox(float x, float y, float w, float h){
		return createStaticBox(x,y,w,h,0);
	}
	
	public static Body createEmptyStaticBody(float x, float y){
		Body b = world.createBody(bodyDef(x*C.WORLD_TO_BOX, y*C.WORLD_TO_BOX,0,BodyType.StaticBody));
		return b;
	}

	
	public static Body createStaticBox(float x, float y, float w, float h,float angle){
		Body b = world.createBody(bodyDef(x*C.WORLD_TO_BOX, y*C.WORLD_TO_BOX,0,BodyType.StaticBody));
//		PolygonShape shape = new PolygonShape();
		poly.setAsBox(w/2*C.WORLD_TO_BOX, h/2*C.WORLD_TO_BOX);
		b.createFixture(poly, 0).setFriction(0);
//		shape.dispose();
		return b;
	}	

	public static Body createStaticPolyLine(float[] vertices, float x, float y) {
		Body b = world.createBody(bodyDef(x*C.WORLD_TO_BOX, y*C.WORLD_TO_BOX,0,BodyType.StaticBody));
		ChainShape shape = new ChainShape();
		shape.createChain(worldToBoxVertices(vertices));
		b.createFixture(shape, 0);
		shape.dispose();
		return b;
	}
	
	public static float[] worldToBoxVertices(float[] xy){
		for(int e = 0;e < xy.length;e++)
			xy[e]*=C.WORLD_TO_BOX;
		return xy;
	}
	
	//FIXME poly creates incorrect and sometimes fail
	public static Body createStaticPoly(float[] vertices,float x, float y){
		Body b = world.createBody(bodyDef(x*C.WORLD_TO_BOX, y*C.WORLD_TO_BOX,0,BodyType.StaticBody));
		System.out.println("createStaticPoly   "+ vertices.length);
		Vector2[] vert = new Vector2[vertices.length/2];
		for (int i = 0; i < vert.length;i++){
			vert[i] = new Vector2(vertices[i*2],vertices[i*2+1]);
			vert[i].scl(C.WORLD_TO_BOX);
//			System.out.println(vert[i]);
		}
		PolygonShape shape = new PolygonShape();
		if (vertices.length/2 >= 8 ){
			ShortArray triangles = new EarClippingTriangulator().computeTriangles(vertices);

//			ShortArray triangles = new DelaunayTriangulator().computeTriangles(vertices, true);
			System.out.println(triangles + "   size " + triangles.size);
			int length = triangles.size/3;
			for(int i = 0; i < length;i++){
				shape = new PolygonShape();
				Vector2[] v = new Vector2[3];
				v[0] = vert[triangles.get(i)];
				v[1] = vert[triangles.get(i+1)];
				v[2] = vert[triangles.get(i+2)];
				shape.set(v);
				b.createFixture(shape, 0);
			}
		} else{
			shape = new PolygonShape();
			shape.set(vert);
			b.createFixture(shape, 0);
		}
		shape.dispose();
		return b;
	}

	
	public static Body createBulletBox(float x, float y, float w, float h,float angle,float density, float damping){
		BodyDef bodyDef = bodyDef(x*C.WORLD_TO_BOX, y*C.WORLD_TO_BOX,MathUtils.degRad*angle,BodyType.DynamicBody);
		bodyDef.angularDamping = damping;
		bodyDef.linearDamping = damping;
		bodyDef.bullet = true;
		Body b = world.createBody(bodyDef);
	//	shape = new PolygonShape();
		poly.setAsBox(w/2*C.WORLD_TO_BOX, h/2*C.WORLD_TO_BOX);
		b.createFixture(poly, density).setSensor(true);
//		shape.dispose();
		return b;
	}

	public static Body createDynamicBox(float x, float y, float w, float h,float angle,float density,boolean isSensor, float angularDamping, float linearDamping){
		BodyDef bodyDef = bodyDef(x*C.WORLD_TO_BOX, y*C.WORLD_TO_BOX,MathUtils.degRad*angle,BodyType.DynamicBody);
		bodyDef.angularDamping = angularDamping;
		bodyDef.linearDamping = linearDamping;
		Body b = world.createBody(bodyDef);
		poly.setAsBox(w/2*C.WORLD_TO_BOX, h/2*C.WORLD_TO_BOX);
		b.createFixture(poly, density).setSensor(isSensor);
		return b;
	}
	
	public static Body createDynamicBox(float x, float y, float w, float h,float angle,float density,boolean isSensor, float damping){
		return createDynamicBox(x,y,w,h,angle,density,isSensor,damping,damping);
	}

	public static Body createDynamicCircle(float x, float y, float radius,
			float angle, float density, boolean isSensor, float damping) {

		BodyDef bodyDef = bodyDef(x*C.WORLD_TO_BOX, y*C.WORLD_TO_BOX,MathUtils.degRad*angle,BodyType.DynamicBody);
		bodyDef.angularDamping = damping;
		bodyDef.linearDamping = damping;
		Body b = world.createBody(bodyDef);
//		CircleShape shape = new CircleShape();
		circle.setRadius(radius*C.WORLD_TO_BOX);
		b.createFixture(circle, density).setSensor(isSensor);
//		shape.dispose();
		return b;
	}

	public static Body createStaticCircle(float x, float y, float radius,
			float angle, boolean isSensor, float damping) {

		BodyDef bodyDef = bodyDef(x*C.WORLD_TO_BOX, y*C.WORLD_TO_BOX,MathUtils.degRad*angle,BodyType.StaticBody);
		bodyDef.angularDamping = damping;
		bodyDef.linearDamping = damping;
		Body b = world.createBody(bodyDef);
//		CircleShape shape = new CircleShape();
		circle.setRadius(radius*C.WORLD_TO_BOX);
		b.createFixture(circle, 0).setSensor(isSensor);
//		shape.dispose();
		return b;
	}
	
	
	public static void setupDimension(float w, float h) {
		BodyDef boxDef = bodyDef(BodyType.StaticBody);
		Body box = world.createBody(boxDef);
		PolygonShape ps1 = new PolygonShape();
		PolygonShape ps2 = new PolygonShape();
		PolygonShape ps3 = new PolygonShape();
		PolygonShape ps4 = new PolygonShape();
		ps1.setAsBox(16*C.WORLD_TO_BOX, h*C.WORLD_TO_BOX, new Vector2(-16*C.WORLD_TO_BOX,h/2*C.WORLD_TO_BOX), 0);
		ps2.setAsBox(16*C.WORLD_TO_BOX, h*C.WORLD_TO_BOX, new Vector2((16+w)*C.WORLD_TO_BOX,h/2*C.WORLD_TO_BOX), 0);
		ps3.setAsBox(w*C.WORLD_TO_BOX, 16*C.WORLD_TO_BOX, new Vector2(w/2*C.WORLD_TO_BOX,-16*C.WORLD_TO_BOX), 0);
		ps4.setAsBox(w*C.WORLD_TO_BOX, 16*C.WORLD_TO_BOX, new Vector2(w/2*C.WORLD_TO_BOX,(16+h)*C.WORLD_TO_BOX), 0);
		box.createFixture(ps1, 0.0f).setRestitution(0.5f);
		box.createFixture(ps2, 0.0f).setRestitution(0.5f);
		box.createFixture(ps3, 0.0f).setRestitution(0.5f);
		box.createFixture(ps4, 0.0f).setRestitution(0.5f);
	}
}
