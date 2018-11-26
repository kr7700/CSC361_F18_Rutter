package com.packetpub.libgdx.rutter.util;

/**
 * @author Kevin Rutter
 * This class contains the Constants to be used in the game
 * (Based on CanyonBunny Constants)
 */
public class Constants
{
	// Visible game world is 5 meters wide
	public static final float VIEWPORT_WIDTH = 5.0f;

	// Visible game world is 5 meters tall
	public static final float VIEWPORT_HEIGHT = 5.0f;
	
	//GUI Width
	public static final float VIEWPORT_GUI_WIDTH = 800.0f;
	
	//GUI Height
	public static final float VIEWPORT_GUI_HEIGHT = 480.0f;
	
	//Location of Description file for texture atlas
	public static final String TEXTURE_ATLAS_OBJECTS = "images/ricerampage.pack.atlas";
	
	// Information for displaying ui elements
	public static final String TEXTURE_ATLAS_UI = "images/ricerampage-ui.pack.atlas";
	public static final String TEXTURE_ATLAS_LIBGDX_UI = "images/uiskin.atlas";
	public static final String SKIN_LIBGDX_UI = "images/uiskin.json";
	public static final String SKIN_RUTTER_UI = "images/ricerampage-ui.json";

	public static final String PREFERENCES = "rutter.prefs";
	
	
	//Location of image file for level 01
	public static final String LEVEL_01 = "levels/level-01.png";
	
	//Amount of extra lives at level start
	public static final int LIVES_START = 3;
}
