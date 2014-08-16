package com.zombie;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.zombie.state.GameState;
import com.zombie.ui.window.Profiles;

public class Profile implements Serializable{

	private static final long serialVersionUID = 1L;

	public static Profile[] profiles;

	public static boolean noProfiles = true;
	public List<Integer> levelsPassed = new ArrayList<Integer>();
	public Save save = new Save();
	public String name;
	
	public static void init(){
		profiles = new Profile[3];
		int i = 0;
		try {	
			for(FileHandle file : Gdx.files.local(".").list(".pro")){
				ObjectInputStream ois = new ObjectInputStream(file.read());
				Profile profile;
					profile = (Profile) ois.readObject();
				profiles[i] = profile;
				ois.close();
				i++;
				noProfiles = false;
				if (C.lastProfile.equalsIgnoreCase(profile.name))
					C.PROFILE = profile;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Profiles.profiles = new Profiles("Profiles",C.UI.SKIN);
		Profiles.profiles.getContentTable().add("Choose your profile, or create one.");
		updateProfilesUI();
	}
	
	public static void updateProfilesUI(){
		Profiles.profiles.getButtonTable().clearChildren();
		for(int i = 0;i < 3;i++){
			if (profiles[i] == null)
				Profiles.profiles.button("Create \n profile", null);
			else{
				if (C.PROFILE != null && C.PROFILE.name.equalsIgnoreCase(profiles[i].name)){
					Profiles.profiles.button("Loaded now \n "+ profiles[i].name, profiles[i]);
				} else
					 Profiles.profiles.button("Load "+ profiles[i].name, profiles[i]);
			}
		}
	}

	public static void create(String text) {
		Profile p = new Profile();
		p.name = text;
		p.save();		
		load(p);


	}

	public void save() {
		save.save(GameState.player);

		try{
			ObjectOutputStream oos = new ObjectOutputStream(Gdx.files.local(name+".pro").write(false));
			oos.writeObject(this);
			oos.flush();
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		init();
	}

	public static void load(Profile object) {
		if (object == null)
			return;

		C.PROFILE = object;
		C.lastProfile = object.name;
		C.APP.save();
		updateProfilesUI();
	}
	
}
