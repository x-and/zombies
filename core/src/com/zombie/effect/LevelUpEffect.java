package com.zombie.effect;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.zombie.C;


public class LevelUpEffect extends AbstractEffect
{
	public static int WIN = 0;
	public static int DRAW = 1;
	public static int DEFEAT = 2;
	public String text;
	public int type = 0;
	boolean up = true;
	float opacity1 = 0.0f;
	
	public LevelUpEffect(){
		renderGroup = C.GROUP_POST_EFFECT;
	}
	
	@Override
	public void update(int delta) {
		setLifeTime(getLifeTime()-delta);
		if (up)	{
			opacity1+=0.01f;
			if (opacity1 > 0.6f)
				up=false;
		} else {
			opacity1-=0.01f;
			if (opacity1 < 0.1f)
				up=true;
		}
	}

//	@Override
//	public void draw(Graphics g) {
//		Color green = new Color(Color.green.getRed(),Color.green.getGreen(),Color.green.getBlue(),opacity1);
//			g.setColor(green);
//		g.fillRect(GameState.offsetX, GameState.offsetY, Constants.SCREEN_WIDTH,Constants.SCREEN_HEIGHT);
//		
//		FontUtils.drawCenter(g.getFont(),"Level Up!", (int)GameState.offsetX, (int)GameState.offsetY+Constants.SCREEN_HEIGHT/2, Constants.SCREEN_WIDTH);
//	}

	@Override
	public void remove() {
		
	}

	@Override
	public void draw(SpriteBatch batch, ShapeRenderer shapeBatch) {
		
	}

	@Override
	public boolean needDraw(Rectangle rect) {
		return false;
	}

}
