package com.mygdx.game.UI;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.StringBuilder;
import com.mygdx.game.MyTowerDefenseGame;
import com.mygdx.game.input.GameKeys;

public class LoadingUI extends Table {

    private final String loadingString;
   private final ProgressBar progressBar;
    private final TextButton txtButton;
    private final TextButton pressAnyKeyButton;


    public LoadingUI(final MyTowerDefenseGame context) {
        super(context.getSkin());
        setFillParent(true); //kitölti a teret

        // kul nyelvekhez
        final I18NBundle i18NBundle =context.getI18NBundle();


       progressBar=  new ProgressBar(0,1,0.01f ,false, getSkin() ,"default");
        //lelassítása hogy szépen lassan menjen - amig nincs sok kép, teszt céljából lássuk
        progressBar.setAnimateDuration(1);

        loadingString = i18NBundle.format("loading");
        txtButton =new TextButton(loadingString, getSkin(),"huge");

        //txtButton =new TextButton("[Red]Lo[Blue]ading..",getSkin(),"huge");
        //az uj nyelvi beállításokkal:
        //txtButton =new TextButton(i18NBundle.format("loading"), getSkin(),"huge");


        txtButton.getLabel().setWrap(true);

        pressAnyKeyButton =new TextButton(i18NBundle.format("pressAnyKey"),getSkin(), "small");
        pressAnyKeyButton.getLabel().setWrap(true);
        pressAnyKeyButton.setVisible(false);
        //példa overridra
        pressAnyKeyButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
              //gombnyomás szelkció
                context.getInputManager().notifyKeyDown(GameKeys.SELECT);


                return true;
            }
        });



        add(pressAnyKeyButton).expand().fillX().bottom().row();

        add(txtButton).expand().fillX().bottom().row();
        add(progressBar).expand().fillX().padBottom(20);
        bottom(); // az egész asztalt leteszi az aljára
            //debug mód a gombokhoz:
        setDebug(true,true);

    }

    public void setProgress(final  float progress){

        progressBar.setValue(progress);

        //txtButtob dinamikus updetealése
        final StringBuilder stringBuilder = txtButton.getLabel().getText();
        stringBuilder.setLength(0);
        //...
        stringBuilder.append(loadingString);
        stringBuilder.append(" {");
        stringBuilder.append(progress*100);


        stringBuilder.append("% }");
        //rendszerértesítés h a szövegben változás van, ujra kell renderelni
        txtButton.getLabel().invalidateHierarchy();

        if (progress >=1 && !pressAnyKeyButton.isVisible()){

            pressAnyKeyButton.setVisible(true);
            pressAnyKeyButton.setColor(1,1,1,0);
            //forever: folyamatosan csinálja
            pressAnyKeyButton.addAction(Actions.forever(Actions.sequence(Actions.alpha(1,1), Actions.alpha(0,1))));
        }

    }




}
