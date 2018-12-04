package com.packetpub.libgdx.rutter.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.packetpub.libgdx.rutter.game.Assets;

/**
 * @author Kevin Rutter
 * Contains methods for drawing the background, with the grass and fence.
 */
public class Background extends AbstractGameObject
{
	private TextureRegion regGrass;
	private TextureRegion regFence;

	private int length;

	/**
	 * Constructor, gets length then calls init method
	 * 
	 * @param length desired length of background
	 */
	public Background(int length)
	{
		this.length = length;
		init();
	}

	/**
	 * Initializes background assets to be drawn
	 */
	private void init()
	{
		dimension.set(10, 2);

		regGrass = Assets.instance.levelDecoration.grass;
		regFence = Assets.instance.levelDecoration.fence;

		// shift background and extend length
		origin.x = -dimension.x * 2;
		length += dimension.x * 2;
	}

	/**
	 * Draws the grass in the background
	 * 
	 * @param batch     the spritebatch object
	 * @param offsetX   x offset for drawing the background
	 * @param offsetY   y offset for drawing the background
	 * @param tintColor desired tint to background
	 */
	private void drawGrass(SpriteBatch batch, float offsetX, float offsetY, float tintColor, float parallaxSpeedX)
	{
		TextureRegion reg = null;
		batch.setColor(tintColor, tintColor, tintColor, 1);
		float xRel = dimension.x * offsetX;
		float yRel = dimension.y * offsetY;

		// background spans the whole level
		int backgroundLength = 0;
		backgroundLength += MathUtils.ceil(length / (2 * dimension.x) * (1 - parallaxSpeedX));
		backgroundLength += MathUtils.ceil(0.6f + offsetX);

		for (int i = 0; i < backgroundLength; i++)
		{
			// grass
			reg = regGrass;
			batch.draw(reg.getTexture(), origin.x + xRel + position.x * parallaxSpeedX, origin.y + yRel + position.y,
					origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y*2, rotation, reg.getRegionX(),
					reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(), false, false);
			xRel += dimension.x;
		}

		// reset color to white
		batch.setColor(1, 1, 1, 1);
	}
	
	/**
	 * Draws the fence in the background
	 * 
	 * @param batch     the spritebatch object
	 * @param offsetX   x offset for drawing the background
	 * @param offsetY   y offset for drawing the background
	 * @param tintColor desired tint to background
	 */
	private void drawFence(SpriteBatch batch, float offsetX, float offsetY, float tintColor, float parallaxSpeedX)
	{
		TextureRegion reg = null;
		batch.setColor(tintColor, tintColor, tintColor, 1);
		float xRel = dimension.x * offsetX;
		float yRel = dimension.y * offsetY;

		// background spans the whole level
		int backgroundLength = 0;
		backgroundLength += MathUtils.ceil(length / (2 * dimension.x) * (1 - parallaxSpeedX));
		backgroundLength += MathUtils.ceil(0.6f + offsetX);

		for (int i = 0; i < backgroundLength; i++)
		{
			// fence
			reg = regFence;
			batch.draw(reg.getTexture(), origin.x + xRel + position.x * parallaxSpeedX, origin.y + yRel + position.y,
					origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y*2, rotation, reg.getRegionX(),
					reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(), false, false);
			xRel += dimension.x;
		}

		// reset color to white
		batch.setColor(1, 1, 1, 1);
	}

	/**
	 * Calls the drawGrass and drawFence method.
	 * 
	 * @param batch	Spritebatch being used to draw.
	 */
	@Override
	public void render(SpriteBatch batch)
	{
		// fences in the farthest background layer
		drawFence(batch, 0.5f, .5f, 1f, .8f);

		// grass in the second background layer
		drawGrass(batch, 0.25f, .25f, 0.5f, .5f);

		// grass in the closest background layer
		drawGrass(batch, 0.0f, -.15f, 0.9f, .3f);
	}

	/**
	 * Updates the scrolling of the background
	 * @param camPosition	Vector of the camera's position
	 */
	public void updateScrollPosition(Vector2 camPosition)
	{
		position.set(camPosition.x, position.y);
	}
}