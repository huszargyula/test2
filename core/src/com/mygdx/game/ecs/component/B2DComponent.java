package com.mygdx.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Pool;

import box2dLight.Light;

public class B2DComponent implements Component, Pool.Poolable {

    public Body body; //Box2d body
    public float width;
    public float height;
    //világitás és beállításai:
    public Light light;
    public float lightDistance;
    public float lightFluctuacionDistance;
    public float lightFluctuacionTime;
    public float lightFluctuacionSpeed;


    //animáciohoz
    public Vector2 renderPosition = new Vector2();

    @Override
    public void reset() {
         lightDistance =0;
         lightFluctuacionDistance =0;
         lightFluctuacionTime=0;
        lightFluctuacionSpeed=0;


        if (light!=null){
            light.remove(true);
            light=null;
        }

        if (body != null){
            //takarítás
            body.getWorld().destroyBody(body);
            body = null;

        }
        width =height =0;
        renderPosition.set(0,0);

    }
}
