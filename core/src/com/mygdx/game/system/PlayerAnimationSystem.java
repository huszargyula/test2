package com.mygdx.game.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
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


        if (b2DComponent.body.getLinearVelocity().equals(Vector2.Zero)){
          /* //player does not move azaz ha nem akarjuk mozgatni
            //ha nem mozogna mondjuk lehet lenne optimálisabb
            animationComponent.aniTime=0;
            */

        }else if(b2DComponent.body.getLinearVelocity().x>0){

            animationComponent.aniType = AnimationType.HERO_MOVE_RIGHT;

        }else  if(b2DComponent.body.getLinearVelocity().x<0){

            animationComponent.aniType = AnimationType.HERO_MOVE_LEFT;

        }else if(b2DComponent.body.getLinearVelocity().y>0){

            //animationComponent.aniType = AnimationType.HERO_MOVE_UP;
            animationComponent.aniType = AnimationType.HERO_MOVE_RIGHT;

        }else if(b2DComponent.body.getLinearVelocity().y<0){

            //animationComponent.aniType = AnimationType.HERO_MOVE_DOWN;
            animationComponent.aniType = AnimationType.HERO_MOVE_LEFT;
        }

    }
}
