package com.zombie.ui.window;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.zombie.C;
import com.zombie.Profile;
import com.zombie.state.MenuState;

public class Profiles extends Dialog {

	public static Profiles profiles;
	
	public Profiles(String title, Skin skin) {
		super(title, skin);
		getCell(getButtonTable()).expandX().fill();
		getButtonTable().defaults().center();
		getButtonTable().defaults().expandX().pad(8);
		getButtonTable().debug();
	}

	protected void result (Object object) {
		if (object == null){
			Dialog d = new Dialog("enter your name", C.UI.SKIN){
				protected void result (Object object) {
					if (object instanceof TextField){
						Profile.create(((TextField) object).getText());
					}
				}
			};
			TextField field = new TextField("", C.UI.SKIN);
			d.getContentTable().add(field).left();
			d.button("Ok", field);
			d.show(MenuState.getInstance());
		} else if (object instanceof Profile){
			Profile.load((Profile)object);
			
		}
	}
	
}
