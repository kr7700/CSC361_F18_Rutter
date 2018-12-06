package com.packetpub.libgdx.rutter.screens;

import com.badlogic.gdx.Game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.packetpub.libgdx.rutter.game.Assets;
import com.packetpub.libgdx.rutter.util.AudioManager;
import com.packetpub.libgdx.rutter.util.Constants;
import com.packetpub.libgdx.rutter.util.GamePreferences;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.Touchable;

/**
 * This screen is for the main menu of the game. Has play button, option button,
 * etc.
 * (Based on CanyonBunny MenuScreen)
 * 
 * @author Kevin Rutter
 */
public class MenuScreen extends AbstractGameScreen
{
	private static final String TAG = MenuScreen.class.getName();

	private Stage stage;
	private Skin skinRutter;

	private Skin skinLibgdx;

	// menu
	private Image imgBackground;
	private Image imgLogo;
	private Image imgInfo;
	private Image imgRice;
	private Image imgBall;
	private Button btnMenuPlay;
	private Button btnMenuOptions;
	private Button btnMenuScore;

	// options
	private Window winOptions;
	private TextButton btnWinOptSave;
	private TextButton btnWinOptCancel;
	private CheckBox chkSound;
	private Slider sldSound;
	private CheckBox chkMusic;
	private Slider sldMusic;
	private CheckBox chkShowFpsCounter;

	// debug
	private final float DEBUG_REBUILD_INTERVAL = 5.0f;
	private boolean debugEnabled = false;
	private float debugRebuildStage;
	
	// high score
	private Window winScore;
	private TextButton btnWinScoreExit;
	private String[] scoreNames = new String[10];
	private int[] scoreNums = new int[10];
	private boolean scoresGotten = false;

	// score entry
	private Window winScoreEntry;
	private TextField txt;
	private TextButton btnWinScoreEnter;
	private boolean gameOver;
	private int score;
	
	/**
	 * Constructor for the screen that holds the main menu of the game.
	 * 
	 * @param game
	 *            The applicationListener for the game. (CSC361_F18_Rutter)
	 */
	public MenuScreen(Game game, boolean gameOver, int score)
	{
		super(game);
		this.gameOver = gameOver;
		this.score = score;
	}

	/**
	 * Puts together all the layers of the menu screen.
	 */
	private void rebuildStage()
	{
		skinRutter = new Skin(Gdx.files.internal(Constants.SKIN_RUTTER_UI),
				new TextureAtlas(Constants.TEXTURE_ATLAS_UI));

		skinLibgdx = new Skin(Gdx.files.internal(Constants.SKIN_LIBGDX_UI),
				new TextureAtlas(Constants.TEXTURE_ATLAS_LIBGDX_UI));

		if (!scoresGotten)
			getScores();
		
		// build all layers
		Table layerBackground = buildBackgroundLayer();
		Table layerObjects = buildObjectsLayer();
		Table layerLogos = buildLogosLayer();
		Table layerControls = buildControlsLayer();
		Table layerOptionsWindow = buildOptionsWindowLayer();
		Table layerScoreWindow = buildScoreWindowLayer();

		// assemble stage for menu screen
		stage.clear();
		Stack stack = new Stack();
		stage.addActor(stack);
		stack.setSize(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT);
		stack.add(layerBackground);
		stack.add(layerObjects);
		stack.add(layerLogos);
		stack.add(layerControls);
		stage.addActor(layerOptionsWindow);
		stage.addActor(layerScoreWindow);
		
		if (gameOver)
		{
			stage.addActor(buildScoreEntryWindowLayer());
			showScoreEntryWindow(true, false);
			showMenuButtons(false);
		}
	}

	/**
	 * Builds the score name entry window.
	 * 
	 * @return The score window.
	 */
	private Table buildScoreEntryWindowLayer()
	{
		winScoreEntry = new Window("Game Over", skinLibgdx);

		Label lbl = new Label("Your score was " + score + "!", skinLibgdx);
		winScoreEntry.add(lbl).row();
		lbl = new Label("Enter your name", skinLibgdx);
		winScoreEntry.add(lbl).row();
		txt = new TextField("", skinLibgdx);
		winScoreEntry.add(txt).row();
		//Gdx.input.setInputProcessor(txt);
		winScoreEntry.add(buildScoreEntryButton());
		
		// Make score window slightly transparent
		winScoreEntry.setColor(1, 1, 1, 0.8f);
		// Let TableLayout recalculate widget sizes and positions
		winScoreEntry.pack();
		// Move options window to bottom right corner
		winScoreEntry.setPosition(Constants.VIEWPORT_GUI_WIDTH - winScore.getWidth() - 50, 50);

		return winScoreEntry;
	}
	
	/**
	 * Builds a table that contains the enter button in the score entry window.
	 * 
	 * @return exit button table.
	 */
	private Table buildScoreEntryButton()
	{
		Table tbl = new Table();
		// + Separator
		Label lbl = null;
		lbl = new Label("", skinLibgdx);
		lbl.setColor(0.75f, 0.75f, 0.75f, 1);
		lbl.setStyle(new LabelStyle(lbl.getStyle()));
		lbl.getStyle().background = skinLibgdx.newDrawable("white");
		tbl.add(lbl).colspan(2).height(1).width(220).pad(0, 0, 0, 1);
		tbl.row();
		lbl = new Label("", skinLibgdx);
		lbl.setColor(0.5f, 0.5f, 0.5f, 1);
		lbl.setStyle(new LabelStyle(lbl.getStyle()));
		lbl.getStyle().background = skinLibgdx.newDrawable("white");
		tbl.add(lbl).colspan(2).height(1).width(220).pad(0, 1, 5, 0);
		tbl.row();

		// + enter Button with event handler
		btnWinScoreEnter = new TextButton("Enter", skinLibgdx);
		tbl.add(btnWinScoreEnter);
		btnWinScoreEnter.addListener(new ChangeListener()
		{
			@Override
			public void changed(ChangeEvent event, Actor actor)
			{
				onEnterClicked();
			}
		});
		return tbl;
	}
	
	/**
	 * Closes the entry window, returns to the main menu.
	 */
	private void onEnterClicked()
	{
		updateScoreFile(txt.getText());
		winScoreEntry.setVisible(false);
		gameOver = false;
		showMenuButtons(true);
		showOptionsWindow(false,true);
		btnMenuPlay.setVisible(true);
		btnMenuOptions.setVisible(true);
		btnMenuScore.setVisible(true);
		winOptions.setVisible(false);
		//AudioManager.instance.onSettingsUpdated();
	}
	
	/**
	 * Updates the score text file.
	 * @param name	Name of current player.
	 */
	public void updateScoreFile(String name)
	{
//		String[] scoreNames = new String[10];
//		int[] scoreNums = new int[10];
		File file = new File(Constants.SCORES);
//		BufferedReader br = null;
//		try
//		{
//			br = new BufferedReader(new FileReader(file));
//		}
//		catch (FileNotFoundException e)
//		{
//			e.printStackTrace();
//		}
//		
//		String line;
//		int i = 0;
//		try
//		{
//			while((line = br.readLine()) != null)
//			{
//				scoreNames[i] = line;
//				line = br.readLine();
//				scoreNums[i] = Integer.parseInt(line);
//				i++;
//			}
//		}
//		catch (IOException e)
//		{
//			e.printStackTrace();
//		}
//		
//		System.out.println("Before:");
//		
//		for(i = 0; i < 10; i++)
//		{
//			System.out.println(scoreNames[i]);
//			System.out.println(scoreNums[i]);
//		}
		int i;
		int newPosition = 10;
		boolean notFound = true;
		for (i = 0; i < 10 & notFound; i++)
		{
			if (score > scoreNums[i])
			{
				newPosition = i;
				notFound = false;
			}
		}
		
		if (newPosition < 10)
		{
			i = 9;
			while(i != newPosition)
			{
				scoreNums[i] = scoreNums[i-1];
				scoreNames[i] = scoreNames[i-1];
				i--;
			}
			scoreNums[i] = score;
			scoreNames[i] = name;
			System.out.println("placed "+ name+" in position "+i);
		}
		
		System.out.println("sorted out score");
		System.out.println("After:");
		for(i = 0; i < 10; i++)
		{
			System.out.println(scoreNames[i]);
			System.out.println(scoreNums[i]);
		}
		
		System.out.println(name);
		
		FileWriter writer = null;
		try
		{
			writer = new FileWriter(file, false);
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
		
		for (i = 0; i < 10; i++)
		{
			try
			{
				writer.write(scoreNames[i] + "\n");
				writer.write(scoreNums[i] + "\n");
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}	
		}
		
		try
		{
			writer.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		stage.addActor(buildScoreWindowLayer());
	}
	
	
	/**
	 * Creates the background layer of the menu screen.
	 * 
	 * @return The background layer.
	 */
	private Table buildBackgroundLayer()
	{
		Table layer = new Table();
		// + Background
		imgBackground = new Image(skinRutter, "background");
		layer.add(imgBackground);
		return layer;
	}

	/**
	 * Creates a the objects layer of the menu, with rice grains and a riceball
	 * 
	 * @return The object layer.
	 */
	private Table buildObjectsLayer()
	{
		Table layer = new Table();
		// + Rice Grains
		imgRice = new Image(skinRutter, "coins");
		layer.addActor(imgRice);
		imgRice.setPosition(135, 80);
		//imgRice.setOrigin(imgRice.getWidth()/2,imgRice.getHeight() /2);
		//imgRice.addAction(sequence(moveTo(135, -20), scaleTo(0,0),fadeOut(0),delay(2.5f),parallel(moveBy(0,100,0.5f,Interpolation.swingOut),scaleTo(1.0f,1.0f,0.25f,Interpolation.linear),alpha(1.0f,0.5f))));
		// + Rice Ball
		imgBall = new Image(skinRutter, "bunny");
		layer.addActor(imgBall);
		//imgBall.addAction(sequence(moveTo(655,510),delay(4.0f),moveBy(-70,-100,0.5f, Interpolation.fade),moveBy(-100,-50,0.5f,Interpolation.fade),moveBy(-150,-300,1.0f,Interpolation.elasticIn)));
		imgBall.setPosition(355, 40);
		return layer;
	}

	/**
	 * Builds the logos layer of the menu screen.
	 * 
	 * @return The logos layer.
	 */
	private Table buildLogosLayer()
	{
		Table layer = new Table();
		layer.left().top();
		// + Game Logo
		imgLogo = new Image(skinRutter, "logo");
		layer.add(imgLogo);
		layer.row().expandY();
		// + Info Logos
		imgInfo = new Image(skinRutter, "info");
		layer.add(imgInfo).bottom();
		if (debugEnabled)
			layer.debug();
		return layer;
	}

	/**
	 * Responsible for building layer with actual actions to it
	 * 
	 * @return finished layer
	 */
	private Table buildControlsLayer()
	{
		Table layer = new Table();

		layer.right().bottom();
		// + Play Button
		btnMenuPlay = new Button(skinRutter, "play");
		layer.add(btnMenuPlay);
		btnMenuPlay.addListener(new ChangeListener()
		{
			@Override
			public void changed(ChangeEvent event, Actor actor)
			{
				onPlayClicked();
			}
		});

		layer.row();
		// + Options Button
		btnMenuOptions = new Button(skinRutter, "options");
		layer.add(btnMenuOptions);
		btnMenuOptions.addListener(new ChangeListener()
		{
			@Override
			public void changed(ChangeEvent event, Actor actor)
			{
				onOptionsClicked();
			}
		});

		layer.row();
		// + High Score Button
		btnMenuScore = new Button(skinRutter, "highscores");
		layer.add(btnMenuScore);
		btnMenuScore.addListener(new ChangeListener()
		{
			@Override
			public void changed(ChangeEvent event, Actor actor)
			{
				onHighScoreClicked();
			}
		});
		
		if (debugEnabled)
			layer.debug();
		
		return layer;
	}

	/**
	 * This gets called when Play button gets clicked
	 */
	private void onPlayClicked()
	{
		AudioManager.instance.stopMusic();
		AudioManager.instance.play(Assets.instance.music.song02);
		game.setScreen(new GameScreen(game));
	}

	/**
	 * Builds the options window for the menu screen.
	 * 
	 * @return The options window.
	 */
	private Table buildOptionsWindowLayer()
	{
		winOptions = new Window("Options", skinLibgdx);
		// + Audio Settings: Sound/Music CheckBox and Volume Slider
		winOptions.add(buildOptWinAudioSettings()).row();
		// + Debug: Show FPS Counter
		winOptions.add(buildOptWinDebug()).row();
		// + Separator and Buttons (Save, Cancel)
		winOptions.add(buildOptWinButtons()).pad(10, 0, 10, 0);

		// Make options window slightly transparent
		winOptions.setColor(1, 1, 1, 0.8f);
		// Hide options window by default
		showOptionsWindow(false,false);
		//winOptions.setVisible(false);
		if (debugEnabled)
			winOptions.debug();
		// Let TableLayout recalculate widget sizes and positions
		winOptions.pack();
		// Move options window to bottom right corner
		winOptions.setPosition(Constants.VIEWPORT_GUI_WIDTH - winOptions.getWidth() - 50, 50);
		return winOptions;
	}

	/**
	 * Builds the audio table for the options window.
	 * 
	 * @return The audio options table.
	 */
	private Table buildOptWinAudioSettings()
	{
		Table tbl = new Table();
		// + Title: "Audio"
		tbl.pad(10, 10, 0, 10);
		tbl.add(new Label("Audio", skinLibgdx, "default-font", Color.ORANGE)).colspan(3);
		tbl.row();
		tbl.columnDefaults(0).padRight(10);
		tbl.columnDefaults(1).padRight(10);
		// + Checkbox, "Sound" label, sound volume slider
		chkSound = new CheckBox("", skinLibgdx);
		tbl.add(chkSound);
		tbl.add(new Label("Sound", skinLibgdx));
		sldSound = new Slider(0.0f, 1.0f, 0.1f, false, skinLibgdx);
		tbl.add(sldSound);
		tbl.row();
		// + Checkbox, "Music" label, music volume slider
		chkMusic = new CheckBox("", skinLibgdx);
		tbl.add(chkMusic);
		tbl.add(new Label("Music", skinLibgdx));
		sldMusic = new Slider(0.0f, 1.0f, 0.1f, false, skinLibgdx);
		tbl.add(sldMusic);
		tbl.row();
		return tbl;
	}

	/**
	 * Builds a table that contains debug settings.
	 * 
	 * @return Debug options table.
	 */
	private Table buildOptWinDebug()
	{
		Table tbl = new Table();
		// + Title: "Debug"
		tbl.pad(10, 10, 0, 10);
		tbl.add(new Label("Debug", skinLibgdx, "default-font", Color.RED)).colspan(3);
		tbl.row();
		tbl.columnDefaults(0).padRight(10);
		tbl.columnDefaults(1).padRight(10);
		// + Checkbox, "Show FPS Counter" label
		chkShowFpsCounter = new CheckBox("", skinLibgdx);
		tbl.add(new Label("Show FPS Counter", skinLibgdx));
		tbl.add(chkShowFpsCounter);
		tbl.row();
		return tbl;
	}

	/**
	 * Builds a table that contains the save and cancel buttons in options window.
	 * 
	 * @return Save/cancel buttons table.
	 */
	private Table buildOptWinButtons()
	{
		Table tbl = new Table();
		// + Separator
		Label lbl = null;
		lbl = new Label("", skinLibgdx);
		lbl.setColor(0.75f, 0.75f, 0.75f, 1);
		lbl.setStyle(new LabelStyle(lbl.getStyle()));
		lbl.getStyle().background = skinLibgdx.newDrawable("white");
		tbl.add(lbl).colspan(2).height(1).width(220).pad(0, 0, 0, 1);
		tbl.row();
		lbl = new Label("", skinLibgdx);
		lbl.setColor(0.5f, 0.5f, 0.5f, 1);
		lbl.setStyle(new LabelStyle(lbl.getStyle()));
		lbl.getStyle().background = skinLibgdx.newDrawable("white");
		tbl.add(lbl).colspan(2).height(1).width(220).pad(0, 1, 5, 0);
		tbl.row();

		// + Save Button with event handler
		btnWinOptSave = new TextButton("Save", skinLibgdx);
		tbl.add(btnWinOptSave).padRight(30);
		btnWinOptSave.addListener(new ChangeListener()
		{
			@Override
			public void changed(ChangeEvent event, Actor actor)
			{
				onSaveClicked();
			}
		});

		// + Cancel Button with event handler
		btnWinOptCancel = new TextButton("Cancel", skinLibgdx);
		tbl.add(btnWinOptCancel);
		btnWinOptCancel.addListener(new ChangeListener()
		{
			@Override
			public void changed(ChangeEvent event, Actor actor)
			{
				onCancelClicked();
			}
		});

		return tbl;
	}
	
	/**
	 * Builds the high score window for the menu screen.
	 * 
	 * @return The score window.
	 */
	private Table buildScoreWindowLayer()
	{
		winScore = new Window("High Scores", skinLibgdx);
		winScore.add(buildScoreWinRows()).row();

		// + Separator and Exit button
		winScore.add(buildScoreWinButton());

		// Make score window slightly transparent
		winScore.setColor(1, 1, 1, 0.8f);
		// Hide score window by default
		showHighScoreWindow(false,false);
		if (debugEnabled)
			winScore.debug();
		// Let TableLayout recalculate widget sizes and positions
		winScore.pack();
		// Move options window to bottom right corner
		winScore.setPosition(Constants.VIEWPORT_GUI_WIDTH - winOptions.getWidth() - 50, 50);
		return winScore;
	}
	
	/**
	 * Gets the scores from the high score file.
	 */
	private void getScores()
	{
		File file = new File(Constants.SCORES);
		BufferedReader br = null;
		try
		{
			br = new BufferedReader(new FileReader(file));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		
		String line;
		int i = 0;
		try
		{
			while((line = br.readLine()) != null)
			{
				scoreNames[i] = line;
				line = br.readLine();
				scoreNums[i] = Integer.parseInt(line);
				i++;
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		scoresGotten = true;
	}
	
	/**
	 * Builds the table that contains the high scores.
	 * 
	 * @return	The high score table.
	 */
	private Table buildScoreWinRows()
	{
		Table tbl = new Table();
		for (int i = 0; i < 10; i++)
		{
			Label lbl = null;
			lbl = new Label(i+1 + ".      ", skinLibgdx);
			tbl.add(lbl);
			
		    String score = ""+scoreNums[i];
			lbl = new Label(score, skinLibgdx);
			tbl.add(lbl);
		    for (int j = 0; j < 10; j++)
		    {
		    	lbl = new Label(" ", skinLibgdx);
		    	tbl.add(lbl);
		    }
			
			lbl = new Label(scoreNames[i], skinLibgdx);
			tbl.add(lbl);
		    for (int j = 0; j < 10; j++)
		    {
		    	lbl = new Label(" ", skinLibgdx);
		    	tbl.add(lbl);
		    }
		    
			tbl.row();
		}
		return tbl;
	}

	/**
	 * Builds a table that contains the exit button in the high score window.
	 * 
	 * @return exit button table.
	 */
	private Table buildScoreWinButton()
	{
		Table tbl = new Table();
		// + Separator
		Label lbl = null;
		lbl = new Label("", skinLibgdx);
		lbl.setColor(0.75f, 0.75f, 0.75f, 1);
		lbl.setStyle(new LabelStyle(lbl.getStyle()));
		lbl.getStyle().background = skinLibgdx.newDrawable("white");
		tbl.add(lbl).colspan(2).height(1).width(220).pad(0, 0, 0, 1);
		tbl.row();
		lbl = new Label("", skinLibgdx);
		lbl.setColor(0.5f, 0.5f, 0.5f, 1);
		lbl.setStyle(new LabelStyle(lbl.getStyle()));
		lbl.getStyle().background = skinLibgdx.newDrawable("white");
		tbl.add(lbl).colspan(2).height(1).width(220).pad(0, 1, 5, 0);
		tbl.row();

		// + exit Button with event handler
		btnWinScoreExit = new TextButton("Exit", skinLibgdx);
		tbl.add(btnWinScoreExit);
		btnWinScoreExit.addListener(new ChangeListener()
		{
			@Override
			public void changed(ChangeEvent event, Actor actor)
			{
				onExitClicked();
			}
		});
		return tbl;
	}
	
	/**
	 * Loads the previously set settings to the options menu.
	 */
	private void loadSettings()
	{
		GamePreferences prefs = GamePreferences.instance;
		prefs.load();
		chkSound.setChecked(prefs.sound);
		sldSound.setValue(prefs.volSound);
		chkMusic.setChecked(prefs.music);
		sldMusic.setValue(prefs.volMusic);
		chkShowFpsCounter.setChecked(prefs.showFpsCounter);
	}

	/**
	 * Loads the options window, hides other buttons.
	 */
	private void onOptionsClicked()
	{
		loadSettings();
		showMenuButtons(false);
		showOptionsWindow(true,true);
		btnMenuPlay.setVisible(false);
		btnMenuOptions.setVisible(false);
		btnMenuScore.setVisible(false);
		winOptions.setVisible(true);
	}
	
	/**
	 * Loads the high score menu, hides other buttons.
	 */
	private void onHighScoreClicked()
	{
		showMenuButtons(false);
		showHighScoreWindow(true,true);
		btnMenuPlay.setVisible(false);
		btnMenuOptions.setVisible(false);
		btnMenuScore.setVisible(false);
		winOptions.setVisible(true);
	}

	/**
	 * Saves changes made in settings, and returns to the main menu.
	 */
	private void onSaveClicked()
	{
		saveSettings();
		onCancelClicked();
		//AudioManager.instance.onSettingsUpdated();
	}

	/**
	 * Closes the options window, returns to the main menu.
	 */
	private void onCancelClicked()
	{
		showMenuButtons(true);
		showOptionsWindow(false,true);
		btnMenuPlay.setVisible(true);
		btnMenuOptions.setVisible(true);
		btnMenuScore.setVisible(true);
		winOptions.setVisible(false);
		//AudioManager.instance.onSettingsUpdated();
	}
	
	/**
	 * Closes the score window, returns to the main menu.
	 */
	private void onExitClicked()
	{
		showMenuButtons(true);
		showHighScoreWindow(false,true);
		btnMenuPlay.setVisible(true);
		btnMenuOptions.setVisible(true);
		btnMenuScore.setVisible(true);
		winOptions.setVisible(false);
		//AudioManager.instance.onSettingsUpdated();
	}


	/**
	 * Save changes made to settings in the options menu.
	 */
	private void saveSettings()
	{
		GamePreferences prefs = GamePreferences.instance;
		prefs.sound = chkSound.isChecked();
		prefs.volSound = sldSound.getValue();
		prefs.music = chkMusic.isChecked();
		prefs.volMusic = sldMusic.getValue();
		prefs.showFpsCounter = chkShowFpsCounter.isChecked();
		prefs.save();
	}

	/**
	 * Renders the current frame of the screen.
	 * 
	 * @param deltaTime
	 *            Amount of time since last frame was rendered.
	 */
	@Override
	public void render(float deltaTime)
	{
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (debugEnabled)
		{
			debugRebuildStage -= deltaTime;
			if (debugRebuildStage <= 0)
			{
				debugRebuildStage = DEBUG_REBUILD_INTERVAL;
				rebuildStage();
			}
		}

		stage.act(deltaTime);
		stage.draw();
		stage.setDebugAll(false);
	}

	/**
	 * Changes the dimensions of the screen.
	 * 
	 * @param width
	 *            New width of the screen.
	 * @param height
	 *            New height of the screen.
	 */
	@Override
	public void resize(int width, int height)
	{
		stage.getViewport().update(width, height, true);
	}

	/**
	 * Redraws the stage when it needs to be shown again.
	 */
	@Override
	public void show()
	{
		stage = new Stage(new StretchViewport(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT));
		Gdx.input.setInputProcessor(stage);
		rebuildStage();
	}

	/**
	 * Frees allocated resources when screen is hidden.
	 */
	@Override
	public void hide()
	{
		stage.dispose();
		skinRutter.dispose();
		skinLibgdx.dispose();
	}

	/**
	 * Menu screen does not need to be paused.
	 */
	@Override
	public void pause()
	{
	}
	
	/**
	 * Animates the buttons on the menu
	 */
	private void showMenuButtons(boolean visible) 
	{
		float moveDuration = 1.0f;
		Interpolation moveEasing = Interpolation.swing;
		float delayOptionsButton = 0.25f;
		float delayScoreButton = 0.5f;

		
		float moveX = 300 * (visible ? -1 : 1);
		float moveY = 0 * (visible ? -1 : 1);
		final Touchable touchEnabled = visible ? Touchable.enabled : Touchable.disabled;
		btnMenuPlay.addAction(moveBy(moveX,moveY,moveDuration,moveEasing));
		btnMenuOptions.addAction(sequence(delay(delayOptionsButton),moveBy(moveX, moveY, moveDuration, moveEasing)));
		btnMenuScore.addAction(sequence(delay(delayScoreButton),moveBy(moveX, moveY, moveDuration, moveEasing)));
		SequenceAction seq = sequence();
		if(visible)
		seq.addAction(delay(delayOptionsButton + moveDuration));
		seq.addAction(run(new Runnable() 
		{
			public void run() 
			{ 
				btnMenuPlay.setTouchable(touchEnabled);
				btnMenuOptions.setTouchable(touchEnabled);
				btnMenuScore.setTouchable(touchEnabled);
			}
		}));
		stage.addAction(seq);
	}
	
	/**
	 * Makes the options window fade in.
	 */
	private void showOptionsWindow (boolean visible,boolean animated) 
	{
		float alphaTo = visible ? 0.8f : 0.0f;
		float duration = animated ? 1.0f : 0.0f;
		Touchable touchEnabled = visible ? Touchable.enabled : Touchable.disabled;
		winOptions.addAction(sequence(touchable(touchEnabled),alpha(alphaTo,duration)));
	}
	
	/**
	 * Makes the high score menu fade in.
	 */
	private void showHighScoreWindow (boolean visible,boolean animated) 
	{
		float alphaTo = visible ? 0.8f : 0.0f;
		float duration = animated ? 1.0f : 0.0f;
		Touchable touchEnabled = visible ? Touchable.enabled : Touchable.disabled;
		winScore.addAction(sequence(touchable(touchEnabled),alpha(alphaTo,duration)));
	}
	
	/**
	 * Makes the score entry menu fade in.
	 */
	private void showScoreEntryWindow (boolean visible,boolean animated) 
	{
		float alphaTo = visible ? 0.8f : 0.0f;
		float duration = animated ? 1.0f : 0.0f;
		Touchable touchEnabled = visible ? Touchable.enabled : Touchable.disabled;
		winScoreEntry.addAction(sequence(touchable(touchEnabled),alpha(alphaTo,duration)));
	}
}