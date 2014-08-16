package com.zombie.logic.item;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;

import com.badlogic.gdx.Gdx;
import com.zombie.logic.enums.ItemType;
import com.zombie.util.XMLUtils;

public class Item implements Cloneable,Comparable<Item>, Serializable{

	private static final long serialVersionUID = 1L;
	public static Map<Integer, Item> items = new HashMap<Integer,Item>();
	public static List<Item> shopItems = new ArrayList<Item>();
	public int id = 0;
	public String name = "";
	public String image = "";
	public ItemType type;
	public int wh = 32;
	public int price;
	public int lifetime = 60000;
	public int count = 1;
	public boolean inShop = true;
	
	// drop chance is 1/1000, if value 1000
	public int dropChance = 1000;
	
	public Item clone(){
		try {
			return (Item) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void reload(){
		items.clear();
		shopItems.clear();
		Node first = XMLUtils.getNodeForStream(Gdx.files.internal("data/data/items.xml").read());
		for(int i = 0; i < first.getChildNodes().getLength();i++ ){
			Node n = first.getChildNodes().item(i);
			if (n.getNodeName().equalsIgnoreCase("item")) {
				String type = n.getAttributes().getNamedItem("type").getNodeValue();
				Item it = null;
				if (type.equalsIgnoreCase("weapon"))
					it = Weapon.loadWeapon(n);
				else if (type.equalsIgnoreCase("ammo"))
					it = Ammo.loadAmmo(n);
				else if (type.equalsIgnoreCase("heal"))
					it = Heal.loadHeal(n);
				else if (type.equalsIgnoreCase("explosive"))
					it = Explosive.loadExplosive(n);
				if (it != null)
					loadItem(n, it);
			}
		}
		Collections.sort(shopItems);
		Gdx.app.log("INFO", "Items loaded. Total items: " + items.size());
	}
	
	static void loadItem(Node node, Item item){
		for(int i = 0; i < node.getChildNodes().getLength();i++ ){
			Node n = node.getChildNodes().item(i);
			if (n.getNodeName().equalsIgnoreCase("param")) {
				String name =  n.getAttributes().getNamedItem("name").getNodeValue();
				if (name.equalsIgnoreCase("image"))
					item.image = n.getTextContent();
				else if (name.equalsIgnoreCase("dropChance"))
					item.dropChance = Integer.parseInt(n.getTextContent());
				else if (name.equalsIgnoreCase("wh"))
					item.wh = Integer.parseInt(n.getTextContent());
				else if (name.equalsIgnoreCase("price"))
					item.price = Integer.parseInt(n.getTextContent());
				else if (name.equalsIgnoreCase("inshop"))
					item.inShop = Boolean.parseBoolean(n.getTextContent());
				else if (name.equalsIgnoreCase("lifetime"))
					item.lifetime = Integer.parseInt(n.getTextContent());

			}
		}
		if (item.inShop)
			if (!shopItems.contains(item))
			shopItems.add(item);
	}

	public static Item getItemById(int i) {
		return items.get(i);
	}

	public static Item getAmmoForWeapon(Weapon w) {
		for(int i =0; i < items.size();i++){
			if (items.get(i).type == ItemType.AMMO){
				if (((Ammo)items.get(i)).weaponId == w.id)
					return items.get(i);
			}
		}
		return null;
	}

	@Override
	public int compareTo(Item o) {
		if (type == o.type)
			return 0;
		if (type == ItemType.WEAPON)
			return -1;
		return 1;
	}

	public boolean isStack() {
		if (type == ItemType.WEAPON)
			return false;
		return true;
	}
	
	public boolean isUseable() {
		if (type == ItemType.HEAL || type == ItemType.EXPLOSIVE)
			return true;
		return false;
	}

}
