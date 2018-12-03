package com.packetpub.libgdx.rutter.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.packetpub.libgdx.rutter.game.objects.AbstractGameObject;
import com.packetpub.libgdx.rutter.game.objects.Background;
import com.packetpub.libgdx.rutter.game.objects.Bug;
import com.packetpub.libgdx.rutter.game.objects.Bullet;
import com.packetpub.libgdx.rutter.game.objects.Dirt;
import com.packetpub.libgdx.rutter.game.objects.Goal;
import com.packetpub.libgdx.rutter.game.objects.Gun;
import com.packetpub.libgdx.rutter.game.objects.Nori;
import com.packetpub.libgdx.rutter.game.objects.RiceBall;
import com.packetpub.libgdx.rutter.game.objects.RiceGrain;
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
		ITEM_RICE_GRAIN(255, 255, 0), //yellow
		GOAL(0, 0, 255); //blue
		
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
	public RiceBall riceBall;
	public Array<Bug> bugs;
	public Array<Gun> guns;
	public Array<Nori> nori;
	public Array<RiceGrain> ricegrains;
	public Array<Bullet> bullets;
	public Goal goal;
	
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
		riceBall = null;
		//objects
		dirtPlatforms = new Array<Dirt>();
		bugs = new Array<Bug>();
		guns = new Array<Gun>();
		nori = new Array<Nori>();
		ricegrains = new Array<RiceGrain>();
		bullets = new Array<Bullet>();
		for (int i = 0; i < 3; i++)
			bullets.add(new Bullet());
		
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
				
				// dirt
				else if(BLOCK_TYPE.DIRT.sameColor(currentPixel))
				{
					if(lastPixel != currentPixel)
					{
						obj = new Dirt();
						float heightIncreaseFactor = 0.25f;
						offsetHeight = -2.5f;
						obj.position.set(pixelX, baseHeight * obj.dimension.y * heightIncreaseFactor + offsetHeight);
						dirtPlatforms.add((Dirt)obj);
					}
					else
					{
						dirtPlatforms.get(dirtPlatforms.size - 1).increaseLength(1);
					}
				}
				
				//player spawn point
				else if (BLOCK_TYPE.PLAYER_SPAWNPOINT.sameColor(currentPixel))
				{
					obj = new RiceBall();
					offsetHeight = -1.5f;
					obj.position.set(pixelX, baseHeight * obj.dimension.y + offsetHeight);
					riceBall = (RiceBall) obj;
				}
				
				//bug
				else if(BLOCK_TYPE.ITEM_BUG.sameColor(currentPixel))
				{
					obj = new Bug();
					offsetHeight = -1.5f;
					obj.position.set(pixelX, baseHeight * obj.dimension.y + offsetHeight);
					bugs.add((Bug) obj);
				}
				
				//gun
				else if(BLOCK_TYPE.ITEM_GUN.sameColor(currentPixel))
				{
					obj = new Gun();
					offsetHeight = -1.5f;
					obj.position.set(pixelX, baseHeight * obj.dimension.y + offsetHeight);
					guns.add((Gun) obj);
				}
				
				//nori
				else if(BLOCK_TYPE.ITEM_NORI.sameColor(currentPixel))
				{
					obj = new Nori();
					offsetHeight = -1.5f;
					obj.position.set(pixelX, baseHeight * obj.dimension.y + offsetHeight);
					nori.add((Nori) obj);
				}
				
				//rice grain
				else if(BLOCK_TYPE.ITEM_RICE_GRAIN.sameColor(currentPixel))
				{
					obj = new RiceGrain();
					offsetHeight = -1.5f;
					obj.position.set(pixelX, baseHeight * obj.dimension.y + offsetHeight);
					ricegrains.add((RiceGrain) obj);
				}
				
				//rice grain
				else if(BLOCK_TYPE.GOAL.sameColor(currentPixel))
				{
					obj = new Goal();
					offsetHeight = -5f;
					obj.position.set(pixelX, baseHeight * obj.dimension.y + offsetHeight);
					goal = (Goal)obj;
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
		
		//Draw bugs
		for (Bug bug : bugs)
			bug.render(batch);
		
		//Draw guns
		for (Gun gun : guns)
			gun.render(batch);
		
		//Draw Nori
		for (Nori individualNori : nori)
			individualNori.render(batch);
		
		//Draw Rice Grains
		for (RiceGrain ricegrain : ricegrains)
			ricegrain.render(batch);
		
		//draw bullets
		for (Bullet bullet : bullets)
			bullet.render(batch);
		
		//Draw Player Character
		riceBall.render(batch);
		
		//Draw Water overlay
		waterOverlay.render(batch);
		
		//draw goal
		goal.render(batch);
	}

	/**
	 * Calls update method of each object in the level.
	 * @param deltaTime		How long since last frame.
	 */
	public void update(float deltaTime)
	{
		riceBall.update(deltaTime);
		for (Dirt dirt : dirtPlatforms)
			dirt.update(deltaTime);
		for (Bug bug : bugs)
			bug.update(deltaTime);
		for (Gun gun : guns)
			gun.update(deltaTime);
		for (Nori individualNori : nori)
			individualNori.update(deltaTime);
		for (RiceGrain ricegrain : ricegrains)
			ricegrain.update(deltaTime);
		for (Bullet bullet: bullets)
			bullet.update(deltaTime);
		goal.update(deltaTime);
	}
}