package com.packetpub.libgdx.rutter.game;

import com.badlogic.gdx.graphics.Pixmap;

import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.packetpub.libgdx.rutter.game.objects.Bug;
import com.packetpub.libgdx.rutter.game.objects.Bullet;
import com.packetpub.libgdx.rutter.game.objects.Dirt;
import com.packetpub.libgdx.rutter.game.objects.Goal;
import com.packetpub.libgdx.rutter.game.objects.Gun;
import com.packetpub.libgdx.rutter.game.objects.Nori;
import com.packetpub.libgdx.rutter.game.objects.RiceBall;
import com.packetpub.libgdx.rutter.game.objects.RiceBall.JUMP_STATE;
import com.packetpub.libgdx.rutter.game.objects.RiceGrain;
import com.packetpub.libgdx.rutter.game.objects.WaterOverlay;
import com.packetpub.libgdx.rutter.screens.MenuScreen;
import com.packetpub.libgdx.rutter.util.B2Listener;
import com.packetpub.libgdx.rutter.util.CameraHelper;
import com.packetpub.libgdx.rutter.util.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;

/**
 * @author Kevin Rutter
 * Contains controls for the game, such as for the camera, movement, etc.
 * (Based on CanyonBunny WorldController)
 */
public class WorldController extends InputAdapter implements Disposable
{
	// Tag used for logging purposes
	private static final String TAG =
			WorldController.class.getName();
	
	
	public CameraHelper cameraHelper;
	
	public Level level;
	public Game game;
	public int lives = Constants.LIVES_START;
	public int score = 0;
	public boolean goalReached;
	public boolean inWater = false;
	public boolean gameEnded = false;
	public float timeLeftGameOverDelay;
	private TextField txt;
	private Stage stage;
	
	public World b2world;
	public B2Listener listener;
	public ArrayList<Body> removeFlagged = new ArrayList<Body>();
	
	/**
	 * Constructor for WorldController.
	 * @param game The game application listener.
	 */
	public WorldController(Game game)
	{
		init();
		this.game = game;
	}
	
	/**
	 * Initialization code for WorldController.
	 * Useful to call when resetting objects.
	 */
	public void init()
	{
		Gdx.input.setInputProcessor(this);
		cameraHelper = new CameraHelper();
		initLevel();
	}
	
	/**
	 * Initializes a new level.
	 */
	public void initLevel()
	{
		score = 0;
		goalReached = false;
		inWater = false;
		timeLeftGameOverDelay = 0;
		level = new Level(Constants.LEVEL_01);
		cameraHelper.setTarget(level.riceBall);
		initPhysics();
	}
	
	/**
	 * Initialize the world and BodyDef objects in order to track physics in Box2D.
	 */
	private void initPhysics()
	{
		if (b2world != null)
			b2world.dispose();
		b2world = new World(new Vector2(0, -9.81f), true);
		
		// Dirt platforms
		Vector2 origin = new Vector2();
		for (Dirt dirt : level.dirtPlatforms)
		{
			BodyDef bodyDef = new BodyDef();
			bodyDef.type = BodyType.KinematicBody;
			bodyDef.position.set(dirt.position);
			Body body = b2world.createBody(bodyDef);
			dirt.body = body;
			PolygonShape polygonShape = new PolygonShape();
			origin.x = dirt.bounds.width / 2.0f;
			origin.y = dirt.bounds.height / 2.0f;
			polygonShape.setAsBox(dirt.bounds.width / 2.0f, dirt.bounds.height / 2.0f, origin, 0);
			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.shape = polygonShape;
			body.createFixture(fixtureDef);
			body.setUserData(dirt);
			polygonShape.dispose();
		}
		
		// Bugs
		for (Bug bug : level.bugs)
		{
			BodyDef bodyDef = new BodyDef();
			bodyDef.type = BodyType.DynamicBody;
			bodyDef.position.set(bug.position);
			Body body = b2world.createBody(bodyDef);
			bug.body = body;
			PolygonShape polygonShape = new PolygonShape();
			origin.x = bug.dimension.x / 2.0f;
			origin.y = bug.dimension.y / 2.0f;
			polygonShape.setAsBox(bug.dimension.x / 2.0f, bug.dimension.y / 2.0f, origin, 0);
			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.shape = polygonShape;
			fixtureDef.density = 10;
			fixtureDef.restitution = 0.001f;
			fixtureDef.friction = 0.9f;
			body.createFixture(fixtureDef);
			fixtureDef.isSensor = true;
			body.setUserData(bug);
			polygonShape.dispose();
		}
		
		// Guns
		for (Gun gun : level.guns)
		{
			BodyDef bodyDef = new BodyDef();
			bodyDef.type = BodyType.KinematicBody;
			bodyDef.position.set(gun.position);
			Body body = b2world.createBody(bodyDef);
			gun.body = body;
			PolygonShape polygonShape = new PolygonShape();
			origin.x = gun.dimension.x / 2.0f;
			origin.y = gun.dimension.y / 2.0f;
			polygonShape.setAsBox(gun.dimension.x / 2.0f, gun.dimension.y / 2.0f, origin, 0);
			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.shape = polygonShape;
			fixtureDef.isSensor = true;
			body.createFixture(fixtureDef);
			body.setUserData(gun);
			polygonShape.dispose();
		}
		
		// Nori
		for (Nori nori : level.nori)
		{
			BodyDef bodyDef = new BodyDef();
			bodyDef.type = BodyType.KinematicBody;
			bodyDef.position.set(nori.position);
			Body body = b2world.createBody(bodyDef);
			nori.body = body;
			PolygonShape polygonShape = new PolygonShape();
			origin.x = nori.dimension.x / 2.0f;
			origin.y = nori.dimension.y / 2.0f;
			polygonShape.setAsBox(nori.dimension.x / 2.0f, nori.dimension.y / 2.0f, origin, 0);
			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.shape = polygonShape;
			fixtureDef.isSensor = true;
			body.createFixture(fixtureDef);
			body.setUserData(nori);
			polygonShape.dispose();
		}
		
		// RiceGrains
		for (RiceGrain grain : level.ricegrains)
		{
			BodyDef bodyDef = new BodyDef();
			bodyDef.type = BodyType.KinematicBody;
			bodyDef.position.set(grain.position);
			Body body = b2world.createBody(bodyDef);
			grain.body = body;
			PolygonShape polygonShape = new PolygonShape();
			origin.x = grain.dimension.x / 2.0f;
			origin.y = grain.dimension.y / 2.0f;
			polygonShape.setAsBox(grain.dimension.x /2.0f, grain.dimension.y / 2.0f, origin, 0);
			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.shape = polygonShape;
			fixtureDef.isSensor = true;
			body.createFixture(fixtureDef);
			body.setUserData(grain);
			polygonShape.dispose();
		}
		
		// Water
		WaterOverlay water = level.waterOverlay;
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;
		bodyDef.position.set(water.position);
		Body body = b2world.createBody(bodyDef);
		water.body = body;
		PolygonShape polygonShape = new PolygonShape();
		origin.x = water.dimension.x / 2.0f;
		origin.y = water.dimension.y / 2.0f;
		polygonShape.setAsBox(water.dimension.x / 2.0f, water.dimension.y / 2.0f, origin, 0);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = polygonShape;
		fixtureDef.isSensor = true;
		body.createFixture(fixtureDef);
		body.setUserData(water);
		polygonShape.dispose();
		
		// RiceBall
		RiceBall ball = level.riceBall;
		bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(ball.position);
		body = b2world.createBody(bodyDef);
		ball.body = body;
		polygonShape = new PolygonShape();
		origin.x = ball.origin.x;
		origin.y = ball.origin.y;
		polygonShape.setAsBox(ball.dimension.x /2.0f, ball.dimension.y / 2.0f, origin, 0);
		fixtureDef = new FixtureDef();
		fixtureDef.shape = polygonShape;
	//	CircleShape circle = new CircleShape();
		//circle.setRadius(ball.origin.x/2);
		fixtureDef.shape = polygonShape;
		fixtureDef.density = 1;
		fixtureDef.restitution = 0.001f;
		fixtureDef.friction = 1f;
		body.createFixture(fixtureDef);
		body.setUserData(ball);
		polygonShape.dispose();
		
		//Goal
		Goal goal = level.goal;
		bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;
		bodyDef.position.set(goal.position);
		body = b2world.createBody(bodyDef);
		goal.body = body;
		polygonShape = new PolygonShape();
		origin.x = goal.dimension.x / 2.0f;
		origin.y = goal.dimension.y / 2.0f;
		polygonShape.setAsBox(goal.dimension.x / 2.0f, goal.dimension.y / 2.0f, origin, 0);
		fixtureDef = new FixtureDef();
		fixtureDef.shape = polygonShape;
		fixtureDef.isSensor = true;
		body.createFixture(fixtureDef);
		body.setUserData(goal);
		polygonShape.dispose();
		
		listener = new B2Listener(level, this);
        b2world.setContactListener(listener);
	}
	
	/**
	 * Resets game if R is pressed, changes selected sprite if space is pressed.
	 * @param keycode	The key that was released.
	 * @return			false
	 */
	@Override
	public boolean keyUp(int keycode)
	{
		// Reset game world
		if (keycode == Keys.R)
		{
			init();
			Gdx.app.debug(TAG, "Game world resetted");
		}
		return false;
	}
	
	/**
	 * Handles movement and jumping of the bunnyhead.
	 * 
	 * @param deltaTime
	 *            How much time has passed since last frame.
	 */
	private void handleInputGame(float deltaTime)
	{
		if (cameraHelper.hasTarget())
		{
			if (level.riceBall.health > 0)
			{
				// Player Movement
				if (Gdx.input.isKeyPressed(Keys.LEFT))
				{
					level.riceBall.body.applyForceToCenter(-50, 0, true);
					level.riceBall.viewDirection = RiceBall.VIEW_DIRECTION.LEFT;
				}
				else if (Gdx.input.isKeyPressed(Keys.RIGHT))
				{
					level.riceBall.body.applyForceToCenter(50, 0, true);
					level.riceBall.viewDirection = RiceBall.VIEW_DIRECTION.RIGHT;
				}
				if (Gdx.input.isKeyJustPressed(Keys.SPACE) && !level.riceBall.isJumping)
				{
					level.riceBall.isJumping = true;
					level.riceBall.body.applyForceToCenter(0, 200, true);
				}
				if (Gdx.input.isKeyJustPressed(Keys.CONTROL_LEFT) && level.riceBall.bullets > 0)
				{
					level.riceBall.bullets--;
					fireBullet();
				}
			}
			if (level.riceBall.health <= 0)
			{
				lives--;
				initLevel();
			}
		}
	}
	
	/**
	 * Get a bullet and place it into the world.
	 */
	public void fireBullet()
	{
		boolean bulletNotFired = true;
		for (int i = 0; i < level.bullets.size & bulletNotFired; i++)
		{
			Bullet bullet = level.bullets.get(i);
			if (!bullet.onScreen)
			{
				bulletNotFired = false;
				if (level.riceBall.viewDirection.equals(RiceBall.VIEW_DIRECTION.RIGHT))
					bullet.position.x = level.riceBall.position.x + 1.7f;
				else if (level.riceBall.viewDirection.equals(RiceBall.VIEW_DIRECTION.LEFT))
				{
					bullet.position.x = level.riceBall.position.x - .7f;
					bullet.reversed = true;
				}
				
				bullet.position.y = level.riceBall.position.y + .3f;
				bullet.onScreen = true;
				
				BodyDef bodyDef = new BodyDef();
				bodyDef.type = BodyType.DynamicBody;
				bodyDef.position.set(bullet.position);
				Body body = b2world.createBody(bodyDef);
				bullet.body = body;
				
				if (level.riceBall.viewDirection.equals(RiceBall.VIEW_DIRECTION.RIGHT))
					body.setLinearVelocity(20, 0);
				if (level.riceBall.viewDirection.equals(RiceBall.VIEW_DIRECTION.LEFT))
					body.setLinearVelocity(-20, 0);
				
				PolygonShape polygonShape = new PolygonShape();
				Vector2 origin = new Vector2(bullet.origin.x, bullet.origin.y);
				polygonShape.setAsBox(bullet.dimension.x /2.0f, bullet.dimension.y / 2.0f, origin, 0);
				FixtureDef fixtureDef = new FixtureDef();
				fixtureDef.shape = polygonShape;
				fixtureDef.density = 1;
				fixtureDef.restitution = 0;
				fixtureDef.friction = 1;
				body.createFixture(fixtureDef);
				body.setUserData(bullet);
				polygonShape.dispose();
			}
		}
	}
	
	/**
	 * Applies updates to the game world many times a second.
	 * @param deltaTime		How much time has passed since last frame.
	 */
	public void update(float deltaTime)
	{
		if (!gameEnded)
		{
			if (lives == 0 || goalReached)
			{
				timeLeftGameOverDelay -= deltaTime;
				if (timeLeftGameOverDelay < 0)
				{
					gameEnded = true;
					gameOver();
				}
			}
			else
			{
				handleInputGame(deltaTime);
			}
			level.update(deltaTime);
			b2world.step(deltaTime, 8, 3);
			while (!removeFlagged.isEmpty())
			{
				b2world.destroyBody(removeFlagged.get(0));
				removeFlagged.remove(0);
			}
			cameraHelper.update(deltaTime);
			if (lives > 0 && inWater)
			{
				lives--;
				if (lives == 0)
					timeLeftGameOverDelay = Constants.TIME_DELAY_GAME_OVER;
				else
					initLevel();
			}
			level.background.updateScrollPosition(cameraHelper.getPosition());
		}
	}
	
	/**
	 * Tests out movement of sprites and camera.
	 * @param deltaTime		How much time since last frame.
	 */
	private void handleDebugInput(float deltaTime)
	{
		if (Gdx.app.getType() != ApplicationType.Desktop)
			return;
		
		// Camera Controls (move)
		float camMoveSpeed = 5 * deltaTime;
		float camMoveSpeedAccelerationFactor = 5;
		if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT))
			camMoveSpeed *= camMoveSpeedAccelerationFactor;
		if (Gdx.input.isKeyPressed(Keys.LEFT))
			moveCamera(-camMoveSpeed, 0);
		if (Gdx.input.isKeyPressed(Keys.RIGHT))
			moveCamera(camMoveSpeed, 0);
		if (Gdx.input.isKeyPressed(Keys.UP))
			moveCamera(0, camMoveSpeed);
		if (Gdx.input.isKeyPressed(Keys.DOWN))
			moveCamera(0, -camMoveSpeed);
		if (Gdx.input.isKeyPressed(Keys.BACKSPACE))
			cameraHelper.setPosition(0, 0);
		
		// Camera Controls (zoom)
		float camZoomSpeed = 1 * deltaTime;
		float camZoomSpeedAccelerationFactor = 5;
		if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT))
			camZoomSpeed *= camZoomSpeedAccelerationFactor;
		if (Gdx.input.isKeyPressed(Keys.COMMA))
			cameraHelper.addzoom(camZoomSpeed);
		if (Gdx.input.isKeyPressed(Keys.PERIOD))
			cameraHelper.addzoom(-camZoomSpeed);
		if (Gdx.input.isKeyPressed(Keys.SLASH))
			cameraHelper.setZoom(1);
	}
	
	/**
	 * Moves the Camera in a given direction
	 * @param x		Horizontal distance.
	 * @param y		Vertical distance.
	 */
	private void moveCamera(float x, float y)
	{
		x += cameraHelper.getPosition().x;
		y += cameraHelper.getPosition().y;
		cameraHelper.setPosition(x, y);
	}
	
	/**
	 * Called when game is over.
	 */
	private void gameOver()
	{
		game.setScreen(new MenuScreen(game, true, score));
	}
	
	/**
	 * Frees up memory used by box2d's physics world.
	 */
	@Override
	public void dispose()
	{
		if (b2world != null)
			b2world.dispose();
	}
}