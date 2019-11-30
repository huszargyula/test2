package com.mygdx.game.map;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.MyTowerDefenseGame;
import com.mygdx.game.ecs.ECSEngine;

import java.util.EnumMap;



import static com.mygdx.game.MyTowerDefenseGame.BIT_GROUND;

public class MapManager {

    public static final String TAG = MapManager.class.getSimpleName();
    private final World world;
    private final Array<Body> bodies;

    private final AssetManager assetManager;
    private  ECSEngine ecsEngine;
    private final Array<Entity> gameObjectsToRemove;

   private MapLoader currentMap; //loader
    private final Array<MapListener> listeners;


    public MapManager(final MyTowerDefenseGame context) {

        world = context.getWorld();
        assetManager = context.getAssetManager();
        bodies = new Array<Body>();

        gameObjectsToRemove = new Array<Entity>();
        listeners = new Array<MapListener>(); // ? ez csak a térképváltáshoz?



    }



    public void setEcsEngine(ECSEngine engine){


        this.ecsEngine = engine;//proba
    }


    public void addMapListener(final MapListener listener) {
        listeners.add(listener);

    }

    public void setMap(final MapType type) {

        //ilyen nem lesz már a nagy


       // if (currentMap != null) {
//TODO ezt lehet itt ?
            world.getBodies(bodies);




        Gdx.app.debug(TAG, "Changing to map" + type);


            Gdx.app.debug(TAG, "Creatin new map of type" + type);

            //TODO ezt se kell minden térkép betöéltésnél
            final TiledMap tiledMap = assetManager.get(type.getFilePath(), TiledMap.class); //


        if (currentMap!=null){

            currentMap.clearMapLoader();
            currentMap.loadMapLoader(tiledMap);
        }

        if (currentMap==null) {
            currentMap = new MapLoader(tiledMap);

        }


        spawnCollisionAreas();
        spawnGameObjects();

        for (final MapListener listener : listeners) {
            listener.mapChanged(currentMap);

        }

    }


    //EREDETI
/*
    public void setMap(final MapType type) {

        //ilyen nem lesz már a nagyobb mapok miatt!
        if (currentMapType == type) {
            //map is already yet
            return;
        }


        if (currentMap != null) {

            world.getBodies(bodies);
            destroyCollisionAreas();
            destroyGameObjects();
        }


        //set newmap
        Gdx.app.debug(TAG, "Changing to map" + type);
        currentMap = mapCache.get(type);

        if (currentMap == null){
            Gdx.app.debug(TAG, "Creatin new map of type" + type);
            final TiledMap tiledMap = assetManager.get(type.getFilePath(), TiledMap.class); //ezt is scak nullázuk
            currentMap = new MapLoader(tiledMap);
            //     currentMap.clearMapLoadera();
            //    currentMap.loadMapLoader(tiledMap);
            //kell új ?
            mapCache.put(type,currentMap); // ha nincs cahs, akkor felesleges!

        }



        spawnCollisionAreas();
        spawnGameObjects();

        for (final MapListener listener : listeners) {
            listener.mapChanged(currentMap);

        }

    }

*/
    public void destroyGameObjects() {
        //két külön tárolás az array index átugrása miatt
        //volt:
        /*
        for (final Entity entity: ecsEngine.getEntities()){
            if (ECSEngine.gameObjCmpMapper.get(entity)!=null){//26:41
                gameObjectsToRemove.add(entity);
            }

        }
        for (final Entity entity :gameObjectsToRemove){

            ecsEngine.removeEntity(entity);
        }



        gameObjectsToRemove.clear();

        */
        ///---uj :minden entity törlésre kerül


        for (final Entity entity: ecsEngine.getEntities()){
               gameObjectsToRemove.add(entity);


        }
        for (final Entity entity :gameObjectsToRemove){

            ecsEngine.removeEntity(entity);
        }



        gameObjectsToRemove.clear();


    }

    private void spawnGameObjects() {
        for (final GameObject gameObject : currentMap.getGameObjects()){

            ecsEngine.createGameObject(gameObject);
        }


    }

    public void destroyCollisionAreas(){

        for (final Body body :bodies){

            if ("GROUND".equals(body.getUserData())){

                world.destroyBody(body);
            }

        }



}





    private void spawnCollisionAreas(){
        //későbbiekben a mapmangere class használata

        MyTowerDefenseGame.resetBodieAndFixtureDefinition();

        for (final CollisionArea collisionArea : currentMap.getCollisionAreas()){



            //poziicó
            MyTowerDefenseGame.BODY_DEF.position.set(collisionArea.getX(),collisionArea.getY());
            MyTowerDefenseGame.BODY_DEF.fixedRotation=true;

            final Body body= world.createBody(MyTowerDefenseGame.BODY_DEF);
            body.setUserData("GROUND");

            MyTowerDefenseGame.FIXTURE_DEF.filter.categoryBits = BIT_GROUND;
            MyTowerDefenseGame.FIXTURE_DEF.filter.maskBits = -1 ;
            final ChainShape chainShape = new ChainShape();
            chainShape.createChain(collisionArea.getVertices());
            //összekötjük a kettőt
            MyTowerDefenseGame.FIXTURE_DEF.shape =chainShape;
            body.createFixture(MyTowerDefenseGame.FIXTURE_DEF);
            chainShape.dispose();


        }

    }


    public MapLoader getCurrentMap(){return currentMap;}


}
