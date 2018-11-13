package com.packetpub.libgdx.rutter.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Makes an abstract class for all game objects to structure themselves around
 * @author Kevin Rutter
 * (Based on CanyonBunny AbstractGameObject)
 */
public abstract class AbstractGameObject
{
	public Vector2 position;
	public Vector2 dimension;
	public Vector2 origin;
	public Vector2 scale;
	public float rotation;
	
	public Body body;
	
	/**
	 * Builds the game object
	 */
	public AbstractGameObject ()
	{
		position = new Vector2();
		dimension = new Vector2(1,1);
		origin = new Vector2();
		scale = new Vector2(1,1);
		rotation = 0;
	}
	
	/**
	 * Updates the object based on the time since last frame
	 * @param deltaTime		Time since last frame
	 */
	public void update (float deltaTime)
	{
		position.set(body.getPosition());
		rotation = body.getAngle() * MathUtils.radiansToDegrees;
	}
	
	/**
	 * Gets the appearance of the game object
	 * @param batch is the sprite batch it uses
	 */
	public abstract void render (SpriteBatch batch);
}
