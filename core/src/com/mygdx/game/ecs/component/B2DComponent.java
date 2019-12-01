package com.mygdx.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
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
    public boolean directionsSetted;

    // hová kattintott a játékos
//    public Vector3 setDirection= new Vector3();


    //animáciohoz
    public Vector2 renderPosition = new Vector2();

    @Override
    public void reset() {

       //setDirection.set(0,0,0);
         lightDistance =0;
         lightFluctuacionDistance =0;
         lightFluctuacionTime=0;
        lightFluctuacionSpeed=0;
        directionsSetted = false;


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
