package com.packetpub.libgdx.rutter.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.packetpub.libgdx.rutter.game.Assets;
import com.packetpub.libgdx.rutter.util.AudioManager;
import com.packetpub.libgdx.rutter.util.Constants;

/**
 * The main character of the game that the player controls
 * @author Kevin Rutter
 */
public class RiceBall extends AbstractGameObject
{
	public static final String TAG = RiceBall.class.getName();
	
	private final float JUMP_TIME_MAX = 0.3f;
	private final float JUMP_TIME_MIN = 0.1f;
	private final float JUMP_TIME_OFFSET_FLYING = JUMP_TIME_MAX - 0.018f;
	
	public enum VIEW_DIRECTION {LEFT, RIGHT}
	
	public enum JUMP_STATE
	{
		GROUNDED, FALLING, JUMP_RISING, JUMP_FALLING
	}
	
	private TextureRegion regBall;
	private TextureRegion regBallHappy;
	
	public VIEW_DIRECTION viewDirection;
	public float timeJumping;
	public JUMP_STATE jumpState;
	public int bullets;
	public int health;
	public ParticleEffect dustParticles = new ParticleEffect();

	public boolean isJumping = false;
	
	/**
	 * Constructor for riceball, calls init()
	 */
	public RiceBall()
	{
		init();
	}
	
	/**
	 *	Initialized the Object RiceBall in the world, setting it's origin, friction, acceleration, etc.
	 */
	public void init()
	{
		dimension.set(1,1);
		regBall = Assets.instance.riceball.riceball;
		regBallHappy = Assets.instance.riceball.riceballhappy;
		//Center image on game object
		origin.set(dimension.x/2, dimension.y/2);
//		//Bounding box for collision detection
//		bounds.set(0,0,dimension.x, dimension.y);
		//Set physics values
//		terminalVelocity.set(3.0f,4.0f);
//		friction.set(12.0f,0.0f);
//		acceleration.set(0.0f, -25.0f);
		//View direction
		viewDirection = VIEW_DIRECTION.RIGHT;
		//Jump state
		jumpState = JUMP_STATE.FALLING;
		timeJumping = 0;
		//Power-ups
		bullets = 0;
		health = 1;
		
		// Particles
		dustParticles.load(Gdx.files.internal("particles/dust.pfx"), Gdx.files.internal("particles"));
	}
	
	/**
	 * Handles the riceball's jumping
	 * @param jumpKeyPressed If the key is pressed
	 */
	public void setJumping(boolean jumpKeyPressed)
	{
		switch (jumpState) {
			case GROUNDED: //Character is standing on a platform
				if (jumpKeyPressed) {
					//Start counting jump time form the beginning
					timeJumping = 0;
					jumpState = JUMP_STATE.JUMP_RISING;
				}
				break;
			case JUMP_RISING: //Rising in the air
				if (!jumpKeyPressed)
				jumpState = JUMP_STATE.JUMP_FALLING;
				break;
			case FALLING: //Falling down
			case JUMP_FALLING: //Falling down after jump
//			if (jumpKeyPressed && hasFeatherPowerup) {
//				timeJumping = JUMP_TIME_OFFSET_FLYING;
//				jumpState = JUMP_STATE.JUMP_RISING;
//			}
			break;
		}
	}
	
	/**
	 * Sets the riceball to have the gun powerup
	 * @param bullets	Amount of bullets in the gun
	 */
	public void setGunPowerUp(int bullets)
	{
		AudioManager.instance.play(Assets.instance.sounds.reload);
		if (this.bullets < bullets)
		{
			this.bullets = bullets;
		}
	}
	
	/**
	 * @return How many bullets the riceball has left in the gun.
	 */
	public int bulletsLeft()
	{
		return bullets;
	}
	
	/**
	 * @return Amount of health the riceball has left.
	 */
	public int healthLeft()
	{
		return health;
	}
	
	/**
	 * Increase or decrease amount of health riceball has
	 * @param changed	Amount health is being changed by (+1 if nori, -1 if hit by bug)
	 */
	public void changeHealth(int changed)
	{
		// changed +1 means nori item is picked up
		if (changed == 1)
		{
			// only increase health to maximum of 2, nori can't be stacked
			if (health == 1)
				health++;
		}
		else
			health += changed;
		if (changed < 0)
		{
			AudioManager.instance.play(Assets.instance.sounds.oof);
		}
		System.out.println("health is now " + health);
	}
	
	/**
	 * Updates the object, when called upon 60 times every secound
	 */
	@Override
	public void update (float deltaTime)
	{
		super.update(deltaTime);
		dustParticles.update(deltaTime);
//		if (velocity.x != 0) {
//			viewDirection = velocity.x < 0 ? VIEW_DIRECTION.LEFT : 
//	VIEW_DIRECTION.RIGHT;
//		}
//		if (timeLeftFeatherPowerup > 0) {
//			timeLeftFeatherPowerup -= deltaTime;
//			if (timeLeftFeatherPowerup < 0) {
//				//disable power-up
//				timeLeftFeatherPowerup = 0;
//				setFeatherPowerup(false);
//			}
//		}
//	}
	
//	/**
//	 * Updates the jumping motion of the player
//	 * @param deltaTime time in the game currently
//	 */
//	@Override
//	protected void updateMotionY (float deltaTime) {
//		switch (jumpState) {
//		case GROUNDED:
//			jumpState = JUMP_STATE.FALLING;
//			break;
//		case JUMP_RISING:
//			//Keep track of jump time
//			timeJumping += deltaTime;
//			//Jump time left?
//			if (timeJumping <= JUMP_TIME_MAX) {
//				//Still jumping
//				velocity.y = terminalVelocity.y;	
//			}
//		}
//		if (jumpState != JUMP_STATE.GROUNDED)
//		super.updateMotionY(deltaTime);
	}
	
	/**
	 * Renders the riceball in the world using the sprite batch
	 */
	@Override
	public void render(SpriteBatch batch)
	{
		TextureRegion reg = null;
		
		//Draw image
		if (health == 2)
		{
			reg = regBallHappy;
		}
		else
		{
			reg = regBall;
		}
		batch.draw(reg.getTexture(),position.x, position.y, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation,reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(), viewDirection == VIEW_DIRECTION.LEFT, false);
		
		if (bullets > 0)
		{
			reg = Assets.instance.gun.gun;
			if (viewDirection == VIEW_DIRECTION.RIGHT)
				batch.draw(reg.getTexture(),position.x+.75f, position.y, origin.x, origin.y, dimension.x, dimension.y, scale.x/2, scale.y/2, 0,reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(), false, false);
			else if (viewDirection == VIEW_DIRECTION.LEFT)
				batch.draw(reg.getTexture(),position.x-.75f, position.y, origin.x, origin.y, dimension.x, dimension.y, scale.x/2, scale.y/2, 0,reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(), true, false);
		}
		
		// Draw Particles
		dustParticles.draw(batch);
	}
	
	/**
	 * Returns name of object
	 */
	public String toString()
	{
		return "riceball";
	}
}