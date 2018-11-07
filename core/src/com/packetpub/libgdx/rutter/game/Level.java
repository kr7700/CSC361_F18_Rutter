package com.packetpub.libgdx.rutter.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.packetpub.libgdx.rutter.game.objects.AbstractGameObject;
import com.packetpub.libgdx.rutter.game.objects.Background;
import com.packetpub.libgdx.rutter.game.objects.Dirt;
import com.packetpub.libgdx.rutter.game.objects.WaterOverlay;

/**
 * @author Kevin Rutter
 * this method is responsible for loading levels
 * (Based on CanyonBunny Level class)
 */
public class Level
{
	public static final String TAG = Level.class.getName();
	
	
	/**
	 * Custom data type, used for storing level data
	 */
	public enum BLOCK_TYPE
	{
		EMPTY(0, 0, 0), //black
		DIRT(0, 255, 0), //green
		PLAYER_SPAWNPOINT(255, 255, 255), //white
		ITEM_BUG(255, 0, 0), //red
		ITEM_GUN(255, 128, 0), //orange
		ITEM_NORI(255, 0, 255), //purple
		ITEM_RICE_GRAIN(255, 255, 0); //yellow
		
		private int color;
		
		/**
		 * Set the color value for the given rgb values
		 * @param r		red values
		 * @param g		green values
		 * @param b		blue values
		 */
		private BLOCK_TYPE(int r, int g, int b)
		{
			color = r << 24 | g << 16 | b << 8 | 0xff;
		}
		
		/**
		 * @param color		Another block's color
		 * @return			If this block's color matches with the other's
		 */
		public boolean sameColor(int color)
		{
			return this.color == color;
		}
		
		/**
		 * @return		This block's color
		 */
		public int getColor()
		{
			return color;
		}
	}
	
	// objects
	public Array<Dirt> dirtPlatforms;
	
	//decoration
	public Background background;
	public WaterOverlay waterOverlay;
	
	/**
	 * Constructor, calls init
	 * @param filename filename to level layout
	 */
	public Level(String filename)
	{
		init(filename);
	}
	
	/**
	 * Takes in the filename of the level to be loaded then read the file
	 * @param filename	The level filename
	 */
	private void init(String filename)
	{
		//objects
		dirtPlatforms = new Array<Dirt>();
		
		//load image file that represents the level data
		Pixmap pixmap = new Pixmap(Gdx.files.internal(filename));
		//scan pixels from top-left to bottom-right
		int lastPixel = -1;
		for(int pixelY = 0; pixelY < pixmap.getHeight(); pixelY++)
		{
			for(int pixelX = 0; pixelX < pixmap.getWidth(); pixelX++)
			{
				AbstractGameObject obj = null;
				float offsetHeight = 0;
				//height grows from bottom to top
				float baseHeight = pixmap.getHeight() - pixelY;
				//get color of current pixel as 32-bit RGBA value
				int currentPixel = pixmap.getPixel(pixelX, pixelY);
				//find matching color value to identify block type (x,y)
				//point and create the corresponding game object if there is a match
				
				//empty space
				if(BLOCK_TYPE.EMPTY.sameColor(currentPixel))
				{
					//do nothing
				}
				
				//rock
				else if(BLOCK_TYPE.DIRT.sameColor(currentPixel))
				{
					if(lastPixel != currentPixel)
					{
						obj = new Dirt();
						float heightIncreaseFactor = 0.25f;
						offsetHeight = -2.5f;
						obj.position.set(pixelX, baseHeight * obj.dimension.y * heightIncreaseFactor + offsetHeight);
						dirtPlatforms.add((Dirt)obj);
					}else
					{
						dirtPlatforms.get(dirtPlatforms.size - 1).increaseLength(1);
					}
				}
				
				//player spawn point
				else if (BLOCK_TYPE.PLAYER_SPAWNPOINT.sameColor(currentPixel))
				{
				}
				
				//feather
				else if(BLOCK_TYPE.ITEM_BUG.sameColor(currentPixel))
				{
				}
				//gun
				else if(BLOCK_TYPE.ITEM_GUN.sameColor(currentPixel))
				{
				}
				//nori
				else if(BLOCK_TYPE.ITEM_NORI.sameColor(currentPixel))
				{
				}
				//rice grain
				else if(BLOCK_TYPE.ITEM_RICE_GRAIN.sameColor(currentPixel))
				{
				}
				
				//unknown object/pixel color
				else 
				{
					int r = 0xff & (currentPixel >>> 24); //red color channel
					int g = 0xff & (currentPixel >>> 16); //green color channel
					int b = 0xff & (currentPixel >>> 8); //blue color channel
					int a = 0xff & currentPixel; //alpha channel
					Gdx.app.error(TAG, "Unknown object at x<" + pixelX + "> y<" + pixelY + ">: r<" + r + "> g<" + g +"> b<" + b + "> a<" + a + ">");
				}
				lastPixel = currentPixel;
			}
		}
		//decorations
		background = new Background(pixmap.getWidth());
		background.position.set(-1, -1);
		waterOverlay = new WaterOverlay(pixmap.getWidth());
		waterOverlay.position.set(0, -3.75f);
		
		//free memory
		pixmap.dispose();
		Gdx.app.debug(TAG, "level '" + filename + "' loaded");
	}
	
	/**
	 * Handles rendering of objects
	 * @param batch Spritebatch object
	 */
	public void render(SpriteBatch batch) {
		//Draw background
		background.render(batch);
		
		//Draw dirt platforms
		for(Dirt dirt : dirtPlatforms)
			dirt.render(batch);
		
		//Draw Water overlay
		waterOverlay.render(batch);
	}
}
