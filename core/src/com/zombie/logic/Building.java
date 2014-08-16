package com.zombie.logic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.zombie.C;
import com.zombie.Renderable;
import com.zombie.logic.object.Door;
import com.zombie.logic.zone.BuildingZone;

public class Building implements Renderable{

	public Door door;
	public TiledMapTileLayer floor,interier,walls,exterier,roofs;
	public BuildingZone zone;
	public String name;
	public boolean drawInside = false;
	public float outsideAlpha;
	public float insideAlpha;
	Color temp = new Color();
	
	public Building(String name) {
		this.name = name;
	}

	public void drawInterior(SpriteBatch batch,OrthogonalTiledMapRenderer mapRenderer) {
		batch.setColor(1, 1, 1, insideAlpha);
		mapRenderer.renderTileLayer(floor);
		mapRenderer.renderTileLayer(interier);
		mapRenderer.renderTileLayer(walls);
	}
	
	public void drawExterior(SpriteBatch batch,OrthogonalTiledMapRenderer mapRenderer) {
		batch.setColor(temp.set(batch.getColor().r, batch.getColor().g, batch.getColor().b, outsideAlpha));
		mapRenderer.renderTileLayer(exterier);
	}
	
	public void drawRoofs(SpriteBatch batch,OrthogonalTiledMapRenderer mapRenderer) {
		batch.setColor(temp.set(batch.getColor().r, batch.getColor().g, batch.getColor().b, outsideAlpha));
		mapRenderer.renderTileLayer(roofs);
	}
	
	@Override
	public void draw(SpriteBatch batch, ShapeRenderer shapeBatch) {
	}
	
	public void update(float delta){
		if (drawInside){
			outsideAlpha -= delta*2;
			insideAlpha += delta*2;
		} else {
			outsideAlpha+= delta*2;
			insideAlpha-= delta*2;
		}
		insideAlpha = Math.max(0, Math.min(1, insideAlpha));
		outsideAlpha = Math.max(0, Math.min(1, outsideAlpha));
	}
	
	@Override
	public int getRenderGroup() {
		return C.GROUP_NORMAL;
	}
	
	@Override
	public boolean needDraw(Rectangle rect) {
		return true;
	}

	public void onEnter() {
		System.out.println("onEnter");
		drawInside = true;
	}

	public void onExit() {
		System.out.println("onExit");
		drawInside = false;
	}

}
