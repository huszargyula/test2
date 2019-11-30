package com.mygdx.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeType;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.UI.GameRenderer;
import com.mygdx.game.audio.AudioManager;
import com.mygdx.game.ecs.ECSEngine;
import com.mygdx.game.input.InputManager;
import com.mygdx.game.map.MapManager;
import com.mygdx.game.screen.AbstractScreen;
import com.mygdx.game.screen.ScreenType;

import java.util.EnumMap;

import box2dLight.Light;
import box2dLight.RayHandler;

public class MyTowerDefenseGame extends Game {
		private static final  String TAG =  MyTowerDefenseGame.class.getSimpleName();

		private EnumMap<ScreenType, AbstractScreen> screenCache;
		private FitViewport screenViewport;

		//16 kategoria az alakzatoknak általában ésekkel operál ami gyors
		public static final short BIT_PLAYER =1<<0; // az 1 est 0 pozicióval mozgatjuk balra azaz 0000 0000 0000 0001
		public static final short BIT_BOX =1<<1;	// az 1 est 0 pozicióval mozgatjuk balra azaz 0000 0000 0000 0010
		public static final short BIT_GROUND =1<<2; // az 1 est 0 pozicióval mozgatjuk balra azaz 0000 0000 0000 0100
		public static final short BIT_CIRCLE =1<<3;
		public static final short BIT_BOARD =1<<4;
		public static final short	BIT_GAME_OBJECT =1<<5;
		public static final short BIT_BUTTON=1<<6;
		/// ///////// a fizikai világ látrehozása
		private World world;

		private WorldContactListener worldContactListener;
		//Collision detect
		public static final BodyDef BODY_DEF = new BodyDef(); // ez a kettő a leeséshez, fizikai enginhez kell
		public static final FixtureDef FIXTURE_DEF = new FixtureDef(); // fixture jelentése kellék, alkatrész
		public int loadingMapId;


	//delta time fixing
		private static final float FIXED_TIME_STEP = 1/60f; // azaz 60 frame per sec
		private float accumulator;
		//ne legyen statikus
		private AssetManager assetManager;
		private OrthographicCamera gameCamera;
		public static final float UNIT_SCALE = 1/32f; //az egsség
		private SpriteBatch spriteBatch; //ez a texutárák rendeleléséhez kell

		public boolean mapShown;

	//audio
		private AudioManager audioManager;

		private Stage stage;
		private Skin skin;
		private I18NBundle i18NBundle;

		private InputManager inputManager;

		//Ecsengine

	private ECSEngine ecsEngine;

	//map manager

	private MapManager mapManager ;


	private GameRenderer gameRenderer;

	//világítás
	private RayHandler rayHandler;

	// mentés

	private PreferenceManager preferenceManager;
	public boolean gameStarter;
//REndering, and heap info:
	public TextButton renderingInfo;
	private StringBuilder stringBuilder;
	private  TmxMapLoader tmxMapLoader;


	public void create(){

			spriteBatch = new SpriteBatch();
			Gdx.app.setLogLevel(Application.LOG_DEBUG);
			mapShown = false;
			gameStarter = false;
			//		//fixing delta time
			accumulator = 0;

			//Fizikai engine incializálása
			Box2D.init();
			//gravitáció, csak y tengely mentén, ha nem mozog, akkor a grav erőst kikcsapolja
			//méter, másodperc, kg
			//
			world = new World(new Vector2(0, -9.81f),true);

			//hozzáadjuk a Collosion detectálás miatt ( is ?)
			worldContactListener = new WorldContactListener();
			world.setContactListener(worldContactListener);

			//világítás
			rayHandler = new RayHandler(world);
			//alap fény létrehzoás
			rayHandler.setAmbientLight(0,0,0,1f);
			// a többi fény a B2d componetnben!!


			//lehet szűrni amit nem akarsz megvilágitani testet
										//a Bit_playeünk fénye , (?? minend group "1" ?, csak a BITGroundot világitsa meg)
			Light.setGlobalContactFilter(BIT_PLAYER, (short)1,BIT_GROUND);
			//assetMannger inicializálása
			assetManager = new AssetManager();
			// beálít h tudjon tildemapat loadoljon
			tmxMapLoader= new  TmxMapLoader(assetManager.getFileHandleResolver());
			assetManager.setLoader(TiledMap.class,tmxMapLoader);

			//betűk, string texturák inicializása
			initalizeSkin();

			//itt a screensize!!
			// ez csak a bbetük gomboknál számít
			//Kamera felbontás
			stage = new Stage(new FitViewport(800,450),spriteBatch); //??
			//Gdx.input.setInputProcessor(stage);

			//audio
			audioManager = new AudioManager(this);

			//input
			inputManager = new InputManager();
			//a widgeteket és a keyobordot is tudja kkezelni
			Gdx.input.setInputProcessor(new InputMultiplexer(inputManager,stage));


			//setup game viewport
			gameCamera = new OrthographicCamera();
			//screentypeok létrehozása
			//9:16 a mobilok / képernyők általános képernyő aránya. ennek utánajárni!
			//ennek a fele a camere setposition ??
			screenViewport = new FitViewport(32,18, gameCamera);


			//FONTOS A SORREND!!!!!
			//create Ecsengine
			//setup Mapmanger
		mapManager = new MapManager(this);


		ecsEngine = new ECSEngine(this);

		mapManager.setEcsEngine(getEcsEngine());

			// create gameRenderer

			gameRenderer = new GameRenderer(this);
			gameRenderer.initGameEntities(this);
			//preferenc manager load-save
			preferenceManager = new PreferenceManager();

			//set first screen
			screenCache = new EnumMap<ScreenType, AbstractScreen>(ScreenType.class);
			setScreen(ScreenType.LOADINGMENU);


		renderingInfo = new TextButton("", getSkin());
		this.getStage().addActor(renderingInfo);

		//TODO HARD CODED
		renderingInfo.setPosition(400,350);

		// renderingInfo.setPosition(  400 ,this.getScreenViewport().getScreenHeight()- renderingInfo.getHeight());


	}

		public Stage getStage() {
			return stage;
		}

		public Skin getSkin() {
			return skin;
		}

		public void initalizeSkin(){

			//setup markup colors
			Colors.put("Red", Color.RED);
			Colors.put("Blue", Color.BLUE);


			//generate ttf bitmaps

			final ObjectMap<String,Object> resources = new ObjectMap<String, Object>();

			final FreeTypeFontGenerator fontGenerator =new FreeTypeFontGenerator(Gdx.files.internal("ui/font.ttf"));

			//size, bold, árnyék parméterek lehetsége megadása
			//skálázás

			final FreeTypeFontGenerator.FreeTypeFontParameter fontParameter =new FreeTypeFontGenerator.FreeTypeFontParameter();
			//skálázásnál hogy élsimitsin
			fontParameter.minFilter = Texture.TextureFilter.Linear;
			fontParameter.magFilter = Texture.TextureFilter.Linear;
			//skálázás, egy kicsi szöveg, egy normál, nagy, extra nagy
			final int[] sizesToCreate = {16,20,26,32};

			for (int size :sizesToCreate){
				fontParameter.size = size;
				//kül fontokhoz elmentjük bitmapba
				final BitmapFont bitmapFont = fontGenerator.generateFont((fontParameter));
				//"Szinesités" engedélyezése a markup
				bitmapFont.getData().markupEnabled= true;
				resources.put("font_"+size,bitmapFont);


			}
			fontGenerator.dispose();

			//load skin
			// az assetmanager furán kezeli- lhetet bugosan, szval ezért rajta kivül dolgozunk
			//Skin parameter pl utvonal


			final SkinLoader.SkinParameter skinParameter = new SkinLoader.SkinParameter("ui/hud.atlas",resources);

			assetManager.load("ui/hud.json", Skin.class, skinParameter);
			//kül nyelvek betűi: betöltése
			assetManager.load("ui/strings", I18NBundle.class);
			//rögötön be is töltjük h a felhasználónak a loading képernyő bejöjjön
			assetManager.finishLoading();
			skin = assetManager.get("ui/hud.json", Skin.class);

			i18NBundle = assetManager.get("ui/strings",I18NBundle.class);




		}


	public void heapAndFPSinfo(float FPS,float drawCalls, float textBind, float javaheap, float nativeheap) {


		//txtButtob ddinamikus updetealése
		stringBuilder = renderingInfo.getLabel().getText();
		stringBuilder.setLength(0);
		//...
		stringBuilder.append("FPS: ");
		stringBuilder.append(" ");
		stringBuilder.append(FPS);

		stringBuilder.append("DrC: ");
		stringBuilder.append(" ");
		stringBuilder.append(drawCalls);

		stringBuilder.append("TxB: ");
		stringBuilder.append(" ");
		stringBuilder.append(textBind);

		stringBuilder.append("Jh: ");
		stringBuilder.append(javaheap);
		stringBuilder.append(" Kby ");

		stringBuilder.append("Nh: ");
		stringBuilder.append(" ");
		stringBuilder.append(nativeheap);
		stringBuilder.append(" Kby ");


		//   stringBuilder.append("% }");
		//rendszerértesítés h a szövegben változás van, ujra kell renderelni
		renderingInfo.getLabel().invalidateHierarchy();




	}

	public TextButton getRenderingInfo() {
		return renderingInfo;
	}




	//megfelelő képernyő beállítás
		public  void setScreen(final ScreenType screenType){


			//megnézzük elérhető e
			final Screen screen = screenCache.get(screenType);


			if (screen == null) {
				//Screen not created => create it!
				try {
					Gdx.app.debug(TAG, "Creating mew screen" + screenType);
					final AbstractScreen newScreen =(AbstractScreen) ClassReflection.getConstructor(screenType.getScreenClass(),MyTowerDefenseGame.class).newInstance(this);
					screenCache.put(screenType,newScreen);
					setScreen(newScreen);
				} catch (ReflectionException e) {
					throw new GdxRuntimeException("Screen " + screenType + "Could not be created", e);

				} catch (Exception e){

					Gdx.app.debug(TAG, "EDDIG  JUT EL2");

				}
			}else {
				Gdx.app.debug(TAG, "Switching to Screen:" +screenType);
				setScreen(screen);

			}

		}


		@Override
		public void render(){

			super.render();

			final float deltaTime = Math.min(0.25f, Gdx.graphics.getRawDeltaTime());

		//if (gameStarter) {
			ecsEngine.update(deltaTime);
			//probaképp ill érdekességkép kirjuk az a két frame közötti idő
			//	Gdx.app.debug(TAG, "idő eltelte két frame között"+Gdx.graphics.getRawDeltaTime());

			//max idő két frame közöttú
			//ez főleg a fizikai világ fizikai hatásaira igaz hogy ne hagyjon ki
			//ne mozogjon bele az egyik fizikai tárgy a másikba a frame kiesésé miatt


			//ehelyett lett a  delta time
			// world.step(Gdx.graphics.getRawDeltaTime(), 6,2);


			accumulator += deltaTime;

			//fizikia világ rendelrelése: a két iteráor most kb optimális, de lehet változtatni hogy pontosabb
			// legyen, ennek utána lehet olvasni ha kell
			//BOX2D debugrenderer a game rendererben !!!
			while (accumulator >= FIXED_TIME_STEP) {
				// TODO save the previos position of the body
				world.step(FIXED_TIME_STEP, 6, 2);
				accumulator -= FIXED_TIME_STEP;

			}
			//TODO calculate rendesposition from previous position and real body pos


			//mennyi idő van a köv frameing
			//final float alpha =accumulator/FIXED_TIME_STEP;
			//később ki lehet simitani a rendereinget ez a későbbiekre
			//interpolációs rendering
	//	}

			gameRenderer.render(accumulator/FIXED_TIME_STEP);
			//valami történik az uI-val villog, kihajvánol => az act updateli
			//ezek a staghez hozzárendelet gombok TABlek rajzolása, efektjei.


			stage.getViewport().apply(); //draw elött v rener elött mindig meg kell hívni
			// Video 16 12:20
			stage.act(deltaTime);
			stage.draw();


		}

	public static void resetBodieAndFixtureDefinition(){

		BODY_DEF.position.set(0,0);
		BODY_DEF.gravityScale =0;
		BODY_DEF.type = BodyDef.BodyType.StaticBody;
		BODY_DEF.fixedRotation = false;

		FIXTURE_DEF.density =0;
		FIXTURE_DEF.isSensor =false;
		FIXTURE_DEF.restitution= 0;
		FIXTURE_DEF.friction=0.2f;
		FIXTURE_DEF.filter.categoryBits =0x0001;
		FIXTURE_DEF.filter.maskBits=-1;
		FIXTURE_DEF.shape=null;


	}




	public GameRenderer getGameRenderer() {
		return gameRenderer;
	}

	public I18NBundle getI18NBundle() {
		return i18NBundle;
	}

	public AudioManager getAudioManager() {
		return audioManager;
	}

	public WorldContactListener getWorldContactListener() {
		return worldContactListener;
	}

	public RayHandler getRayHandler() {
		return rayHandler;
	}

	public PreferenceManager getPreferenceManager() {
		return preferenceManager;
	}

	public MapManager getMapManager(){return mapManager ;}

	public ECSEngine getEcsEngine() {
		return ecsEngine;
	}


	public InputManager getInputManager() {
		return inputManager;
	}

	public FitViewport getScreenViewport(){return screenViewport;}


	public AssetManager getAssetManager(){ return assetManager; }

	public OrthographicCamera getGameCamera(){ return  gameCamera; }

	public SpriteBatch getSpriteBatch() {
		return spriteBatch;
	}

	public  World getWorld(){ return world;}

	public int getLoadingMapId() {
		return loadingMapId;
	}

	public void setLoadingMapId(int loadingMapId) {
		this.loadingMapId = loadingMapId;
	}

	@Override
	public void dispose(){
		super.dispose();
		gameRenderer.dispose();
		rayHandler.dispose();
		world.dispose();
		assetManager.dispose(); // bonyim játéknál menet közben
		//dispos, ! utnaolvas
		spriteBatch.dispose();
		stage.dispose();

	}


	public TmxMapLoader getAssetTiledLoader() {
		return this.tmxMapLoader;
	}
}