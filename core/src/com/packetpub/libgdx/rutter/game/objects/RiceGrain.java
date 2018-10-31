package com.packetpub.libgdx.rutter.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.packetpub.libgdx.rutter.game.Assets;

/**
 * Contains information for the rice grain object, which increases score and
 * disappears once it's collected.
 * 
 * @author Kevin Rutter
 */
public class RiceGrain extends AbstractGameObject
{
	private TextureRegion regRiceGrain;

	public boolean collected;

	/**
	 * Constructor for rice grain object.
	 */
	public RiceGrain()
	{
		init();
	}

	/**
	 * Initialize the rice grain object by setting it's width/height, getting its
	 * asset image, setting its bound, and making it uncollected so it's obtainable.
	 */
	private void init()
	{
		dimension.set(0.5f, 0.5f);
		regRiceGrain= Assets.instance.ricegrain.ricegrain;
		
		// Set bounding box for collision detection
		//bounds.set(0, 0, dimension.x, dimension.y);
		collected = false;
	}

	/**
	 * Renders the rice grain (if it hasn't been collected yet).
	 * 
	 * @param batch
	 *            SpriteBatch being used to draw the rice grain.
	 */
	public void render(SpriteBatch batch)
	{
		if (collected)
			return;

		TextureRegion reg = null;
		reg = regRiceGrain;
		batch.draw(reg.getTexture(), position.x, position.y, origin.x, origin.y, dimension.x, dimension.y, scale.x,
				scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(),
				false, false);
	}

	/**
	 * Return the score value of a rice grain.
	 * 
	 * @return A rice grain is worth 10 score.
	 */
	public int getScore()
	{
		return 10;
	}
}