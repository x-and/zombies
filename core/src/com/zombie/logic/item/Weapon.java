package com.zombie.logic.item;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Node;

import com.zombie.logic.enums.ItemType;
import com.zombie.util.Rnd;

public class Weapon extends Item{

	private static final long serialVersionUID = 1L;

	public static Map<Integer, Weapon> weapons = new HashMap<Integer,Weapon>();

	public int damage = 30;
	public float rndAngle = 4;
	public int hitTime = 430;
	public float bulletSpeed = 0.5f;
	public int reloadTime = 2000;
	public int ammo = 12;
	public int maxAmmo = 12;	
	public int totalAmmo = 0;	
	public String particle_shot;
	public String particle_smoke;	
	public String shootSound = "shoot_0";
	public String missSound = "shoot_0";
	int shootSoundCount = 1;
	int missSoundCount = 1;
	public String clipoutSound = "clipout_0";
	public String clipinSound = "clipout_0";
	public boolean strongBullet = false;
	public boolean twoHanded = false;
	public WeaponType weaponType = WeaponType.STANDART;
	public Bullet bullet;
	
	public Weapon(){
		type = ItemType.WEAPON;
	}
	

	
	public static Weapon getWeaponById(int itemId) {
		return (Weapon) Item.items.get(itemId);
	}

	public void setMaxAmmo(int i) {
		maxAmmo = i;
		ammo = i;
	}
	
	public static class Bullet implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public int weaponId;
		public float density;
		public float damping;
		public float velocity;
		public String image = "";
		public int width;
		public int height;
		public int explosiveId;
	}

	public static Item loadWeapon(Node node) {
		Weapon weapon = new Weapon();
		weapon.id = Integer.parseInt(node.getAttributes().getNamedItem("id").getNodeValue());
		weapon.name = node.getAttributes().getNamedItem("name").getNodeValue();
		for(int i = 0; i < node.getChildNodes().getLength();i++ ){
			Node n = node.getChildNodes().item(i);
			if (n.getNodeName().equalsIgnoreCase("param")) {
				String name =  n.getAttributes().getNamedItem("name").getNodeValue();
				if (name.equalsIgnoreCase("damage"))
					weapon.damage = Integer.parseInt(n.getTextContent());
				else if (name.equalsIgnoreCase("rndAngle"))
					weapon.rndAngle = Integer.parseInt(n.getTextContent());
				else if (name.equalsIgnoreCase("hitTime"))
					weapon.hitTime = Integer.parseInt(n.getTextContent());
				else if (name.equalsIgnoreCase("reloadTime"))
					weapon.reloadTime = Integer.parseInt(n.getTextContent());
				else if (name.equalsIgnoreCase("maxAmmo"))
					weapon.setMaxAmmo(Integer.parseInt(n.getTextContent()));
				else if (name.equalsIgnoreCase("shootsound")){
					if (n.getAttributes().getNamedItem("count") != null)
						weapon.shootSoundCount = Integer.parseInt(n.getAttributes().getNamedItem("count").getNodeValue());
					weapon.shootSound = n.getTextContent();
				} else if (name.equalsIgnoreCase("misssound")){
					if (n.getAttributes().getNamedItem("count") != null)
						weapon.missSoundCount = Integer.parseInt(n.getAttributes().getNamedItem("count").getNodeValue());
					weapon.missSound = n.getTextContent();
				} else if (name.equalsIgnoreCase("clipoutsound"))
					weapon.clipoutSound = n.getTextContent();
				else if (name.equalsIgnoreCase("clipinsound"))
					weapon.clipinSound = n.getTextContent();
				else if (name.equalsIgnoreCase("twoHanded"))
					weapon.twoHanded = Boolean.parseBoolean(n.getTextContent());
				else if (name.equalsIgnoreCase("strongBullet"))
					weapon.strongBullet = Boolean.parseBoolean(n.getTextContent());
				else if (name.equalsIgnoreCase("weapontype"))
					weapon.weaponType = WeaponType.forString(n.getTextContent());
				else if (name.equalsIgnoreCase("particle_shot"))
					weapon.particle_shot = n.getTextContent();
				else if (name.equalsIgnoreCase("particle_shot2"))
					weapon.particle_smoke = n.getTextContent();
			} else if (n.getNodeName().equalsIgnoreCase("bullet")){
				loadBullet(n,(Weapon)weapon);
			}
		}
		items.put(weapon.id, weapon);
		Weapon.weapons.put(weapon.id, weapon);
		return weapon;
	}

	private static void loadBullet(Node node, Weapon weapon) {
		Bullet b = weapon.bullet = new Bullet();
		for(int i = 0; i < node.getChildNodes().getLength();i++ ){
			Node n = node.getChildNodes().item(i);
			if (n.getNodeName().equalsIgnoreCase("param")) {
				String name =  n.getAttributes().getNamedItem("name").getNodeValue();
				if (name.equalsIgnoreCase("density"))
					b.density = Float.parseFloat(n.getTextContent());
				else if (name.equalsIgnoreCase("damping"))
					b.damping = Float.parseFloat(n.getTextContent());
				else if (name.equalsIgnoreCase("velocity"))
					b.velocity = Float.parseFloat(n.getTextContent());
				else if (name.equalsIgnoreCase("width"))
					b.width = Integer.parseInt(n.getTextContent());
				else if (name.equalsIgnoreCase("height"))
					b.height = Integer.parseInt(n.getTextContent());
				else if (name.equalsIgnoreCase("image"))
					b.image = n.getTextContent();
				else if (name.equalsIgnoreCase("explosiveId"))
					b.explosiveId = Integer.parseInt(n.getTextContent());			
			}
		}
	}
	
	public Weapon clone(){
		Weapon cloned = (Weapon) super.clone();
		cloned.bullet = bullet;
		return cloned;
	}

	public String getMissSound() {
		if (missSoundCount == 1)
			return missSound;
		return missSound+Rnd.nextInt(missSoundCount);
	}
	
	public String getShootSound() {
		if (shootSoundCount == 1)
			return shootSound;
		return shootSound+Rnd.nextInt(shootSoundCount);
	}
}
