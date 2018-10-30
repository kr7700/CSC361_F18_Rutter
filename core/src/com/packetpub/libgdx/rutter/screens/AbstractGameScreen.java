package com.packetpub.libgdx.rutter.screens;

import com.badlogic.gdx.Screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.packetpub.libgdx.rutter.game.Assets;

/**
 * @author Kevin Rutter
 * Creates a abstract class as the framework of all game screens
 * (Based on CanyonBunny AbstractGameScreen)
 */
public abstract class AbstractGameScreen implements Screen
{
	protected Game game;

	/**
	 * Initializes a screen for this game.
	 * @param game	This game.
	 */
	public AbstractGameScreen(Game game)
	{
		this.game = game;
	}

	/**
	 * Calls for the current instance of the game to be drawn
	 */
	public abstract void render(float deltaTime);

	/**
	 * Allows us to resize the screen
	 */
	public abstract void resize(int width, int height);

	/**
	 * Shows the screen for when it should be used
	 */
	public abstract void show();

	/**
	 * Hides the screen when not being used
	 */
	public abstract void hide();

	/**
	 * Pauses the game
	 */
	public abstract void pause();

	/**
	 * Resumes the game from being paused
	 */
	public void resume()
	{
		Assets.instance.init(new AssetManager());
	}

	/**
	 * Gets rid of unused elements
	 */
	public void dispose()
	{
		Assets.instance.dispose();
	}
}