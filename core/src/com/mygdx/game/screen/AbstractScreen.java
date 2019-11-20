package com.mygdx.game.screen;


import com.badlogic.gdx.Screen;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.MyTowerDefenseGame;
import com.mygdx.game.audio.AudioManager;
import com.mygdx.game.input.GameKeyInputListener;
import com.mygdx.game.input.InputManager;

import box2dLight.RayHandler;

public  abstract class AbstractScreen <T extends Table> implements Screen , GameKeyInputListener {
    protected final MyTowerDefenseGame context;
    protected final FitViewport viewport;
    protected final World world;

    protected final Stage stage;
    protected final T screenUI;
    protected  final InputManager inputManager;
    protected final AudioManager audioManager;

    protected final RayHandler rayHandler;

public AbstractScreen(final MyTowerDefenseGame context){


        this.context= context;

        this.rayHandler = context.getRayHandler();
        viewport = context.getScreenViewport();
        this.world = context.getWorld();

        inputManager = context.getInputManager();
        stage = context.getStage();
        screenUI = getScreenUI(context);

        //audio
       audioManager= context.getAudioManager();

        }

//table
protected abstract T getScreenUI( final MyTowerDefenseGame context);


@Override
public void resize(final int width, final int height){
        viewport.update(width,height);
        stage.getViewport().update(width,height,true);

      //a terület ahol a rayhandler should render
    //kell a resiznál!
        rayHandler.useCustomViewport(viewport.getScreenX(), viewport.getScreenY(), viewport.getScreenWidth(), viewport.getScreenHeight());

        }


    @Override
    public void show(){
        inputManager.addInputListener(this);
        stage.addActor(screenUI);

        }

        //pl a játék alatt nem kell a loading UI
    @Override
    public void hide(){

        inputManager.removeInputListener(this);
        stage.getRoot().removeActor(screenUI); //root : group of diff actor . actor = a widgetek


    }


}