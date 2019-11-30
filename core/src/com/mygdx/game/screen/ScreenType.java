package com.mygdx.game.screen;

import com.badlogic.gdx.Screen;

public enum ScreenType {

    GAME(GameScreen.class),
    LOADINGGAME(LoadingGameScreen.class),
    MAINMENU(MainMenuScreen.class),
    LOADINGMENU(LoadingMenuScreen.class);


    private final Class<? extends AbstractScreen> screenClass;

    ScreenType(final  Class<? extends AbstractScreen> screenClass){

        this.screenClass = screenClass;
    }

    public Class<? extends Screen> getScreenClass() { //itt neme Abstratból származik ?
        return screenClass;
    }
}
