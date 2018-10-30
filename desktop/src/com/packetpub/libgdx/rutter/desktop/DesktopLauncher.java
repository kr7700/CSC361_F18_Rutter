package com.packetpub.libgdx.rutter.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.packetpub.libgdx.rutter.CSC361_F18_Rutter;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;

/**
 * @Author Kevin Rutter
 * This code will run the game and build a texture atlas if one is not built
 * (Based on CanyonBunny DesktopLauncher)
 */
public class DesktopLauncher
{
	private static boolean rebuildAtlas = false;
	private static boolean drawDebugOutline = false;

	/**
	 * Main class to run the game
	 */
	public static void main(String[] arg)
	{
		if (rebuildAtlas)
		{
			Settings settings = new Settings();
			settings.maxWidth = 1024;
			settings.maxHeight = 1024;
			settings.duplicatePadding = false;
			settings.debug = drawDebugOutline;
			TexturePacker.process(settings, "assets-raw/images", "../core/assets/images", "ricerampage.pack");
			//TexturePacker.process(settings, "assets-raw/images-ui", "../core/assets/images", "ricerampage-ui.pack");
		}
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new CSC361_F18_Rutter(), config);
	}
}
