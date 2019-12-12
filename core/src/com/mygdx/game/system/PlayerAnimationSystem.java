package com.mygdx.game.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.MyTowerDefenseGame;
import com.mygdx.game.UI.AnimationType;
import com.mygdx.game.ecs.ECSEngine;
import com.mygdx.game.ecs.component.AnimationComponent;
import com.mygdx.game.ecs.component.B2DComponent;
import com.mygdx.game.ecs.component.PlayerComponent;

public class PlayerAnimationSystem extends IteratingSystem {


    public PlayerAnimationSystem(MyTowerDefenseGame context){
        //aki mind3 ban benne van csak arra igaz
        super((Family.all(AnimationComponent.class,PlayerComponent.class , B2DComponent.class).get()));

    }


    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        final B2DComponent b2DComponent = ECSEngine.b2dCmpMapper.get(entity);
        final AnimationComponent animationComponent = ECSEngine.aniCmpMapper.get(entity);




        if (b2DComponent.body.getLinearVelocity().equals(Vector2.Zero)==false ) {
        resetAnimationComponentSettings(animationComponent);

        }

            if (b2DComponent.body.getLinearVelocity().equals(Vector2.Zero) ){
        //    Gdx.app.debug("Most áll","NAVEGRE");

        }else if(b2DComponent.body.getLinearVelocity().x>0){
         //   resetAnimationComponentSettings(animationComponent);
            animationComponent.aniType = AnimationType.HERO_MOVE_RIGHT;
          //  Gdx.app.debug("Most 1","dd");


        }else  if(b2DComponent.body.getLinearVelocity().x<0){
           // resetAnimationComponentSettings(animationComponent);

            animationComponent.aniType = AnimationType.HERO_MOVE_LEFT;
            //Gdx.app.debug("Most 2","dd");


        }else if(b2DComponent.body.getLinearVelocity().y>0){

           // resetAnimationComponentSettings(animationComponent);
            //animationComponent.aniType = AnimationType.HERO_MOVE_UP;
            animationComponent.aniType = AnimationType.HERO_MOVE_RIGHT;
            //Gdx.app.debug("Most 3","ddddd");

        }else if(b2DComponent.body.getLinearVelocity().y<0){
         //   resetAnimationComponentSettings(animationComponent);
            //animationComponent.aniType = AnimationType.HERO_MOVE_DOWN;
            //Gdx.app.debug("Most 4","d");

            animationComponent.aniType = AnimationType.HERO_MOVE_LEFT;
        }else{

            //Gdx.app.debug("Most áll","dddd");


        }




    }

    public void resetAnimationComponentSettings(AnimationComponent animationComponent){

        animationComponent.isSpecificFrameStarted = false;
        animationComponent.isSpecificFrameFinished = false;
        animationComponent.startAnimationCyle = false;
        animationComponent.finishAnimationCycle= false;

    }

}
