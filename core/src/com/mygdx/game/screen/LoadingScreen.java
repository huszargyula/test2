package com.mygdx.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.game.MyTowerDefenseGame;
import com.mygdx.game.UI.LoadingUI;

import static com.mygdx.game.map.MapCol.TAG;

public class LoadingScreen extends AbstractScreen
{   //private final gameTD context;

    private final AssetManager assetManager;

    public LoadingScreen(final MyTowerDefenseGame context){
        super(context);
        //átatódik az assetMAnager és betölti a mapot
        this. assetManager= context.getAssetManager();
        assetManager.load("map/map.tmx", TiledMap.class);
        //assetManager.finishloading !


    }

    @Override
    protected Table getScreenUI(final Skin skin) {
        return new LoadingUI(stage, skin);
    }



    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0,1,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //assetManager.updet= betülti, igaz ha befejeződött a betöltés
        //assetManager.getprogres, majd a kis loadingbarhoz
        if (assetManager.update()){

    //        context.setScreen(ScreenType.GAME);
        }


        //viewport.apply(true);
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
}