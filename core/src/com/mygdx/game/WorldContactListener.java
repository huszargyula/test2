package com.mygdx.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Array;


import static com.mygdx.game.MyTowerDefenseGame.BIT_ENEMY;
import static com.mygdx.game.MyTowerDefenseGame.BIT_GAME_OBJECT;
import static com.mygdx.game.MyTowerDefenseGame.BIT_PLAYER;

public class    WorldContactListener implements ContactListener {
    private final Array<PlayerCollisionListener> listeners;

    public WorldContactListener(){
        listeners = new Array<PlayerCollisionListener>();

    }

    public  void addPlayerCollisionListener (final PlayerCollisionListener listener){
        listeners.add(listener);

    }

    //ütközés történik kezdődik, befejeződik
    @Override
    public void beginContact(Contact contact) {

        //régi példa
        //a két test ami ütközik
      //  final Fixture fixtureA = contact.getFixtureA();
       // final Fixture fixtureB = contact.getFixtureB();


        //Gdx.app.debug("CONTACT", "BEGIN:"+fixtureA.getBody().getUserData() + "" +fixtureA.isSensor());
       // Gdx.app.debug("CONTACT", "BEGIN:"+fixtureB.getBody().getUserData() + "" +fixtureB.isSensor());

        //gameObject + player érintkezik
        final Entity player;
        final Entity enemy; //TODO ez enemyObject
        //a két ütköző test
        final Body bodyA = contact.getFixtureA().getBody();
        final Body bodyB = contact.getFixtureB().getBody();
        // kül kategóriák is vannak /Bit game Object- playerBit ezt kell ellenőrizni
        final int catFixA =  contact.getFixtureA().getFilterData().categoryBits;
        final int catFixB =  contact.getFixtureB().getFilterData().categoryBits;


        if ((int)(catFixA & BIT_PLAYER )== BIT_PLAYER){
            player = (Entity) bodyA.getUserData();

        }else if ((int)(catFixB & BIT_PLAYER )== BIT_PLAYER) {
            player = (Entity) bodyB.getUserData();

        }else  {

         //nincs player Contact
            return;

        }

        if ((int)(catFixA & BIT_ENEMY )== BIT_ENEMY){
            enemy = (Entity) bodyA.getUserData();
        }else if ((int)(catFixB & BIT_ENEMY )== BIT_ENEMY) {
            enemy = (Entity) bodyB.getUserData();
        }else  {

            //nincs enemy entity  Contactban
            return;
        }

        //Gdx.app.debug("COLLDEBUG", "player collides with gameObj");

        for (final PlayerCollisionListener listener:listeners){

            listener.playerCollision(player,enemy);
        }

    }

    @Override
    public void endContact(Contact contact) {


        //we dont  neeed a special logic here
     // final Fixture fixtureA = contact.getFixtureA();
     //  final Fixture fixtureB = contact.getFixtureB();


     //   Gdx.app.debug("CONTACT", "END:"+fixtureA.getBody().getUserData() + "" +fixtureA.isSensor());
     //   Gdx.app.debug("CONTACT", "END:"+fixtureB.getBody().getUserData() + "" +fixtureB.isSensor());




    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

        //we dont  neeed a special logic here

        //példa
        // contact.setEnabled(false);
        // from top side (falling) : can go through platform
        // from bottom side (jumping ) cant through , stick to platform
        //van pédakód simon githubon

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

        //we dont  neeed a special logic here


    }
    public interface PlayerCollisionListener{
        void playerCollision(final Entity player,final Entity gamObject);

    }


}

