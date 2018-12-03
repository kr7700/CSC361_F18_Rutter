package com.packetpub.libgdx.rutter.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.packetpub.libgdx.rutter.game.Assets;

/**
 * Contains information for the bullet, which is used to damage bugs.
 * 
 * @author Kevin Rutter
 */
public class Bullet extends AbstractGameObject
{
	private TextureRegion regBullet;

	public boolean onScreen;

	/**
	 * Constructor for the bullet object.
	 */
	public Bullet()
	{
		init();
	}

	/**
	 * Initialize the bullet object by setting it's width/height, getting its
	 * asset image, setting its bound, and making it not on screen so it's not rendered.
	 */
	private void init()
	{
		dimension.set(0.25f, 0.14f);
		regBullet = Assets.instance.bullet.bullet;
		
		// Set bounding box for collision detection
		//bounds.set(0, 0, dimension.x, dimension.y);
		onScreen = false;
	}

	/**
	 * Renders the bullet (if it's been fired, and hasn't hit anything)
	 * 
	 * @param batch
	 *            SpriteBatch being used to draw the bullet.
	 */
	public void render(SpriteBatch batch)
	{
		if (!onScreen)
			return;

		TextureRegion reg = null;
		reg = regBullet;
		batch.draw(reg.getTexture(), position.x, position.y, origin.x, origin.y, dimension.x, dimension.y, scale.x,
				scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(),
				false, false);
	}
	
	/**
	 * Returns name of object
	 */
	public String toString()
	{
		return "bullet";
	}
}