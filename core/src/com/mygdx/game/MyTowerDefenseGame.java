package com.mygdx.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.screen.AbstractScreen;
import com.mygdx.game.screen.ScreenType;

import java.util.EnumMap;

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
		/// ///////// a fizikai világ látrehozása
		private World world;
		private Box2DDebugRenderer box2DDebugRenderer;
		private WorldContactListener worldContactListener;
		//Collision detect



		//delta time fixing
		private static final float FIXED_TIME_STEP = 1/60f; // azaz 60 frame per sec
		private float accumulator;
		//ne legyen statikus
		private AssetManager assetManager;
		private OrthographicCamera gameCamera;
		public static final float UNIT_SCALE = 1/32f; //az egsség
		private SpriteBatch spriteBatch; //ez a texutárák rendeleléséhez kell


		private Stage stage;
		private Skin skin;

		public void create(){

			spriteBatch = new SpriteBatch();
			Gdx.app.setLogLevel(Application.LOG_DEBUG);
			//		//fixing delta time
			accumulator = 0;

			//Fizikai engineincializálása
			Box2D.init();
			//gravitáció, csak y tengely mentén, ha nem mozog, akkor a grav erőst kikcsapolja
			//méter, másodperc, kg
			//
			world = new World(new Vector2(0, -9.81f),true);

			//hozzáadjuk a Collosion detectálás miatt ( is ?)
			worldContactListener = new WorldContactListener();
			world.setContactListener(worldContactListener);

			box2DDebugRenderer =new Box2DDebugRenderer();

			//assetMannger inicializálása
			assetManager = new AssetManager();
			// beálít h tudjon tildemapat loadoljon
			assetManager.setLoader(TiledMap.class, new TmxMapLoader(assetManager.getFileHandleResolver()));
			initalizeSkin();
			//itt a screensize!!

			stage = new Stage(new FitViewport(450,800),spriteBatch); //??

			gameCamera = new OrthographicCamera();
			//screentypeok létrehozása
			//9:16 a mobilok / képernyők általános képernyő aránya. ennek utánajárni!
			screenViewport = new FitViewport(18,16, gameCamera);
			screenCache = new EnumMap<ScreenType, AbstractScreen>(ScreenType.class);
			setScreen(ScreenType.LOADING);


		}

		public Stage getStage() {
			return stage;
		}

		public Skin getSkin() {
			return skin;
		}

		private void initalizeSkin(){
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
				resources.put("font_"+size,fontGenerator.generateFont(fontParameter));


			}
			fontGenerator.dispose();

			//load skin
			// az assetmanager furán kezeli- lhetet bugosan, szval ezért rajta kivül dolgozunk
			//Skin parameter pl utvonal

			final SkinLoader.SkinParameter skinParameter = new SkinLoader.SkinParameter("ui/tower_ui.atlas",resources);

			assetManager.load("ui/hud.json", Skin.class, skinParameter);
			//rögötön be is töltjük h a felhasználónak a loading képernyő bejöjjön
			assetManager.finishLoading();
			skin = assetManager.get("ui/hud.json", Skin.class);



		}

		public FitViewport getScreenViewport(){return screenViewport;}

		public Box2DDebugRenderer getBox2DDebugRenderer(){return box2DDebugRenderer;}

		public AssetManager getAssetManager(){

			return assetManager;
		}

		public OrthographicCamera getGameCamera(){
			return  gameCamera;

		}

		public SpriteBatch getSpriteBatch() {
			return spriteBatch;
		}

		public  World getWorld(){

			return world;
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
					throw new GdxRuntimeException("Screen" + screenType + "Could not be created", e);

				} catch (Exception e){

					Gdx.app.debug(TAG, "EDDIG  JUT EL2");

				}
			}else {
				Gdx.app.debug(TAG, "Switching to Screen:" +screenType);
				setScreen(screen);

			}

		}

		@Override
		public void dispose(){
			super.dispose();
			box2DDebugRenderer.dispose();
			world.dispose();
			assetManager.dispose(); // bonyim játéknál menet közben
			//dispos, ! utnaolvas
			spriteBatch.dispose();
			stage.dispose();
		}


		@Override
		public void render(){

			super.render();

			//probaképp ill érdekességkép kirjuk az a két frame közötti idő
			//	Gdx.app.debug(TAG, "idő eltelte két frame között"+Gdx.graphics.getRawDeltaTime());

			//max idő két frame közöttú
			//ez főleg a fizikai világ fizikai hatásaira igaz hogy ne hagyjon ki
			//ne mozogjon bele az egyik fizikai tárgy a másikba a frame kiesésé miatt


			//ehelyett lett a  delta time
			// world.step(Gdx.graphics.getRawDeltaTime(), 6,2);



			accumulator += Math.min(0.25f,Gdx.graphics.getRawDeltaTime());

			while(accumulator>= FIXED_TIME_STEP){

				world.step(FIXED_TIME_STEP, 6,2);
				accumulator-= FIXED_TIME_STEP;

			}



			//mennyi idő van a köv frameing
			//final float alpha =accumulator/FIXED_TIME_STEP;
			//később ki lehet simitani a rendereinget ez a későbbiekre
			//interpolációs rendering

			//valami történik az uI-val villog, kihajvánol => az act updateli
			stage.getViewport().apply(); //draw elött v rener elött mindig meg kell hívni
// Video 16 12:20
			stage.act();
			stage.draw();


		}

	}