package com.mygdx.game.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
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



    public PlayerMovmentSystem(final MyTowerDefenseGame context) {
        // kül famayli metodus hívások
        //family.all, exlude, one

        //all amibe az összes van
        super(Family.all(PlayerComponent.class, B2DComponent.class).get());
        //igy minden entitásra megvan hívva, ami player comptonent és B2d component
        //itt most ez midneig 1 entity, a player

        context.getInputManager().addInputListener(this);

      //  directionChange =false; //ezt és ennek meghívását ki lehet kommentelni
        xFactor=yFactor =0;

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


            directionChange =false; //ne hívodjon minig meg
            b2dComponent.body.applyLinearImpulse(
                    (xFactor * playerComponent.speed.x - b2dComponent.body.getLinearVelocity().x )* b2dComponent.body.getMass(),
                    (yFactor * playerComponent.speed.y - b2dComponent.body.getLinearVelocity().y )* b2dComponent.body.getMass(),
                    b2dComponent.body.getWorldCenter().x, b2dComponent.body.getWorldCenter().y,
                    true
            );
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
