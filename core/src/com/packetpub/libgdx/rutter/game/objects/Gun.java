package com.packetpub.libgdx.rutter.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.packetpub.libgdx.rutter.game.Assets;

/**
 * @author Kevin Rutter
 * Contains methods for the Gun game object
 */
public class Gun extends AbstractGameObject
{
	private TextureRegion regGun;

	public boolean collected;
	public int bullets;

	/**
	 * Constructor, just calls init
	 */
	public Gun()
	{
		init();
	}

	/**
	 * Sets its dimension, gets gun texture, sets its bounds, and the starting number of bullets.
	 */
	private void init()
	{
		dimension.set(0.5f, 0.5f);

		regGun = Assets.instance.gun.gun;

		// Set bounding box for collision detection
		//bounds.set(0, 0, dimension.x, dimension.y);

		collected = false;
		bullets = 3;
	}

	/**
	 * This draws the gun so long as it hasn't been collected at desired location.
	 */
	public void render(SpriteBatch batch)
	{
		if (collected)
		{
			// later, gun will be rendered next to the riceball once collected.
			//if (bullets == 0)
				return;
		}

		TextureRegion reg = null;
		reg = regGun;
		batch.draw(reg.getTexture(), position.x, position.y, origin.x, origin.y, dimension.x, dimension.y, scale.x,
				scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(),
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