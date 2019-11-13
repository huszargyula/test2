package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

public class WorldContactListener implements ContactListener {

    //ütközés történik kezdődik, befejeződik
    @Override
    public void beginContact(Contact contact) {

        //a két test ami ütközik
        final Fixture fixtureA = contact.getFixtureA();
        final Fixture fixtureB = contact.getFixtureB();


        Gdx.app.debug("CONTACT", "BEGIN:"+fixtureA.getBody().getUserData() + "" +fixtureA.isSensor());
        Gdx.app.debug("CONTACT", "BEGIN:"+fixtureB.getBody().getUserData() + "" +fixtureB.isSensor());

    }

    @Override
    public void endContact(Contact contact) {

        final Fixture fixtureA = contact.getFixtureA();
        final Fixture fixtureB = contact.getFixtureB();


        Gdx.app.debug("CONTACT", "END:"+fixtureA.getBody().getUserData() + "" +fixtureA.isSensor());
        Gdx.app.debug("CONTACT", "END:"+fixtureB.getBody().getUserData() + "" +fixtureB.isSensor());




    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {


        //példa
        // contact.setEnabled(false);


    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}

