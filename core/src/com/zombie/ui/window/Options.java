package com.zombie.ui.window;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import java.io.IOException;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.minlog.Log;
import com.zombie.C;
import com.zombie.C.APP;

public class Options extends Window{

	Button apply,back;
	public Slider volume;
	CheckBox sound, vsync, fullScreen;
	SelectBox list;
	Array<DisplayMode> modes = new Array<DisplayMode>();
	
	public Options(String title, Skin skin) {
		super(title, skin);
		init();
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha){
		Log.info("Options", "draw");
		super.draw(batch, parentAlpha);
	}
	
	private void init() {

		for(DisplayMode m : Gdx.graphics.getDisplayModes()){
			if (m.bitsPerPixel < 24)
				continue;
			if (m.width < 800 || m.height < 600)
				continue;
			modes.add(m);
		}
		if (modes.size == 0)
			modes.add(Gdx.graphics.getDesktopDisplayMode());
		list = new SelectBox(modes.toArray(), C.UI.SKIN);
		volume = new Slider(0f, 1f, 0.01f, false, com.zombie.C.UI.SKIN);
		volume.setName("Volume");
		sound = new CheckBox("Sound ON", com.zombie.C.UI.SKIN);
		vsync = new CheckBox("Vsync ON", com.zombie.C.UI.SKIN);
		fullScreen = new CheckBox("fullScreen", com.zombie.C.UI.SKIN);
		
		setMovable(false);
		defaults().space(10);
		add(new Label("Sound",com.zombie.C.UI.SKIN)).colspan(2);
		add(volume).expandY();
		add(sound).expandY();
		row();
		if (Gdx.app.getType() != ApplicationType.Android){
			add(new Label("Graphics",com.zombie.C.UI.SKIN)).colspan(2);
			add(list);
			add(fullScreen);
			row();
		}
		
		add(new Label("Sound",com.zombie.C.UI.SKIN));
		row();
		add(new Label("Control",com.zombie.C.UI.SKIN)).colspan(2);	
		
		row();
		add(apply = new TextButton("Apply", com.zombie.C.UI.SKIN)).center();
		add(back = new TextButton("Back", com.zombie.C.UI.SKIN)).colspan(3).right();
		row();
		pack();
		setVisible(false);
		setPosition(Gdx.graphics.getWidth()/2 - getWidth()/2,
				Gdx.graphics.getHeight() - C.UI.SPACING-getHeight());
		
		apply.addListener(new ClickListener(){
			public void clicked (InputEvent event, float x, float y) {
				APP.SND_ENABLED = sound.isChecked();
				APP.SND_VOL = volume.getValue();
				if (Gdx.app.getType() != ApplicationType.Android){
					APP.FULLSCREEN = fullScreen.isChecked();
					APP.VSYNC = vsync.isChecked();
					APP.DISPLAYMODE = modes.get(list.getSelectionIndex());
					APP.changeDisplayMode();
				}
				addAction(sequence(fadeOut(1, Interpolation.fade), Actions.visible(false)));
				save();
			}
		});
		back.addListener(new ClickListener(){
			public void clicked (InputEvent event, float x, float y) {
				addAction(sequence(fadeOut(1, Interpolation.fade), Actions.visible(false)));
			}
		});
	}

	protected void save() {
		if (Gdx.app.getType() == ApplicationType.Desktop){
			FileHandle file;
			if (!Gdx.files.local("config.ini").exists())
				try {
					Gdx.files.local("config.ini").file().createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			file = Gdx.files.local("config.ini");
			
			file.writeString("", false);
			file.writeString("volume=" + volume.getValue()+"\n", true);
			file.writeString("sound=" + sound.isChecked()+"\n", true);
			file.writeString("vsync=" + vsync.isChecked()+"\n", true);
			file.writeString("fullScreen=" + fullScreen.isChecked()+"\n", true);
			file.writeString("list=" + list.getSelection()+"\n", true);
			file.writeString("profile=" + C.lastProfile, true);
		}
	}
	
	
	@Override
	public void setVisible(boolean visible){
		super.setVisible(visible);
		if (visible){
			volume.setValue(APP.SND_VOL);
			sound.setChecked(APP.SND_ENABLED);
			vsync.setChecked(APP.VSYNC);
			fullScreen.setChecked(APP.FULLSCREEN);
			if (APP.DISPLAYMODE != null)
				list.setSelection(APP.DISPLAYMODE.toString());
		}
	}

}
