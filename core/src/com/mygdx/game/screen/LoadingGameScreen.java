package com.mygdx.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.MyTowerDefenseGame;
import com.mygdx.game.UI.LoadingGameUI;
import com.mygdx.game.audio.AudioType;
import com.mygdx.game.input.GameKeys;
import com.mygdx.game.input.InputManager;
import com.mygdx.game.map.MapType;

public class LoadingGameScreen extends AbstractScreen<LoadingGameUI>
{   //private final gameTD context;

    public static final String TAG = LoadingGameScreen.class.getSimpleName();


    private final AssetManager assetManager;
    private boolean isMusicLoaded;
    private int loadingMapId;
    private final GLProfiler profiler;

    public LoadingGameScreen(final MyTowerDefenseGame context){
        super(context);
        //átatódik az assetMAnager és betölti a mapot
        this. assetManager= context.getAssetManager();
        loadingMapId = 0;
        profiler = new GLProfiler(Gdx.graphics);
        profiler.enable();

    }

    @Override
    protected LoadingGameUI getScreenUI(final MyTowerDefenseGame context) {
        return new LoadingGameUI(context);
    }


    @Override
    public void render(float delta) {
//itt már nem rendelődik demmi, hanem a "gameRenderer" ben történik
        //és ott márkitörli
   //    Gdx.gl.glClearColor(0,0,0,1);
    //   Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //assetManager.updet= betülti, igaz ha befejeződött a betöltés
        //assetManager.getprogres, majd a kis loadingbarhoz
        // Gdx.input.getX() érdekes beleolvasni!!
     //   assetManager.update();
        //progress bar mozgasa
        screenUI.setProgress(assetManager.getProgress());
    /*    if (!isMusicLoaded && assetManager.isLoaded(AudioType.INTRO.getFilePath())){
           isMusicLoaded = true;
           audioManager.playAudio(AudioType.INTRO);

        }

*/

    }

    @Override
    public void resize(int width, int height) {

        super.resize(width,height);

    }
    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }


    @Override
    public void dispose() {

    }


    @Override
    public void show(){

        super.show();
        Gdx.app.debug("LOADINGGAMESCRRE","MOST JÖTT LÉTRE");

        //effekt betoltése
//TODO ezek kitoltése betöltött utak elmentése
        assetManager.load("charracters_effects/effect.atlas", TextureAtlas.class );
        assetManager.load("anim/bird_anim2.atlas", TextureAtlas.class );
        assetManager.load("anim/button.atlas",TextureAtlas.class);
        assetManager.load("anim/hero_all_animation.atlas", TextureAtlas.class );
        assetManager.load("anim/proba.atlas", TextureAtlas.class );
        assetManager.load("anim/mano_mage.atlas", TextureAtlas.class );





        //1 darab map betöltése
        // assetManager.load("map/map.tmx", TiledMap.class);
        //assetManager.finishloading !

        //összes map betötése

       // for (final MapType mapType :MapType.values()){

         //   assetManager.load(mapType.getFilePath(),TiledMap.class);

        //}
        loadingMapId = context.getLoadingMapId();
     //   loadingMapId=2;
        if (loadingMapId==0){

            Gdx.app.debug(TAG,"There is no map choosen");
        }else {
            // lehet nem kell ujra megadni, de biztos ami tuti
            assetManager.setLoader(TiledMap.class,context.getAssetTiledLoader());
            assetManager.load("map/map_ID_"+loadingMapId+".tmx", TiledMap.class);
            Gdx.app.debug(TAG,"Loading : " +" map/map_ID_"+loadingMapId+".tmx");

        }


        //load audio
/*        isMusicLoaded = false;
        for (final AudioType audioType:AudioType.values()){
            assetManager.load(audioType.getFilePath(),audioType.isMusic() ? Music.class: Sound.class);
        }
*/

        assetManager.finishLoading();

        profiler.reset();
        // TODO ezt is le lehet váltani a string konkatenációt!
        context.getMapManager().setMap(MapType.valueOf("MAP_"+loadingMapId));
        profiler.reset();
        assetManager.finishLoading();


        //width az one world unit
        //0áznmi
        //TODO fonots, amit elöbb rajzol, az lesz  hátul a rétegekben

        context.getEcsEngine().createPlayer(context.getMapManager().getCurrentMap().getStartLocation(),2,2,1.5f,1.5f,2);

        // context.getEcsEngine().createPlayer(context.getMapManager().getCurrentMap().getStartLocation(),2,2,128,128,1);
     //   context.getEcsEngine().createPlayer(context.getMapManager().getCurrentMap().getStartLocation().add(5,5),2,2,2,2,1);
        Gdx.app.debug("PStartLoc",""+context.getMapManager().getCurrentMap().getStartLocation());
       // context.getEcsEngine().createPlayer(context.getMapManager().getCurrentMap().getStartLocation().add(-5,-5),1,1,2,2,3);


        //  context.getEcsEngine().createPlayer(context.getMapManager().getCurrentMap().getStartLocation().add(6,1),2,2,128,128,3);
      // context.getEcsEngine().createPlayer(context.getMapManager().getCurrentMap().getStartLocation().add(8,1),2,2,128,128,4);

        //TODO hard Coded


        context.gameStarter = true;



    }

    @Override
    public void hide(){
        super.hide();
        //audioManager.stopCurrentMusic();


    }

    @Override
    public void keyPressed(InputManager manager, GameKeys key) {


        if(assetManager.getProgress()>=1) {
          //  audioManager.playAudio(AudioType.SELECT);
          if (assetManager.update()){ // ha mindent betöltött az asset manager
              context.setScreen(ScreenType.GAME);
          }

        }
    }

    @Override
    public void keyUp(InputManager manager, GameKeys key) {

    }
}
