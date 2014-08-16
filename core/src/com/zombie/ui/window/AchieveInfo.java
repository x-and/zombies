package com.zombie.ui.window;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.repeat;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.visible;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.manager.ResourceManager;
import com.manager.TimeManager;
import com.zombie.C;
import com.zombie.achieve.Achievement;

public class AchieveInfo extends Table implements Runnable{
	
	Image image;
	Label desc;
	
	public AchieveInfo(){
		super(C.UI.SKIN);
		init();
	}

	private void init() {
//		debug();
		setBackground(new TextureRegionDrawable(ResourceManager.getImage("table_back")));
		setSize(256,100);
		setPosition(Gdx.graphics.getWidth(),400);
		image = new Image(ResourceManager.getImage("achieve"));
		image.setSize(64, 64);
		Table table = new Table(C.UI.SKIN);
		table.left().top();
		add("Achieve unlocked").center().colspan(2).row();
		add(image).expandY().width(64).height(64).padRight(8);
		add(table).left().top().expand().fill();
		desc = (Label) table.add("description:").left().top().expand().fill().getWidget();
		desc.setWrap(true);
		row();
		setVisible(false);
	}
	
	public void hide(){
		addAction(sequence(moveTo(Gdx.graphics.getWidth(), 400, 0.5f,Interpolation.swingIn),visible(false),Actions.run(this)));
	}

	long time = 0;
	int lifeTime = 4000;
	public Array<Achievement> list = new Array<Achievement>();
	
	public void unhide() {
		time = TimeManager.getLongTime();
		setVisible(true);
		addAction(sequence(moveTo(Gdx.graphics.getWidth()-getWidth()-C.UI.SPACING/2, 400, 0.5f,Interpolation.swingOut)));
		addAction(repeat(3,sequence(scaleBy(1.1f,1.1f,0.5f),scaleBy(0.9f,0.9f,0.5f))));
		image.addAction(repeat(3,sequence(scaleBy(0.1f,0.1f,0.2f),scaleBy(-0.1f,-0.1f,0.2f))));
		addAction(sequence(delay(3),Actions.run(new Runnable(){

			@Override
			public void run() {
				hide();
			}})));
	}

	public void init(Achievement ac) {
		image.setDrawable(new TextureRegionDrawable(ResourceManager.getImage(ac.image)));
		desc.setText(ac.desc);
		unhide();
	}

	@Override
	public void run() {
		if (list.size != 0)
			init(list.pop());
	}
	
}
