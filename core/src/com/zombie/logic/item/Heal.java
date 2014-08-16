package com.zombie.logic.item;

import org.w3c.dom.Node;

import com.zombie.logic.enums.ItemType;

public class Heal extends Item {

	private static final long serialVersionUID = 1L;
	public int heal;
	
	public Heal(){
		type = ItemType.HEAL;
	}
	
	public static Item loadHeal(Node node) {
		Heal heal = new Heal();
		heal.id = Integer.parseInt(node.getAttributes().getNamedItem("id").getNodeValue());
		heal.name = node.getAttributes().getNamedItem("name").getNodeValue();
		for(int i = 0; i < node.getChildNodes().getLength();i++ ){
			Node n = node.getChildNodes().item(i);
			if (n.getNodeName().equalsIgnoreCase("param")) {
				String name =  n.getAttributes().getNamedItem("name").getNodeValue();
				if (name.equalsIgnoreCase("amount"))
					heal.heal = Integer.parseInt(n.getTextContent());
			}
		}
		items.put(heal.id, heal);
		return heal;
	}
}
