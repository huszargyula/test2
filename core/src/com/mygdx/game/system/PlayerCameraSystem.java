package com.mygdx.game.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.mygdx.game.MyTowerDefenseGame;
import com.mygdx.game.ecs.ECSEngine;
import com.mygdx.game.ecs.component.B2DComponent;
import com.mygdx.game.ecs.component.PlayerComponent;

public class PlayerCameraSystem extends IteratingSystem {
    private final OrthographicCamera gameCamera;

    public PlayerCameraSystem(final MyTowerDefenseGame context){
        super(Family.all(PlayerComponent.class, B2DComponent.class).get());
        gameCamera = context.getGameCamera();

    }


    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        //gamekamera mpzgatás
        gameCamera.position.set(ECSEngine.b2dCmpMapper.get(entity).renderPosition,0);

    }
}
