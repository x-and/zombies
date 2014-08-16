package com.zombie.logic.item;

import org.w3c.dom.Node;

import com.zombie.logic.enums.ItemType;

public class Ammo extends Item {

	private static final long serialVersionUID = 1L;
	public int ammo;
	public int weaponId;
	
	public Ammo(){
		type = ItemType.AMMO;
	}
	
	public static Item loadAmmo(Node node) {
		Ammo ammo = new Ammo();
		ammo.id = Integer.parseInt(node.getAttributes().getNamedItem("id").getNodeValue());
		ammo.name = node.getAttributes().getNamedItem("name").getNodeValue();
		for(int i = 0; i < node.getChildNodes().getLength();i++ ){
			Node n = node.getChildNodes().item(i);
			if (n.getNodeName().equalsIgnoreCase("param")) {
				String name =  n.getAttributes().getNamedItem("name").getNodeValue();
				if (name.equalsIgnoreCase("ammo"))
					ammo.ammo = Integer.parseInt(n.getTextContent());
				else if (name.equalsIgnoreCase("weaponId"))
					ammo.weaponId = Integer.parseInt(n.getTextContent());
			}
		}
		items.put(ammo.id, ammo);
		return ammo;
	}

}
