package com.mygdx.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.mygdx.game.MyTowerDefenseGame;
import com.mygdx.game.UI.LoadingUI;
import com.mygdx.game.audio.AudioType;
import com.mygdx.game.input.GameKeys;
import com.mygdx.game.input.InputManager;

public class LoadingScreen extends AbstractScreen<LoadingUI>
{   //private final gameTD context;

    private final AssetManager assetManager;
    private boolean isMusicLoaded;


    public LoadingScreen(final MyTowerDefenseGame context){
        super(context);
        //átatódik az assetMAnager és betölti a mapot
        this. assetManager= context.getAssetManager();
        assetManager.load("map/map.tmx", TiledMap.class);
        //assetManager.finishloading !

      //load audio
        isMusicLoaded = false;
        for (final AudioType audioType:AudioType.values()){
          assetManager.load(audioType.getFilePath(),audioType.isMusic() ? Music.class: Sound.class);
        }
    }

    @Override
    protected LoadingUI getScreenUI(final MyTowerDefenseGame context) {
        return new LoadingUI(context);
    }



    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //assetManager.updet= betülti, igaz ha befejeződött a betöltés
        //assetManager.getprogres, majd a kis loadingbarhoz
        // Gdx.input.getX() érdekes beleolvasni!!
        assetManager.update();
        //progress bar mozgasa
        screenUI.setProgress(assetManager.getProgress());
        if (!isMusicLoaded && assetManager.isLoaded(AudioType.INTRO.getFilePath())){
           isMusicLoaded = true;
           audioManager.playAudio(AudioType.INTRO);

        }



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

    }

    @Override
    public void hide(){
        super.hide();
        audioManager.stopCurrentMusic();


    }

    @Override
    public void keyPressed(InputManager manager, GameKeys key) {

        audioManager.playAudio(AudioType.SELECT);
        if(assetManager.getProgress()>=1) {
           context.setScreen(ScreenType.GAME);
        }
    }

    @Override
    public void keyUp(InputManager manager, GameKeys key) {

    }
}
