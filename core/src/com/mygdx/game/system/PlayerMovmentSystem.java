package com.mygdx.game.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.MyTowerDefenseGame;
import com.mygdx.game.ecs.ECSEngine;
import com.mygdx.game.ecs.component.B2DComponent;
import com.mygdx.game.ecs.component.PlayerComponent;
import com.mygdx.game.ecs.component.PlayerIconComponent;
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
    public MyTowerDefenseGame context;
    public Vector2 direction;

    public boolean isSelected;
  //  MyTowerDefenseGame context;
    public ImmutableArray<Entity> playerIconEntites;


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
        this.context = context;
        direction = new Vector2(0,0);
    }

    //ez elvileg minden entitásra meg lesz hívva ami a "familye" része
    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        playerIconEntites = context.getEcsEngine().getEntitiesFor(Family.all( PlayerIconComponent.class).get());
        final  PlayerComponent playerComponent=  ECSEngine.playerCmpMapper.get(entity); // ez ugyanaz mint a felette levő, csak component mapperrel, gyorsabb
        final  B2DComponent b2dComponent = ECSEngine.b2dCmpMapper.get(entity);

    directionChange =false; //ne hívodjon minig meg

       // Gdx.app.debug("RenderInfo X", (float) b2dComponent.setDirection.x + " current:" + b2dComponent.body.getPosition().x);
      //  Gdx.app.debug(TAG, "Screen is touched " + Gdx.input.getX());


        if ( playerComponent.setDirection.x >b2dComponent.body.getPosition().x ){

          xFactor =1;


       }else {
            xFactor =-1;
        }

       if ( playerComponent.setDirection.y >b2dComponent.body.getPosition().y){

            yFactor =1;


        }else {

               yFactor =-1;

       }

       if (playerComponent.setDirection.x == 0 & playerComponent.setDirection.y == 0){

           xFactor=yFactor=0;
        }


            //b2dComponent.body.applyAngularImpulse();
        //TODO NE HOZZ AMINDIG LÉTRE? TÖBBIT KITÖRÖLNI!
        direction.set(playerComponent.setDirection.x, playerComponent.setDirection.y);
        direction.sub(b2dComponent.body.getPosition());
        direction.nor();



        if (Math.abs(playerComponent.setDirection.x-b2dComponent.body.getPosition().x)<0.5f &  Math.abs(playerComponent.setDirection.y-b2dComponent.body.getPosition().y)<0.5f) {
            b2dComponent.body.setLinearVelocity(0,0);
            //nem mozog
        }else{
            b2dComponent.body.setLinearVelocity(direction.scl(playerComponent.speed));


        }




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
