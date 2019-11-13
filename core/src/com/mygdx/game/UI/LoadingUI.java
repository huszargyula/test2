package com.mygdx.game.UI;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class LoadingUI extends Table {
   private final ProgressBar progressBar;
    private final TextButton txtButton;
    private final TextButton pressAnyKeyButton;


    public LoadingUI(final Skin skin) {
        super(skin);
        setFillParent(true); //kitölti a teret

       progressBar=  new ProgressBar(0,1,0.01f ,false, skin ,"default");
        //lelassítása hogy szépen lassan menjen - amig nincs sok kép, teszt céljából lássuk
        progressBar.setAnimateDuration(1);

        txtButton =new TextButton("Loading..",skin,"huge");
        txtButton.getLabel().setWrap(true);

        pressAnyKeyButton =new TextButton("Press any key",skin, "normal");
        pressAnyKeyButton.getLabel().setWrap(true);
        pressAnyKeyButton.setVisible(false);

        add(pressAnyKeyButton).expand().fillX().bottom().row();

        add(txtButton).expand().fillX().bottom().row();
        add(progressBar).expand().fillX().padBottom(20);
        bottom(); // az egész asztalt leteszi az aljára
            //debug mód a gombokhoz:
        setDebug(true,true);

    }

    public void setProgress(final  float progress){

        progressBar.setValue(progress);

        if (progress >=1 && !pressAnyKeyButton.isVisible()){

            pressAnyKeyButton.setVisible(true);
            pressAnyKeyButton.setColor(1,1,1,0);
            //forever: folyamatosan csinálja
            pressAnyKeyButton.addAction(Actions.forever(Actions.sequence(Actions.alpha(1,1), Actions.alpha(0,1))));
        }

    }




}
