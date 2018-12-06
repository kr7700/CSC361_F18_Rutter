package com.packetpub.libgdx.rutter.util;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.packetpub.libgdx.rutter.game.Level;
import com.packetpub.libgdx.rutter.game.WorldController;
import com.packetpub.libgdx.rutter.game.objects.Bug;
import com.packetpub.libgdx.rutter.game.objects.Bullet;
import com.packetpub.libgdx.rutter.game.objects.Gun;
import com.packetpub.libgdx.rutter.game.objects.Nori;
import com.packetpub.libgdx.rutter.game.objects.RiceGrain;

/**
 * Contact Listener for the World.
 * Performs certain functions when one object collides with the other.
 * 
 * @author Kevin Rutter
 */
public class B2Listener implements ContactListener
{
	private Level level;
	private WorldController worldController;
	
	/**
	 * Constructor for B2Listener. Needs level to get objects, controller to modify score, destroy bodies.
	 * @param level		The game's level.
	 * @param worldController	The game's worldController.
	 */
	public B2Listener(Level level, WorldController worldController)
	{
		this.level = level;
		this.worldController = worldController;
	}
	
	@Override
	public void beginContact(Contact contact)
	{
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		System.out.println("Begin Contact: " + fixtureA.getBody().getUserData().toString() + " and " + fixtureB.getBody().getUserData().toString());
		if (fixtureA.getBody().getUserData().toString() == "riceball" || fixtureB.getBody().getUserData().toString() == "riceball")
		{
			//swap fixtureA and B so that riceball is in B, other object is in A.
			if (fixtureA.getBody().getUserData().toString() == "riceball")
			{
				fixtureB = contact.getFixtureA();
				fixtureA = contact.getFixtureB();
			}
			
			if (fixtureA.getBody().getUserData().toString() == "dirt");
			{
				level.riceBall.isJumping = false;
			}
			if (fixtureA.getBody().getUserData().toString() == "ricegrain")
			{
				for (RiceGrain grain: level.ricegrains)
				{
					if (fixtureA.getBody().getUserData().equals(grain))
					{
						worldController.removeFlagged.add(fixtureA.getBody());
						grain.collected = true;
						worldController.score += grain.getScore();
					}
				}
			}
			if (fixtureA.getBody().getUserData().toString() == "nori")
			{
				for (Nori nori : level.nori)
				{
					if (fixtureA.getBody().getUserData().equals(nori))
					{
						worldController.removeFlagged.add(fixtureA.getBody());
						nori.collected = true;
						worldController.score += nori.getScore();
						level.riceBall.changeHealth(1);
					}
				}
			}
			if (fixtureA.getBody().getUserData().toString() == "gun")
			{
				for (Gun gun : level.guns)
				{
					if (fixtureA.getBody().getUserData().equals(gun))
					{
						worldController.removeFlagged.add(fixtureA.getBody());
						gun.collected = true;
						worldController.score += gun.getScore();
						level.riceBall.setGunPowerUp(gun.bullets);
					}
				}
			}
			if (fixtureA.getBody().getUserData().equals(level.waterOverlay))
			{
				worldController.inWater = true;
			}
			if (fixtureA.getBody().getUserData().equals(level.goal))
			{
				worldController.goalReached = true;
				worldController.timeLeftGameOverDelay = Constants.TIME_DELAY_GAME_OVER;
			}
			if (fixtureA.getBody().getUserData().toString() == "bug")
			{
				for (Bug bug : level.bugs)
				{
					if (fixtureA.getBody().getUserData().equals(bug))
					{
						worldController.removeFlagged.add(fixtureA.getBody());
						bug.killed = true;
						worldController.score += bug.getScore();
						level.riceBall.changeHealth(-1);
					}
				}
			}
		}
		
		if (fixtureA.getBody().getUserData().toString() == "bug")
		{
			Bug bug = (Bug) fixtureA.getBody().getUserData();
			bug.grounded = true;
		}
		if (fixtureB.getBody().getUserData().toString() == "bug")
		{
			Bug bug = (Bug) fixtureB.getBody().getUserData();
			bug.grounded = true;
		}
		
		if (fixtureA.getBody().getUserData().toString() == "bullet")
		{
			if (fixtureB.getBody().getUserData().toString() == "bug")
			{
				for (Bug bug : level.bugs)
				{
					if (fixtureB.getBody().getUserData().equals(bug))
					{
						worldController.removeFlagged.add(fixtureB.getBody());
						bug.killed = true;
						worldController.score += bug.getScore();
					}
				}
			}
			if (fixtureB.getBody().getUserData().toString() == "bug" || fixtureB.getBody().getUserData().toString() == "dirt" || fixtureB.getBody().getUserData().toString() == "water")
			{
				for (Bullet bullet : level.bullets)
				{
					if (fixtureA.getBody().getUserData().equals(bullet))
					{
						worldController.removeFlagged.add(fixtureA.getBody());
						bullet.onScreen = false;
					}
				}
			}
		}
		if (fixtureB.getBody().getUserData().toString() == "bullet")
		{
			if (fixtureA.getBody().getUserData().toString() == "bug")
			{
				for (Bug bug : level.bugs)
				{
					if (fixtureA.getBody().getUserData().equals(bug))
					{
						worldController.removeFlagged.add(fixtureA.getBody());
						bug.killed = true;
						worldController.score += bug.getScore();
					}
				}
			}
			//if (fixtureA.getBody().getUserData().toString() == "bug" || fixtureA.getBody().getUserData().toString() == "dirt" || fixtureA.getBody().getUserData().toString() == "water")
			{
				for (Bullet bullet : level.bullets)
				{
					if (fixtureB.getBody().getUserData().equals(bullet))
					{
						worldController.removeFlagged.add(fixtureB.getBody());
						bullet.onScreen = false;
					}
				}
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