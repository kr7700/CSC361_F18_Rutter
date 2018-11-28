package com.packetpub.libgdx.rutter.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.packetpub.libgdx.rutter.game.Assets;

/**
 * @author Kevin Rutter
 * Contains methods for the bug game object. (inflicts 1 point of damage, dies on contact or after shot)
 */
public class Bug extends AbstractGameObject
{
	private TextureRegion regBug;

	public boolean killed;
	public boolean grounded = false;
	public int frames = 0;

	/**
	 * Constructor, just calls init
	 */
	public Bug()
	{
		init();
	}

	/**
	 * Sets its dimension, gets bug texture, and then sets its bounds
	 */
	private void init()
	{
		dimension.set(1f, 2.0f);

		regBug = Assets.instance.bug.bug;

		// Set bounding box for collision detection
		//bounds.set(0, 0, dimension.x, dimension.y);

		killed = false;
	}

	/**
	 * This draws the bug so long as it hasn't been killed at the given location.
	 */
	public void render(SpriteBatch batch)
	{
		if (killed)
			return;

		TextureRegion reg = null;
		reg = regBug;
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
		return 50;
	}
	
	/**
	 * Returns name of object
	 */
	public String toString()
	{
		return "bug";
	}
	
	@Override
	/**
	 * Updates the bug based on the time since last frame
	 * @param deltaTime		Time since last frame
	 */
	public void update (float deltaTime)
	{
		if (grounded)
			{
			frames++;
			// switch off between moving left and right ever other second
			if (frames < 50)
			{
				body.applyForceToCenter(-100, 0, true);
			}
			else
			{
				if (frames > 100)
				{
					frames = 0;
				}
				else
				{
					body.applyForceToCenter(100, 0, true);
				}
			}
		}
		position.set(body.getPosition());
		rotation = body.getAngle() * MathUtils.radiansToDegrees;
	}
}