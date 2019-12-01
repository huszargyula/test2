package com.mygdx.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;

public class PlayerComponent implements Component, Pool.Poolable {

    public boolean hasAxe;
    public Vector2 speed = new Vector2();
    public boolean hasSelected;
    public int playerId;
    public Vector3 setDirection= new Vector3();


    @Override
    public void reset() {

        hasSelected = false;
        hasAxe = false;
        speed.set(0,0);
        playerId = 0;
        setDirection.set(0,0,0);

    }


}
