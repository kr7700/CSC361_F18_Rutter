package com.packetpub.libgdx.rutter.util;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.packetpub.libgdx.rutter.game.Level;

/**
 * Contact Listener for the World.
 * Performs certain functions when one object collides with the other.
 * 
 * @author Kevin Rutter
 */
public class B2Listener implements ContactListener
{
	private Level level;
	
	/**
	 * Constructor for B2Listener. Needs level to get objects.
	 * @param level		The game's level.
	 */
	public B2Listener(Level level)
	{
		this.level = level;
	}
	
	@Override
	public void beginContact(Contact contact)
	{
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		if (fixtureA.getBody().getUserData().toString() == "riceball");
		{
			if (fixtureB.getBody().getUserData().toString() == "dirt");
			{
				level.riceBall.isJumping = false;
			}
		}
		if (fixtureB.getBody().getUserData().toString() == "riceball");
		{
			if (fixtureA.getBody().getUserData().toString() == "dirt");
			{
				level.riceBall.isJumping = false;
			}
		}
	}

	@Override
	public void endContact(Contact contact)
	{
		
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold)
	{
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse)
	{
		
	}
}