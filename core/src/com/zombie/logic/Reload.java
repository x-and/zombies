package com.zombie.logic;

import com.manager.ResourceManager;
import com.zombie.logic.object.LiveObject;
import com.zombie.state.GameState;
import com.zombie.util.SoundUtils;

public class Reload implements Runnable {

	LiveObject obj;
	
	public Reload(LiveObject object) {
		obj =object;
	}

	@Override
	public void run() {
		if (obj.getWeapon().totalAmmo != 0 && obj.getWeapon().totalAmmo >= obj.getWeapon().maxAmmo){
			obj.getWeapon().ammo = obj.getWeapon().maxAmmo;
			if (obj.getWeapon().totalAmmo != Integer.MAX_VALUE)
				obj.getWeapon().totalAmmo -= obj.getWeapon().maxAmmo;
			SoundUtils.playSound(ResourceManager.getSound(obj.getWeapon().clipinSound),obj.getPos());
			obj.reload = null;
			if (obj == GameState.player)
				GameState.getInstance().ui.weaponChanged(obj.getWeapon());

		}
	}
}