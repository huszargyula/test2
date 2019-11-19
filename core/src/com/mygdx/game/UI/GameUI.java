package com.mygdx.game.UI;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.mygdx.game.MyTowerDefenseGame;

public class GameUI extends Table {
    public GameUI(final MyTowerDefenseGame context) {

        super(context.getSkin());
        setFillParent(true);
      //  add(new TextButton("Valami",getSkin(),"huge"));

    }
}