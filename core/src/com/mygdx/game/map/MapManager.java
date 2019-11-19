package com.mygdx.game.map;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
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
    private final ECSEngine ecsEngine;
    private final Array<Entity> gameObjectsToRemove;

    private MapType currentMapType;
    private MapCol currentMap; //ez sztem MAPCOl
    private final EnumMap<MapType, MapCol> mapCache;
    private final Array<MapListener> listeners;

    public MapManager(final MyTowerDefenseGame context) {
        currentMapType = null;
        currentMap = null;
        world = context.getWorld();
        ecsEngine =context.getEcsEngine();
        gameObjectsToRemove = new Array<Entity>();
        assetManager = context.getAssetManager();
        bodies = new Array<Body>();
        mapCache = new EnumMap<MapType, MapCol>(MapType.class);
        listeners = new Array<MapListener>();



    }

    public void addMapListener(final MapListener listener) {
        listeners.add(listener);

    }

    public void setMap(final MapType type) {
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
            final TiledMap tiledMap = assetManager.get(type.getFilePath(), TiledMap.class);
            currentMap = new MapCol(tiledMap);
            mapCache.put(type,currentMap);

        }



        spawnCollisionAreas();
        spawnGameObjects();

        for (final MapListener listener : listeners) {
            listener.mapChanged(currentMap);

        }

    }

    private void destroyGameObjects() {
        //két külön tárolás az array index átugrása miatt
        for (final Entity entity: ecsEngine.getEntities()){
            if (ECSEngine.gameObjcmpMapper.get(entity)!=null){//26:41
                gameObjectsToRemove.add(entity);

            }
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

    private void destroyCollisionAreas(){

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


    public MapCol getCurrentMap(){return currentMap;}


}
