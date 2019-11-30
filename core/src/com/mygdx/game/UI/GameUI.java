package com.mygdx.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.StringBuilder;
import com.mygdx.game.MyTowerDefenseGame;
import com.mygdx.game.input.GameKeys;
import com.mygdx.game.screen.ScreenType;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;

import static com.mygdx.game.UI.GameRenderer.TAG;

public class GameUI extends Table  {

    boolean clickedOnButtonOne;
    boolean clickedOnButtonTwo;
    boolean secondCick;
    public  TextButton txtButton;
    public  TextButton txtButton2;
    public TextButton backButton;
    public InputListener bubbleListener;



    public GameUI(final MyTowerDefenseGame context) {

        super(context.getSkin());
        setFillParent(true);

        final I18NBundle i18NBundle =context.getI18NBundle();
/*        //setHeight();
   //     this.setWidth(context.getScreenViewport().getScreenHeight());
    //   this.setHeight(context.getScreenViewport().getScreenWidth());
     //  this.setTouchable(Touchable.enabled);
        this.clickedOnButtonOne = false;
        this.clickedOnButtonTwo = false;
        this.secondCick = false;
        this.setTouchable(Touchable.enabled);


        txtButton = new TextButton("P111",getSkin(),"huge");
        txtButton2 = new TextButton("P2",getSkin(),"huge");
        backButton = new TextButton(i18NBundle.format("backButton"),getSkin(), "normal");
        txtButton.setName("egyesgom");









        bubbleListener= new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button)        {
                //txtButton   = (TextButton) event.getRelatedActor().getName();
                Gdx.app.debug(TAG,"screeen name "+event.getRelatedActor().getName());


                if (txtButton == null){
                    Gdx.app.debug(TAG,"screeen tou ");

                }else {

                    Gdx.app.debug(TAG,"screeen noz ");
                }
                // if (txtButton.isPressed()) {Gdx.app.debug(TAG,"Preessed txt ");}
                //lse{
                //    Gdx.app.debug(TAG,"screeen ");

                //}


                return true; //or false
            }
        };


/*




        /*
        this.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    System.out.println("I got clicked!");

                }







        });
txtButton.addListener(bubbleListener);

*/





        /*
        txtButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                //gombnyomás szelkció
              // context.getInputManager().notifyKeyDown(GameKeys.SELECT);
               // Gdx.app.debug(TAG,"Preessed");



                return true;
            }
        });

*/




/*
        this.addListener(new InputListener() {
                      @Override
                      public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                          //gombnyomás szelkció
                          super.touchDown(event, x, y, pointer, button);

               //           context.getInputManager().notifyKeyDown(GameKeys.SELECT);
                          // Gdx.app.debug(TAG,"Preessed");

                          Gdx.app.debug(TAG, "Preessed back");
            ///              context.setScreen(ScreenType.LOADINGMENU);
                          return true;
                      }
                  });

*/






       setDebug(true,true);

        //event.handle
/*
        this.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {

                //kattintva van
                if (txtButton.isPressed() | txtButton2.isPressed()) {}else{
                    System.out.println("I got clicked!and not a button: "+x+ "    "+ y);
                }

                System.out.println("I got clicked!: "+x+ "    "+ y);
                if (clickedOnButtonOne && !clickedOnButtonTwo) {

                    if (secondCick){
                    x = Gdx.input.getX();
                    y = Gdx.input.getY();
               //       Gdx.app.debug(TAG,"Creating new animation of type");

                //hova menjenű
                Vector3 vector = new Vector3(x, y, 0);
                Vector3 vectorUp = context.getGameCamera().unproject(vector);


                System.out.println("unproject koords!:X  " + vectorUp.x + " Y   " + vectorUp.y);


                 System.out.println("WIDTH HEIGHT!:X  " + context.getScreenViewport().getScreenWidth() + " Y   " + context.getScreenViewport().getScreenHeight());


                 context.getEcsEngine().playerCharSetDestination(vectorUp.x, vectorUp.y);
                 // Vector3 vector = new Vector3(Gdx.input.getX(),Gdx.input.getY(),0);
                //  Vector3 vector = new Vector3(Gdx.input.getX(),Gdx.input.getY(),0);

                    clickedOnButtonOne = false;
                    secondCick= false;
                    return;
                    }

                    secondCick = true;
                }


                System.out.println("I got clicked!: "+x+ "    "+ y);
                if (clickedOnButtonTwo && !clickedOnButtonOne) {

                    if (secondCick){
                        x = Gdx.input.getX();
                        y = Gdx.input.getY();
                       //hova menjenű
                        Vector3 vector = new Vector3(x, y, 0);
                        Vector3 vectorUp = context.getGameCamera().unproject(vector);
                        System.out.println("unproject koords!:X  " + vectorUp.x + " Y   " + vectorUp.y);
                        System.out.println("WIDTH HEIGHT!:X  " + context.getScreenViewport().getScreenWidth() + " Y   " + context.getScreenViewport().getScreenHeight());
                        context.getEcsEngine().playerCharSetDestination(vectorUp.x, vectorUp.y);

                        clickedOnButtonTwo = false;
                        secondCick= false;
                        return;
                    }

                    secondCick = true;
                }



             }
          });

*/

  //Gdx.input.setInputProcessor(bubbleListener);
    }



    public boolean isButtonSelected(){

        return true;
    }

    }
