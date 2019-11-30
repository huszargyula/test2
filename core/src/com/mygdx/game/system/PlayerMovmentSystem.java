package com.mygdx.game.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.MyTowerDefenseGame;
import com.mygdx.game.ecs.ECSEngine;
import com.mygdx.game.ecs.component.B2DComponent;
import com.mygdx.game.ecs.component.PlayerComponent;
import com.mygdx.game.input.GameKeyInputListener;
import com.mygdx.game.input.GameKeys;
import com.mygdx.game.input.InputManager;



public class PlayerMovmentSystem extends IteratingSystem implements GameKeyInputListener {

    private boolean directionChange;
    private int xFactor;
    private int yFactor;
    private int antiWiggleFactorX;
    private int antiWiggleFactorY;
    public OrthographicCamera camer;

    public boolean isSelected;



    public PlayerMovmentSystem(final MyTowerDefenseGame context) {
        // kül famayli metodus hívások
        //family.all, exlude, one

        //all amibe az összes van
        super(Family.all(PlayerComponent.class, B2DComponent.class).get());
        //igy minden entitásra megvan hívva, ami player comptonent és B2d component
        //itt most ez midneig 1 entity, a player
        camer = context.getGameCamera();
        context.getInputManager().addInputListener(this);
        isSelected = false;
      //  directionChange =false; //ezt és ennek meghívását ki lehet kommentelni
        xFactor=yFactor =0;
        antiWiggleFactorX = antiWiggleFactorY =1;
    }

    //ez elvileg minden entitásra meg lesz hívva ami a "familye" része
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        //el kell érnünk az összes komponensünk a meghíváshoz: Component mapper az enginben
        //entity.getComponent(PlayerComponent.class); ez lenne alapból, de ez lassú megoldás!!


        //PLAYER Mozgatása
       // if (directionChange) {
            final  PlayerComponent playerComponent=  ECSEngine.playerCmpMapper.get(entity); // ez ugyanaz mint a felette levő, csak component mapperrel, gyorsabb
            final  B2DComponent b2dComponent = ECSEngine.b2dCmpMapper.get(entity);


//////////////////////////////////////////////////////////////
  /*
        if (Gdx.input.justTouched() &isSelected == true ){

             // b2dComp

            isSelected = false;

            int pointX = Gdx.input.getX();
            int pointY = Gdx.input.getY();
            Gdx.app.debug("PLAYERMOVMENTSYSTEM", "Screen Touched after selected " +Gdx.input.justTouched());
            // b2dComp

            Vector3 vector = new Vector3(pointX, pointY, 0);
            Vector3 vectorUp = camer.unproject(vector);
           // b2dComp

            b2dComponent.setDirection.set(vectorUp);
           // b2dComp

        }

        if (Gdx.input.justTouched()) {
                int pointX = Gdx.input.getX();
                int pointY = Gdx.input.getY();

                Vector3 vector = new Vector3(pointX, pointY, 0);
                Vector3 vectorUp = camer.unproject(vector);

                boolean wasTouched = b2dComponent.body.getFixtureList().first().testPoint(vectorUp.x, vectorUp.y);

                if (wasTouched) {
                    Gdx.app.debug("PLAYERMOVMENTSYSTEM", "Body was touched ");
                    // b2dComp
                    wasTouched = false;
                    //Handle touch
                   isSelected = true;

                }
              }
*/
//////////////////////////////////////////////




            /*
            directionChange =false; //ne hívodjon minig meg
            b2dComponent.body.applyLinearImpulse(
                    (xFactor * playerComponent.speed.x - b2dComponent.body.getLinearVelocity().x )* b2dComponent.body.getMass(),
                    (yFactor * playerComponent.speed.y - b2dComponent.body.getLinearVelocity().y )* b2dComponent.body.getMass(),
                    b2dComponent.body.getWorldCenter().x, b2dComponent.body.getWorldCenter().y,
                    true
            );


 */

// telefon origo az 0,0
//origo world

directionChange =false; //ne hívodjon minig meg





       // Gdx.app.debug("RenderInfo X", (float) b2dComponent.setDirection.x + " current:" + b2dComponent.body.getPosition().x);
      //  Gdx.app.debug(TAG, "Screen is touched " + Gdx.input.getX());


        if ( b2dComponent.setDirection.x >b2dComponent.body.getPosition().x ){

          xFactor =1;


       }else {
            xFactor =-1;
        }

       if ( b2dComponent.setDirection.y >b2dComponent.body.getPosition().y){

            yFactor =1;


        }else {

               yFactor =-1;

       }

       if (b2dComponent.setDirection.x == 0 & b2dComponent.setDirection.y == 0){

           xFactor=yFactor=0;
        }
       // b2dComponent.body.setTransform(b2dComponent.setDirection.x,b2dComponent.setDirection.y-b2dComponent.height/2,0);

       // Gdx.app.debug(TAG, "Screen is touched " + b2dComponent.setDirection.x +"Y: "+b2dComponent.setDirection.y);

    /*    if (playerComponent.hasSelected){
        b2dComponent.body.setTransform(b2dComponent.setDirection.x,b2dComponent.setDirection.y,0);

       }
     */

        //   b2dComponent.body.setTransform(b2dComponent.setDirection.x,b2dComponent.setDirection.y,0);

/*

        if  ((Math.abs( b2dComponent.setDirection.x-b2dComponent.body.getPosition().x)<1.15) & (Math.abs( b2dComponent.setDirection.y-b2dComponent.body.getPosition().y)<1.15)) {
        }else if ((Math.abs( b2dComponent.setDirection.x-b2dComponent.body.getPosition().x)<1.15)){

            b2dComponent.body.applyLinearImpulse(

                  0,//* b2dComponent.body.getMass(),
                    ((yFactor * playerComponent.speed.y - b2dComponent.body.getLinearVelocity().y)),//* b2dComponent.body.getMass(),
                    b2dComponent.body.getWorldCenter().x, b2dComponent.body.getWorldCenter().y,
                    true


            );


        }else if  (Math.abs( b2dComponent.setDirection.y-b2dComponent.body.getPosition().y)<1.15){

            b2dComponent.body.applyLinearImpulse(

                    ((xFactor * playerComponent.speed.x - b2dComponent.body.getLinearVelocity().x)),//* b2dComponent.body.getMass(),
                    0,//* b2dComponent.body.getMass(),
                    b2dComponent.body.getWorldCenter().x, b2dComponent.body.getWorldCenter().y,
                    true


            );

        }
         else {
        }

       */

              //      Gdx.app.debug(TAG, "Screen is touched "+ Math.abs( b2dComponent.setDirection.y-b2dComponent.body.getPosition().y));
           // b2dComponent.body.

        //hova menjenű
/*
        int pointX= Gdx.input.getX();
            int pointY =   Gdx.input.getY();

        Vector3 vector = new Vector3(pointX,pointY, 0);
        Vector3 vectorUp = camer.unproject(vector);

        boolean wasTouched = b2dComponent.body.getFixtureList().first().testPoint(vectorUp.x,vectorUp.y);

        if (wasTouched) {
            Gdx.app.debug(TAG, "Body was touched ");
            // b2dComp

            //Handle touch
        }

 */


            b2dComponent.body.applyLinearImpulse(

                    ((xFactor * playerComponent.speed.x - b2dComponent.body.getLinearVelocity().x)),//* b2dComponent.body.getMass(),
                    ((yFactor * playerComponent.speed.y - b2dComponent.body.getLinearVelocity().y)),//* b2dComponent.body.getMass(),
                    b2dComponent.body.getWorldCenter().x, b2dComponent.body.getWorldCenter().y,
                    true


            );


   //     }
       // }

    }

    @Override
    public void keyPressed(final InputManager manager,final GameKeys key) {
        switch (key){
            case LEFT:
                directionChange = true;
                xFactor =-1;



                break;


            case RIGHT:
                directionChange = true;
                xFactor =1;
                break;


            case UP:

                directionChange= true;
                yFactor =1;
                break;
            case DOWN:
                directionChange = true;
                yFactor=-1;
                break;


            default:
                return;




        }


    }


    @Override
    public void keyUp(final InputManager manager,final GameKeys key) {

        switch (key){



            case LEFT:
                directionChange =true;
                xFactor = manager.isKeyPressed(GameKeys.RIGHT) ? 1:0;

                break;

            case RIGHT:
                directionChange =true;
                xFactor = manager.isKeyPressed(GameKeys.LEFT) ? -1:0;


                break;

            case UP:
                directionChange =true;

                yFactor = manager.isKeyPressed(GameKeys.DOWN) ? -1:0;

                break;

            case DOWN:
                directionChange =true;
                yFactor = manager.isKeyPressed(GameKeys.UP) ? 1:0;

                break;

            default:
                break;


        }


    }
//system tipusok
//itarate, intervall, és a kettö kombinációja




}
