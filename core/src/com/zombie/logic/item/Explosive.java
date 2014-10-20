package com.zombie.logic.item;

import org.w3c.dom.Node;

import com.zombie.logic.enums.ItemType;

public class Explosive extends Item {

	private static final long serialVersionUID = 1L;
	public float damage;
	public int radius;
	public float velocity;
	public int timer;
	public String condition;
	public boolean blowOnContact;
	public String explodeSound = "explode_0";
	public String explodeParticle = "explode_0";
	public float detectorRadius;
	public boolean beepOnTimer = false;	
	
	public Explosive(){
		type = ItemType.EXPLOSIVE;
	}
	
	public static Item loadExplosive(Node node) {
		Explosive explosive = new Explosive();
		explosive.id = Integer.parseInt(node.getAttributes().getNamedItem("id").getNodeValue());
		explosive.name = node.getAttributes().getNamedItem("name").getNodeValue();
		for(int i = 0; i < node.getChildNodes().getLength();i++ ){
			Node n = node.getChildNodes().item(i);
			if (n.getNodeName().equalsIgnoreCase("param")) {
				String name =  n.getAttributes().getNamedItem("name").getNodeValue();
				if (name.equalsIgnoreCase("radius"))
					explosive.radius = Integer.parseInt(n.getTextContent());
				else if (name.equalsIgnoreCase("damage"))
					explosive.damage = Integer.parseInt(n.getTextContent());
				else if (name.equalsIgnoreCase("velocity"))
					explosive.velocity = Float.parseFloat(n.getTextContent());
				else if (name.equalsIgnoreCase("timer"))
					explosive.timer = Integer.parseInt(n.getTextContent());
				else if (name.equalsIgnoreCase("condition"))
					explosive.condition = n.getTextContent();
				else if (name.equalsIgnoreCase("blowoncontact"))
					explosive.blowOnContact = Boolean.parseBoolean(n.getTextContent());
				else if (name.equalsIgnoreCase("explodesound"))
					explosive.explodeSound = n.getTextContent();	
				else if (name.equalsIgnoreCase("particle_explode"))
					explosive.explodeParticle = n.getTextContent();
				else if (name.equalsIgnoreCase("detectorRadius"))
					explosive.detectorRadius = Float.parseFloat(n.getTextContent());
				else if (name.equalsIgnoreCase("beeponTimer"))
					explosive.beepOnTimer  = Boolean.parseBoolean(n.getTextContent());
			}
		}
		items.put(explosive.id, explosive);
		return explosive;
	}

}
