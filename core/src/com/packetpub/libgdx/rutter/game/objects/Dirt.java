package com.packetpub.libgdx.rutter.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.packetpub.libgdx.rutter.game.Assets;


/**
 * Contains the information for creating and modifying the dirt object.
 * (the platforms the riceball jumps around on)
 * @author Kevin Rutter
 */
public class Dirt extends AbstractGameObject
{
	private TextureRegion regEdge;
	private TextureRegion regMiddle;
	
	private int length;
	
	/**
	 * Constructor for dirt object.
	 */
	public Dirt()
	{
		init();
	}
	
	/**
	 * Initialize the dirt object by setting its dimensions, width, and height.
	 */
	private void init()
	{
		dimension.set(1, 1.5f);
		
		regEdge = Assets.instance.dirt.edge;
		regMiddle = Assets.instance.dirt.middle;
		
		// starting length of this dirt object
		setLength(1);		
	}
	
	/**
	 * Set the starting length of a dirt object.
	 * @param length	How wide (in meters) the dirt platform will be.
	 */
	public void setLength(int length)
	{
		this.length = length;
	}
	
	/**
	 * Increase the length of a dirt platform.
	 * @param amount	The amount of meters to increase the dirt's length by.
	 */
	public void increaseLength(int amount)
	{
		setLength(length + amount);
	}
	
	/**
	 * Renders the dirt object, by drawing the left edge, a certain amount of
	 * middle edges, and then the right edge as a mirrored left edge.
	 * @param batch		SpriteBatch being used to draw the dirt.
	 */
	@Override
	public void render(SpriteBatch batch)
	{
		TextureRegion reg = null;
		
		float relX = 0;
		float relY = 0;
		
		// Draw left edge
		reg = regEdge;
		relX -= dimension.x / 4;
		batch.draw(reg.getTexture(), position.x + relX, position.y + relY,
				origin.x, origin.y, dimension.x / 4, dimension.y,
				scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(),
				reg.getRegionWidth(), reg.getRegionHeight(), false, false);
		
		// Draw middle
		relX = 0;
		reg = regMiddle;
		for (int i = 0; i < length; i++)
		{
			batch.draw(reg.getTexture(), position.x + relX, position.y + relY,
					origin.x, origin.y, dimension.x, dimension.y,
					scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(),
					reg.getRegionWidth(), reg.getRegionHeight(), false, false);
			relX += dimension.x;
		}
		
		// Draw right edge
		reg = regEdge;
		batch.draw(reg.getTexture(), position.x + relX, position.y + relY,
				origin.x + dimension.x / 8, origin.y, dimension.x / 4, dimension.y,
				scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(),
				reg.getRegionWidth(), reg.getRegionHeight(), true, false);
	}
}