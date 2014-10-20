package com.zombie.ui.window;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.visible;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.manager.ResourceManager;
import com.zombie.C;

public class QuestDialog extends Table {

	public static QuestDialog dialog = new QuestDialog();
	public Image image;
	public Label name,text;
	
	public QuestDialog() {
		super(C.UI.SKIN);
		setVisible(false);
		resize(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		top();
		debug();
		defaults().pad(4).top();
		setBackground(new TextureRegionDrawable(ResourceManager.getImage("table_back")));
		add("Quest").left().top();
		name = (Label) add("").center().top().expandX().getWidget();
		image = new Image();
		image.setSize(64, 64);
		row();
		add(image).top().left();
		text = (Label) add("").top().left().expand().fill().getWidget();
		text.setWrap(true);	
		text.setAlignment(Align.top, Align.left);
//		text.setWidth(text.getWidth()/2);

	}
	
	public void resize(int w, int h) {
		setSize(w/3,h/6);
		if (isVisible())
			setPosition(0,0);
		else
			setPosition(-getWidth(),0);
	}

	public void hide(){
		addAction(sequence(moveTo(-getWidth(), 0,1),visible(false)));
	}

	public void unhide() {
		addAction(sequence(visible(true),moveTo(0, 0, 1),delay(10),run(new Runnable(){

			@Override
			public void run() {
				hide();
			}})));
	}

	
	public void setText(Drawable drawable, String nameText,String tText){
		if (drawable != null)
			image.setDrawable(drawable);
		name.setText(nameText);
		text.setText(tText);
	}
}
