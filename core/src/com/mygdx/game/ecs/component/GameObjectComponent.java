package com.mygdx.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.game.map.GameObjectType;


public class GameObjectComponent implements Component, Pool.Poolable {

    public int animationIndex;
    public GameObjectType type;

    @Override
    public void reset() {
        type = null;
        animationIndex =-1;
    }
}
