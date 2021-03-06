package com.packetpub.libgdx.rutter.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.packetpub.libgdx.rutter.util.Constants;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * @author Kevin Rutter
 * Contains methods for displaying the world
 * (Based on CanyonBunny WorldRenderer)
 */
public class WorldRenderer implements Disposable
{
	private OrthographicCamera camera;
	private OrthographicCamera cameraGUI;
	private SpriteBatch batch;
	private WorldController worldController;

	/**
	 * Constructor for WorldRenderer, gets worldController then calls init
	 * @param worldController	The worldController being used by the game.
	 */
	public WorldRenderer(WorldController worldController)
	{
		this.worldController = worldController;
		init();
	}

	/**
	 * Initializes the batch and camera variables.
	 * Camera is set to the VIEWPORT_WIDTH and HEIGHT found in constants class, then its position is set to 0,0,0
	 */
	private void init()
	{
		batch = new SpriteBatch();

		// set up camera
		camera = new OrthographicCamera(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
		camera.position.set(0, 0, 0);

		// update camera with changes
		camera.update();
		cameraGUI = new OrthographicCamera(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT);
		cameraGUI.position.set(0, 0, 0);
		cameraGUI.setToOrtho(true);
		cameraGUI.update();
	}

	/**
	 * In charge of rendering the current game state.
	 */
	public void render()
	{
		renderWorld(batch);
		renderGui(batch);
	}
	
	/**
	 * When called resizes the camera to the called height and width
	 * 
	 * @param width  Desired Height
	 * @param height Desired Width
	 */
	public void resize(int width, int height)
	{
		camera.viewportWidth = (Constants.VIEWPORT_HEIGHT / height) * width;
		camera.update();
		
		cameraGUI.viewportHeight = Constants.VIEWPORT_GUI_HEIGHT;
		cameraGUI.viewportWidth = (Constants.VIEWPORT_GUI_HEIGHT / (float)height) * (float)width;
		
		cameraGUI.position.set(cameraGUI.viewportWidth / 2, cameraGUI.viewportHeight / 2, 0);
		cameraGUI.update();
	}

	/**
	 * Responsible for rendering the world
	 * @param batch SpriteBatch object
	 */
	private void renderWorld(SpriteBatch batch)
	{
		worldController.cameraHelper.applyTo(camera);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		worldController.level.render(batch);
		batch.end();
	}
	
	/**
	 * Responsible for render score gui element
	 * @param batch Spritebatch object
	 */
	private void renderGuiScore (SpriteBatch batch)
	{
		float x = -15;
		float y = - 15;
		
		batch.draw(Assets.instance.ricegrain.ricegrain, x, y, 50, 50, 100, 100, 0.35f, -0.35f, 0);
		Assets.instance.fonts.defaultBig.draw(batch, "" + worldController.score + "   (" + worldController.highscore + ")", x + 75, y + 37);
	}
	
	/**
	 * Responsible for rendering extra lives
	 * @param batch Spritebatch object
	 */
	private void renderGuiExtraLive(SpriteBatch batch)
	{
		float x = cameraGUI.viewportWidth - 50 - Constants.LIVES_START * 50;
		float y = -15;
		for(int i = 0; i < Constants.LIVES_START; i++)
		{
			if(worldController.lives <= i)
				batch.setColor(0.5f, 0.5f, 0.5f, 0.5f);
			
			batch.draw(Assets.instance.riceball.riceball, x + i * 50, y, 50, 50, 120, 100, 0.35f, -0.35f, 0);
			batch.setColor(1, 1, 1, 1);
		}
	}
	
	/**
	 * Responsible for rendering bullets
	 * @param batch Spritebatch object
	 */
	private void renderGuiBullets(SpriteBatch batch)
	{
		float x = cameraGUI.viewportWidth - 50 - 3 * 50;
		float y = 35;
		for(int i = 0; i < 3; i++)
		{
			if(worldController.level.riceBall.bullets <= i)
				batch.setColor(0.5f, 0.5f, 0.5f, 0.5f);
			
			batch.draw(Assets.instance.bullet.bullet, x + i * 50, y, 50, 50, 120, 100, 0.35f, -0.35f, 0);
			batch.setColor(1, 1, 1, 1);
		}
	}
	
	/**
	 * responsible for rendering fps counter
	 * @param batch		The spritebatch we are using to draw.
	 */
	private void renderGuiFpsCounter (SpriteBatch batch)
	{
		float x = cameraGUI.viewportWidth -55;
		float y = cameraGUI.viewportHeight - 15;
		int fps = Gdx.graphics.getFramesPerSecond();
		BitmapFont fpsFont = Assets.instance.fonts.defaultNormal;
		if(fps >= 45)
		{
			//45 or more FPS show up in green
			fpsFont.setColor(0, 1, 0, 1);
		}
		else if(fps >= 30)
		{
			//30 or more FPS show up in yellow
			fpsFont.setColor(1, 1, 0, 1);
		}
		else
		{
			//less than 30 FPS show up in red
			fpsFont.setColor(1, 0, 0, 1);
		}
		fpsFont.draw(batch, "FPS: " + fps, x, y);
		fpsFont.setColor(1, 1, 1, 1); //white
	}
	
	/**
	 * Calls rendering methods for GUI
	 * @param batch Sprite batch object
	 */
	private void renderGui(SpriteBatch batch)
	{
		batch.setProjectionMatrix(cameraGUI.combined);
		batch.begin();
		//draw collected rice grains + text
		// (anchored to top left edge)
		renderGuiScore(batch);
		//draw extra lives icon + text (anchored to top right edge)
		renderGuiExtraLive(batch);
		//draw amount of bullets left
		renderGuiBullets(batch);
		//draw FPS text (anchored to bottom right edge)
		renderGuiFpsCounter(batch);
		batch.end();
	}

	/**
	 * Calls for the batch to be disposed of
	 */
	@Override
	public void dispose()
	{
		batch.dispose();
	}
}