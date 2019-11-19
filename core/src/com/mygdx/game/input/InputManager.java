package com.mygdx.game.input;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Array;

public class InputManager implements InputProcessor {
    private  final GameKeys[] keyMapping;
    private  final boolean[] keyState;
    private final Array<GameKeyInputListener> listeners;


    public InputManager(){

        this.keyMapping = new GameKeys[256];// annyi lehet a max, mert ennyi a legmagasabb bill érték 255
        //define map
        for (final GameKeys gameKey: GameKeys.values()){
            for (final int code:gameKey.keyCode){

                keyMapping[code]=gameKey;
            }


        }
        keyState = new boolean[GameKeys.values().length];
        listeners = new Array<GameKeyInputListener>();

    }

    public void addInputListener(final GameKeyInputListener listener){
        listeners.add(listener);



    }

    //ált nincs használva
    public void removeInputListener(final GameKeyInputListener listener){
        //identitás chekkolás
        //value == listener ( identit= true esetén ezt használja
        //value.equeals identity false
        listeners.removeValue(listener,true);

    }


    @Override
    public boolean keyDown(int keycode) {
        final GameKeys gameKey = keyMapping[keycode];
        if (gameKey==null){
            //no mapping --> nothing to do
            return  false;//utánaolvas értesítési sorrendiség
        }

        notifyKeyDown(gameKey);


        return false;
    }



    public void notifyKeyDown(GameKeys gameKey) {

        keyState[gameKey.ordinal()]=true;//pressed
        for(final GameKeyInputListener listener :listeners){
            listener.keyPressed(this,gameKey);
        }
    }

    @Override
    public boolean keyUp(int keycode) {
        final GameKeys gameKey = keyMapping[keycode];
        if (gameKey==null){
            //no mapping --> nothing to do
            return  false;//utánaolvas értesítési sorrendiség
        }

        notifyKeyUp(gameKey);


        return false;
    }

    private void notifyKeyUp(GameKeys gameKey) {
        keyState[gameKey.ordinal()]=false;//pressed
        for(final GameKeyInputListener listener :listeners){
            listener.keyUp(this,gameKey);
        }

    }

    public boolean isKeyPressed(final GameKeys gameKey){
        return keyState[gameKey.ordinal()];

    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
