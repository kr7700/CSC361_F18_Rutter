package com.packetpub.libgdx.rutter.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.packetpub.libgdx.rutter.game.Assets;

/**
 * @author Kevin Rutter
 * Contains methods for the Nori game object (acts like a mushroom in Mario)
 */
public class Nori extends AbstractGameObject
{
	private TextureRegion regNori;

	public boolean collected;

	/**
	 * Constructor, just calls init
	 */
	public Nori()
	{
		init();
	}

	/**
	 * Sets its dimension, gets nori texture, and then sets its bounds
	 */
	private void init()
	{
		dimension.set(0.5f, 0.5f);

		regNori = Assets.instance.nori.nori;

		// Set bounding box for collision detection
		//bounds.set(0, 0, dimension.x, dimension.y);

		collected = false;
	}

	/**
	 * This draws the nori so long as it hasn't been collected at desired location.
	 */
	public void render(SpriteBatch batch)
	{
		if (collected)
			return;

		TextureRegion reg = null;
		reg = regNori;
		batch.draw(reg.getTexture(), position.x, position.y, origin.x, origin.y, dimension.x, dimension.y, scale.x,
				scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(), reg.getRegionWidth(),
				false, false);
	}

	/**
	 * Returns a score value when called
	 * 
	 * @return score value
	 */
	public int getScore()
	{
		return 30;
	}
}