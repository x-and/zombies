package com.zombie;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;

public class C {
	
	public static final int TILESIZE = 32;

	public static Profile PROFILE;
	public static String lastProfile;
	
	public static final int STATE_MENU = 0;
	public static final int STATE_GAME = 1;
	public static final int STATE_LEVEL_MENU = 6;
	public static final int STATE_HELP = 7;
	public static final int STATE_ABOUT = 8;
	public static final int STATE_LOADING = 9;
	
	public static final int MAX_CHANCE = 100000;
	public static int TRANSITION_TIME = 200;
	
	public static int MAP_WIDTH = 3200;		
	public static int MAP_HEIGHT = 3200;

	public static final int GROUP_PRE_EFFECT = 0;
	public static final int GROUP_PRE_NORMAL = 1;
	public static final int GROUP_NORMAL = 2;
	public static final int GROUP_POST_NORMAL = 3;	
	public static final int GROUP_AFTER_BUILDINGS = 4;
	public static final int GROUP_POST_EFFECT = 5;		
	public static final int GROUP_LAST = 6;	
	public static final int GROUP_COUNT = 7;

	public static final long TIMER_AI = 100;

	/*player start position*/
	public static float START_X;
	public static float START_Y;

	public static final float WORLD_TO_BOX = 0.01f;

	public static final float BOX_TO_WORLD = 100f;

	public static long BLOOD_EFFECT_TIME = 30000;

	public static String lang;


	public static class APP{

		public static boolean FULLSCREEN = false;
		public static float SND_VOL = 0.5f;
		public static boolean SND_ENABLED = true;
		public static boolean DEBUG = false;
		public static boolean VSYNC = true;
		public static DisplayMode DISPLAYMODE;
		
		public static void init() {
			if (Gdx.app.getType() == ApplicationType.Desktop){
				boolean needCreate = false;
				if (Gdx.files.local("config.ini").exists()){
//					needCreate = true;
//					save();
////					Gdx.files.local("config.ini").file().createNewFile();
////					return;
				
					Properties props = new Properties();
					try {
						props.load(Gdx.files.local("config.ini").reader());
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					SND_VOL = Float.parseFloat(props.getProperty("volume", "0.5f"));
					SND_ENABLED = Boolean.parseBoolean(props.getProperty("sound", "true"));
					FULLSCREEN = Boolean.parseBoolean(props.getProperty("fullScreen", "false"));
					VSYNC = Boolean.parseBoolean(props.getProperty("vsync", "true"));
					lastProfile = props.getProperty("profile", "");
					String mode = props.getProperty("list", "false");
					for (DisplayMode m : Gdx.graphics.getDisplayModes()){
						if (m.toString().equalsIgnoreCase(mode))
							DISPLAYMODE = m;
					}
					
				}

				if (DISPLAYMODE == null)
					DISPLAYMODE = Gdx.graphics.getDesktopDisplayMode();
				
				if (!Gdx.files.local("config.ini").exists())
					save();

				Gdx.graphics.setDisplayMode(DISPLAYMODE.width,DISPLAYMODE.height,FULLSCREEN);
				Gdx.graphics.setVSync(VSYNC);
				
			}
		}

		public static void save() {
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
				file.writeString("volume=" + SND_VOL+"\n", true);
				file.writeString("sound=" + SND_ENABLED+"\n", true);
				file.writeString("vsync=" + VSYNC+"\n", true);
				file.writeString("fullScreen=" + FULLSCREEN+"\n", true);
				file.writeString("list=" + DISPLAYMODE.toString()+"\n", true);
				file.writeString("profile=" + C.lastProfile, true);
			}
		}
		
		
		public static void changeDisplayMode() {
			if (Gdx.app.getGraphics().supportsDisplayModeChange()){
				Gdx.graphics.setDisplayMode(DISPLAYMODE.width, DISPLAYMODE.height, FULLSCREEN);
			}
		}
	}
	
	public static class UI{
		
		public static float BTN_BACK_HEIGHT = 32;
		public static float BTN_BACK_WIDTH = 128;

		public static float PHOTO_W = 100;
		public static float PHOTO_H = 100;
		public static float SPACING = 16f;
		public static float MENU_BTN_W = 80f;
		public static float MENU_BTN_H = 30f;
		public static Skin SKIN;
		public static BitmapFont FONT;
		public static BitmapFont FONT_MINI;
		public static BitmapFont FONT_BIG;
		
		public static void init() {
			System.out.println(Gdx.files.internal(".").file().getAbsolutePath());
			UI.SKIN = new Skin(Gdx.files.internal("data"+File.separator+"ui"+File.separator+"uiskin.json"));

			FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("data"+File.separator+"ui"+File.separator+"font.ttf2"));
			
			BitmapFont font12 = generator.generateFont(12);
			BitmapFont font15 = generator.generateFont(14);
			BitmapFont font20 = generator.generateFont(20);
			UI.SKIN.get(TextButtonStyle.class).font = font15;
			UI.SKIN.get(LabelStyle.class).font = font15;
			UI.SKIN.add("default-font", font15);
			UI.SKIN.add("small-font", font12);
			UI.FONT = font15;
			UI.FONT_MINI = font12;
			UI.FONT_BIG = font20;		
			generator.dispose();
		}
	}
}
