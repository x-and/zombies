package com.zombie.effect;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.manager.ResourceManager;
import com.zombie.C;
import com.zombie.C.UI;
import com.zombie.util.SoundUtils;
import com.zombie.util.Utils;

public class TextEffect extends AbstractEffect{

	public String text ="Text Text Text Text Text Text Text Text Text Text ";
	String shownText = "";
	public long allTextShownTime = 8000L;
	public int symbolShowTime = 50;
	public Color color = Color.RED;
	public boolean soundOn = false;
	TextBounds bounds;
	
	public TextEffect(){
		setFullLifeTime(8000);
		position = new Vector2(50,500);
		renderGroup = C.GROUP_LAST;
	}
	
	public TextEffect(String string) {
		this();
		text = string;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void update(int delta) {
		if (allTextShownTime == 0 && getFullLifeTime() == getLifeTime())
			shownText = text;
		setLifeTime(getLifeTime()-delta);

		if (allTextShownTime != 0 && getFullLifeTime()-getLifeTime() < symbolShowTime*text.length()){
//			System.out.println("Elapsed: " + (getFullLifeTime()-getLifeTime()) + " ; shown symbols: " + shownText.length() + " ; timeFor1Symbol "+ timeToShowChar + " ; shownText: " + shownText);
			if (shownText.length()*symbolShowTime + symbolShowTime < getFullLifeTime()-getLifeTime())
				if (text.length() >= shownText.length()+ 1){
					shownText+=text.substring(shownText.length(),shownText.length()+ 1);
					if (soundOn)
						SoundUtils.playSound(ResourceManager.getSound("click"),Vector2.Zero);
				}
		}
		if (getLifeTime() < 300){
			if (shownText.length()>0)
				shownText = shownText.substring(0, shownText.length()-1);
		}
	}

	@Override
	public void remove() {
		
	}

	@Override
	public void draw(SpriteBatch batch, ShapeRenderer shapeBatch) {
		Utils.pushColor(batch);
		batch.setColor(color);
		bounds = UI.FONT.drawMultiLine(batch, shownText, getX(), getY());
		Utils.popColor(batch);
	}

	public boolean needDraw(Rectangle rect){
		if (bounds == null)
			return true;
		Rectangle.tmp.set(getX(), getY(), bounds.width,bounds.height);
		return rect.overlaps(Rectangle.tmp);
	}

}
