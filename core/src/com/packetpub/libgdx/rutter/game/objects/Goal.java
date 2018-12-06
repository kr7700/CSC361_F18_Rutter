package com.packetpub.libgdx.rutter.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.packetpub.libgdx.rutter.game.Assets;

/**
 * Contains information for the goal, which ends the level
 * 
 * @author Kevin Rutter
 */
public class Goal extends AbstractGameObject
{
	private TextureRegion regGoal;

	/**
	 * Constructor for the goal object.
	 */
	public Goal()
	{
		init();
	}

	/**
	 * Initialize the goal object by setting it's width/height, getting its
	 * asset image, setting its bound.
	 */
	private void init()
	{
		dimension.set(2f, 2f);
		regGoal = Assets.instance.goal.goal;
		
		// Set bounding box for collision detection
		//bounds.set(0, 0, dimension.x, dimension.y);
	}
	
	
	/**
	 * Renders the goal
	 * 
	 * @param batch
	 *            SpriteBatch being used to draw the bullet.
	 */
	public void render(SpriteBatch batch)
	{
		TextureRegion reg = regGoal;
		batch.draw(reg.getTexture(), position.x, position.y, origin.x, origin.y, dimension.x, dimension.y, scale.x,
				scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(),
				false, false);
	}
	
	/**
	 * Returns name of object
	 */
	public String toString()
	{
		return "goal";
	}
}