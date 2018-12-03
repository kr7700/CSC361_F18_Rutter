package com.packetpub.libgdx.rutter;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.packetpub.libgdx.rutter.game.Assets;
import com.packetpub.libgdx.rutter.screens.GameScreen;
import com.packetpub.libgdx.rutter.screens.MenuScreen;
//import com.packetpub.libgdx.rutter.util.AudioManager;
import com.packetpub.libgdx.rutter.util.GamePreferences;

/**
 * The new Main class that works as an application listener
 * 
 * @author Kevin Rutter
 */

public class CSC361_F18_Rutter extends Game
{
	@Override
	public void create()
	{
		// Set Libgdx log level
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		// Load assets
		Assets.instance.init(new AssetManager());
		// Load preferences for audio settings and start playing music
		GamePreferences.instance.load();
		//AudioManager.instance.play(Assets.instance.music.song01);
		// Start game at menu screen
		setScreen(new MenuScreen(this, false, 0));
	}
}