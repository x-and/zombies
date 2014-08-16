package com.zombie.logic;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Node;

import com.badlogic.gdx.Gdx;
import com.manager.ResourceManager;
import com.zombie.SoundInfo;
import com.zombie.util.Rnd;
import com.zombie.util.XMLUtils;

public class Material {

	static Map<String,Material> materials = new HashMap<String,Material>();
	
	public String name;
	public String sound;
	public String dieSound;
	public int soundCount;
	
	public static void reload(){
		Node first = XMLUtils.getNodeForStream(Gdx.files.internal("data/data/materials.xml").read());
		for(int i = 0; i < first.getChildNodes().getLength();i++ ){
			Node n = first.getChildNodes().item(i);
			if (n.getNodeName().equalsIgnoreCase("material")) {
				loadMaterial(n);
			}
		}
		Gdx.app.log("INFO", "Materials loaded. Total materials: " + materials.size());

	}
	
	private static void loadMaterial(Node n) {
		Material mat = new Material();
		mat.name = n.getAttributes().getNamedItem("name").getNodeValue();
		mat.sound = n.getAttributes().getNamedItem("sound").getNodeValue();
		mat.soundCount = Integer.parseInt(n.getAttributes().getNamedItem("soundcount").getNodeValue());
		mat.dieSound = n.getAttributes().getNamedItem("diesound").getNodeValue();
		materials.put(mat.name, mat);
	}

	public static Material get(String name){
		return materials.get(name);
	}

	public SoundInfo getSound() {
		return ResourceManager.getSound(sound+Rnd.nextInt(soundCount));
	}
}
