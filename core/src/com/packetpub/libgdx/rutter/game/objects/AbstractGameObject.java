package com.packetpub.libgdx.rutter.game.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
		
	private final float FLOAT_CYCLE_TIME = .5f;
	private final float FLOAT_AMPLITUDE = 0.5f;
	
	private float floatCycleTimeLeft;
	private boolean floatingDownwards;
	private Vector2 floatTargetPosition;
	
	public Body body;
	
	public float stateTime;
	public Animation<TextureRegion> animation;
	
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
		
		floatingDownwards = false;
		floatCycleTimeLeft = MathUtils.random(0, FLOAT_CYCLE_TIME / 2);
		floatTargetPosition = null;
	}
	
	/**
	 * Sets the current animation from the animation object
	 * @param animation animation object
	 */
	public void setAnimation(Animation animation)
	{
		this.animation = animation;
		stateTime = 0;
	}
	
	/**
	 * Updates the object based on the time since last frame
	 * @param deltaTime		Time since last frame
	 */
	public void update (float deltaTime)
	{
		stateTime += deltaTime;
		if (body != null)
		{
			position.set(body.getPosition());
			rotation = body.getAngle() * MathUtils.radiansToDegrees;
		}
	}
	
	/**
	 * Gets the appearance of the game object
	 * @param batch is the sprite batch it uses
	 */
	public abstract void render (SpriteBatch batch);
	
	/**
	 * Handles the lerp floating of certain items.
	 * @param deltaTime	Time since last frame.
	 */
	public void lerpUpdate(float deltaTime)
	{
		floatCycleTimeLeft -= deltaTime;
		if (floatTargetPosition == null)
			floatTargetPosition = new Vector2(position);

		if (floatCycleTimeLeft <= 0)
		{
			floatCycleTimeLeft = FLOAT_CYCLE_TIME;
			floatingDownwards = !floatingDownwards;
			body.setLinearVelocity(0, FLOAT_AMPLITUDE * (floatingDownwards ? -1 : 1));
		}
		else
		{
			body.setLinearVelocity(body.getLinearVelocity().scl(0.98f));
		}
		position.lerp(floatTargetPosition, deltaTime);
	}
}
