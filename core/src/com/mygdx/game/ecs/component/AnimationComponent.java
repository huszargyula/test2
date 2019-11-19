package com.mygdx.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.game.UI.AnimationType;

public class AnimationComponent implements Component, Pool.Poolable{
    public AnimationType aniType;
    public float aniTime;
    public float width;
    public float height;


    @Override
    public void reset() {
        aniType = null;
        aniTime = 0;
        width =0;
        height=0;

    }
}
