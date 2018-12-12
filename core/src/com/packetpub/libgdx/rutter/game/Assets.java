package com.packetpub.libgdx.rutter.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.packetpub.libgdx.rutter.util.Constants;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;

/**
 * @author Kevin Rutter
 * Handles texture loading using a texture atlas.
 * (Based on CanyonBunny Assets Class)
 */
public class Assets implements Disposable, AssetErrorListener
{
	public static final String TAG = Assets.class.getName();

	public static final Assets instance = new Assets();

	private AssetManager assetManager;

	public AssetSounds sounds;
	public AssetMusic music;
	public AssetFonts fonts;
	public AssetRiceBall riceball;
	public AssetBug bug;
	public AssetDirt dirt;
	public AssetRiceGrain ricegrain;
	public AssetNori nori;
	public AssetGun gun;
	public AssetBullet bullet;
	public AssetLevelDecoration levelDecoration;
	public AssetGoal goal;

	// singleton: prevent instantiation from other classes
	private Assets()
	{
	}

	/**
	 * Contains fonts for the game.
	 * @author Kevin Rutter
	 */
	public class AssetFonts
	{
		public final BitmapFont defaultSmall;
		public final BitmapFont defaultNormal;
		public final BitmapFont defaultBig;
		
		/**
		 * Initializes fonts
		 */
		public AssetFonts ()
		{
			//Create three fonts using Libgdx' 15px bitmap font
			defaultSmall = new BitmapFont(
					Gdx.files.internal("images/arial-15.fnt"),true);
			defaultNormal = new BitmapFont(
					Gdx.files.internal("images/arial-15.fnt"),true);
			defaultBig = new BitmapFont(
					Gdx.files.internal("images/arial-15.fnt"),true);
			//set font sizes
			defaultSmall.getData().setScale(0.75f);
			defaultNormal.getData().setScale(1.0f);
			defaultBig.getData().setScale(2.0f);
			//enable linear texture filtering for smooth fonts
			defaultSmall.getRegion().getTexture().setFilter(
					TextureFilter.Linear, TextureFilter.Linear);
			defaultNormal.getRegion().getTexture().setFilter(
					TextureFilter.Linear,TextureFilter.Linear);
			defaultBig.getRegion().getTexture().setFilter(
					TextureFilter.Linear, TextureFilter.Linear);
			
		}
	}
	
	/**
	 * Load up the texture atlas.
	 * 
	 * @param assetManager
	 *            The asset manager this class will use.
	 */
	public void init(AssetManager assetManager)
	{
		this.assetManager = assetManager;
		// set asset manager error handler
		assetManager.setErrorListener(this);
		// load texture atlas
		assetManager.load(Constants.TEXTURE_ATLAS_OBJECTS, TextureAtlas.class);
		// load sounds
		assetManager.load("sounds/crunch.wav", Sound.class);
		assetManager.load("sounds/jump.wav", Sound.class);
		assetManager.load("sounds/oof.wav", Sound.class);
		assetManager.load("sounds/gunshot.wav", Sound.class);
		assetManager.load("sounds/reload.wav", Sound.class);
		assetManager.load("sounds/wrapper.wav", Sound.class);
		assetManager.load("sounds/live_lost.wav", Sound.class);
		// load music
		assetManager.load("music/keith303_-_brand_new_highscore.mp3", Music.class);
		assetManager.load("music/E1M1.mp3", Music.class);
		assetManager.load("music/doom2m10.mp3", Music.class);
		// start loading assets and wait until finished
		assetManager.finishLoading();
		Gdx.app.debug(TAG, "# of assets loaded: " + assetManager.getAssetNames().size);
		for (String a : assetManager.getAssetNames())
			Gdx.app.debug(TAG, "asset: " + a);

		TextureAtlas atlas = assetManager.get(Constants.TEXTURE_ATLAS_OBJECTS);

		// enable texture filtering for pixel smoothing
		for (Texture t : atlas.getTextures()) {
			t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		}

		// create game resource objects
		fonts = new AssetFonts();
		sounds = new AssetSounds(assetManager);
		music = new AssetMusic(assetManager);
		riceball = new AssetRiceBall(atlas);
		bug = new AssetBug(atlas);
		dirt = new AssetDirt(atlas);
		ricegrain = new AssetRiceGrain(atlas);
		nori = new AssetNori(atlas);
		gun = new AssetGun(atlas);
		bullet = new AssetBullet(atlas);
		levelDecoration = new AssetLevelDecoration(atlas);
		goal = new AssetGoal(atlas);
	}

	/**
	 * Tell the asset manager to to unload assets.
	 */
	@Override
	public void dispose()
	{
		assetManager.dispose();
		fonts.defaultSmall.dispose();
		fonts.defaultNormal.dispose();
		fonts.defaultBig.dispose();
	}

	/**
	 * Called when the asset manager has an error with an asset, prints out an error
	 * log.
	 */
	@Override
	public void error(AssetDescriptor asset, Throwable throwable)
	{
		Gdx.app.error(TAG, "Couldn't load asset '" + asset.fileName + "'", (Exception) throwable);
	}

	/**
	 * @author Kevin Rutter
	 * Initializes and holds the game's decorative textures.
	 */
	public class AssetLevelDecoration
	{
		public final AtlasRegion grass;
		public final AtlasRegion fence;
		public final AtlasRegion waterOverlay;

		/**
		 * Initialize atlas regions for decorations.
		 * 
		 * @param atlas
		 *            The texture atlas being used.
		 */
		public AssetLevelDecoration(TextureAtlas atlas)
		{
			grass = atlas.findRegion("grass");
			fence = atlas.findRegion("fence");
			waterOverlay = atlas.findRegion("water_overlay");
		}
	}

	/**
	 * @author Kevin Rutter
	 * This class holds info for the riceball texture
	 */
	public class AssetRiceBall
	{
		public final AtlasRegion riceball;
		public final AtlasRegion riceballhappy;

		/**
		 * Sets riceball to hold the reference to the correct region from atlas.
		 * 
		 * @param atlas
		 *            Texture atlas
		 */
		public AssetRiceBall(TextureAtlas atlas)
		{
			riceball = atlas.findRegion("riceball");
			if (riceball != null)
			{
				System.out.println("ball isnt null");
			}
			riceballhappy = atlas.findRegion("riceballhappy");
			if (riceballhappy == null)
			{
				System.out.println("happy is null");
			}
		}
	}
	
	/**
	 * @author Kevin Rutter
	 * This class holds info for the bug texture
	 */
	public class AssetBug
	{
		public final AtlasRegion bug;
		public final Animation animBug;
		public final Animation animDead;

		/**
		 * Sets bug to hold the reference to the correct region from atlas.
		 * 
		 * @param atlas
		 *            Texture atlas
		 */
		public AssetBug(TextureAtlas atlas)
		{
			bug = atlas.findRegion("bug");
			Array<AtlasRegion> regions = null;
			regions = atlas.findRegions("anim_bug");
			System.out.println("bug regions: "+regions.size);
			animBug = new Animation(1.0f/2.0f, regions,
					Animation.PlayMode.LOOP_PINGPONG);
			regions = null;
			regions = atlas.findRegions("anim_dead");
			animDead = new Animation(1.0f/8.0f, regions,
					Animation.PlayMode.NORMAL);
		}
	}

	/**
	 * @author Kevin Rutter
	 * This class holds info for dirt edge and middle texture
	 */
	public class AssetDirt
	{
		public final AtlasRegion edge;
		public final AtlasRegion middle;

		/**
		 * sets edge and middle to hold references to the appropriate areas
		 * 
		 * @param atlas
		 *            Texture atlas
		 */
		public AssetDirt(TextureAtlas atlas)
		{
			edge = atlas.findRegion("dirt_edge");
			middle = atlas.findRegion("dirt_middle");
		}
	}

	/**
	 * @author Kevin Rutter
	 * This class holds info for the rice grain texture
	 */
	public class AssetRiceGrain
	{
		public final AtlasRegion ricegrain;

		/**
		 * Sets ricegrain to hold the reference to the correct region in the atlas
		 * 
		 * @param atlas
		 *            Texture atlas
		 */
		public AssetRiceGrain(TextureAtlas atlas)
		{
			ricegrain = atlas.findRegion("item_rice_grain");
		}
	}

	/**
	 * @author Kevin Rutter
	 * This class holds info for the nori texture
	 */
	public class AssetNori
	{
		public final AtlasRegion nori;

		/**
		 * Sets nori to hold the reference to the correct region from the atlas
		 * 
		 * @param atlas
		 *            Texture atlas
		 */
		public AssetNori(TextureAtlas atlas)
		{
			nori = atlas.findRegion("item_nori");
		}
	}
	
	/**
	 * @author Kevin Rutter
	 * This class holds info for the gun texture
	 */
	public class AssetGun
	{
		public final AtlasRegion gun;

		/**
		 * Sets gun to hold the reference to the correct region from the atlas
		 * 
		 * @param atlas
		 *            Texture atlas
		 */
		public AssetGun(TextureAtlas atlas)
		{
			gun = atlas.findRegion("item_gun");
		}
	}
	
	/**
	 * @author Kevin Rutter
	 * This class holds info for the bullet texture
	 */
	public class AssetBullet
	{
		public final AtlasRegion bullet;

		/**
		 * Sets gun to hold the reference to the correct region from the atlas
		 * 
		 * @param atlas
		 *            Texture atlas
		 */
		public AssetBullet(TextureAtlas atlas)
		{
			bullet = atlas.findRegion("bullet");
		}
	}
	
	/**
	 * @author Kevin Rutter
	 * This class holds info for the goal texture
	 */
	public class AssetGoal
	{
		public final AtlasRegion goal;

		/**
		 * Sets gun to hold the reference to the correct region from the atlas
		 * 
		 * @param atlas
		 *            Texture atlas
		 */
		public AssetGoal(TextureAtlas atlas)
		{
			goal = atlas.findRegion("goal");
		}
	}
	
	/**
	 * @Author Kevin Rutter
	 * This class holds info for the sounds
	 */
	public class AssetSounds
	{
		public final Sound jump;
		public final Sound liveLost;
		public final Sound gunshot;
		public final Sound oof;
		public final Sound reload;
		public final Sound wrapper;
		public final Sound crunch;

		/**
		 * Gets all of the sounds.
		 * @param am	The assetmanager
		 */
		public AssetSounds(AssetManager am)
		{
			jump = am.get("sounds/jump.wav", Sound.class);
			liveLost = am.get("sounds/live_lost.wav", Sound.class);
			gunshot = am.get("sounds/gunshot.wav", Sound.class);
			oof = am.get("sounds/oof.wav", Sound.class);
			reload = am.get("sounds/reload.wav", Sound.class);
			wrapper = am.get("sounds/wrapper.wav", Sound.class);
			crunch = am.get("sounds/crunch.wav", Sound.class);
		}
	}

	/**
	 * @Author Kevin Rutter
	 * This class holds info for the game music
	 */
	public class AssetMusic
	{
		public final Music song01;
		public final Music song02;
		public final Music menuMusic;

		/**
		 * Gets all of the music.
		 * @param am	The assetmanager
		 */
		public AssetMusic(AssetManager am)
		{
			song01 = am.get("music/keith303_-_brand_new_highscore.mp3", Music.class);
			song02 = am.get("music/E1M1.mp3", Music.class);
			menuMusic = am.get("music/doom2m10.mp3", Music.class);
		}
	}
}