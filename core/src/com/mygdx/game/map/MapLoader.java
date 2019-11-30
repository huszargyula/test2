package com.mygdx.game.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;

import static com.mygdx.game.MyTowerDefenseGame.UNIT_SCALE;

public class MapLoader {

    public static final String TAG = MapLoader.class.getSimpleName();

    public TiledMap tiledMap;
    private  final Array<CollisionArea> collisionAreas;
    private final Vector2 startLocation;
    private final Array gameObjects;
    private final IntMap<Animation<Sprite>> mapAnimations;
    private  Vector2 enemyStartLocation;
    private Array<Vector2> enemyPath;


    public MapLoader(TiledMap tiledMap) {
        this.tiledMap = tiledMap;

        collisionAreas = new Array<CollisionArea>();
        parseCollisionLayer();

        startLocation = new Vector2();
        parsePlayerStartLocation();

        gameObjects = new Array<GameObject>();
        mapAnimations = new IntMap<Animation<Sprite>>();
        parseGameObjectLayer();


        enemyStartLocation = new Vector2();
        parseEnemyStartLocation();


        enemyPath = new Array<Vector2>();
        parseEnemyPathLocations();


    }

    public void loadMapLoader(final TiledMap tiledMap){

        this.tiledMap = tiledMap;


        parseCollisionLayer();

        parsePlayerStartLocation();

        parseGameObjectLayer();

        parseEnemyStartLocation();

        parseEnemyPathLocations();

    }

    public void clearMapLoader(){

        collisionAreas.clear();
        startLocation.set(0,0);
        mapAnimations.clear();
        gameObjects.clear();

        enemyStartLocation.set(0,0);
        enemyPath.clear();



    }

    private void parseEnemyPathLocations(){
        //Start Locationok kiolvasása
        //Vector2 vector = new Vector2();
        Gdx.app.debug(TAG, "ENEMYPATH");

        final MapLayer startLocationLayer = tiledMap.getLayers().get("pathFinder");
        if (startLocationLayer == null) {

            Gdx.app.debug(TAG, "There is no pathFinder layer!");
            return;
        }

        final MapObjects objects = startLocationLayer.getObjects();

        for (final MapObject mapObj : startLocationLayer.getObjects()) {

            if (mapObj instanceof RectangleMapObject) {

                final RectangleMapObject rectangleMapObject = (RectangleMapObject) mapObj;
                final Rectangle rectangle = rectangleMapObject.getRectangle();
                enemyPath.add(new Vector2(rectangle.x * UNIT_SCALE, rectangle.y * UNIT_SCALE ));
                Gdx.app.debug(TAG,"ENEMY Path betöltve"+ enemyPath.size);

            } else{
                Gdx.app.debug(TAG,"MapObjectype" + mapObj +" is not supported for startPlayerLocation layer!");




            }


        }





    }


    private void parseEnemyStartLocation(){

        //Start Locationok kiolvasása

        final MapLayer startLocationLayer = tiledMap.getLayers().get("enemyStart");
        if (startLocationLayer == null) {

            Gdx.app.debug(TAG, "There is no enemyStart layer!");
            return;
        }

        final MapObjects objects = startLocationLayer.getObjects();

        for (final MapObject mapObj : startLocationLayer.getObjects()) {

            if (mapObj instanceof RectangleMapObject) {

                final RectangleMapObject rectangleMapObject = (RectangleMapObject) mapObj;
                final Rectangle rectangle = rectangleMapObject.getRectangle();
                enemyStartLocation.set(rectangle.x * UNIT_SCALE, rectangle.y * UNIT_SCALE );
            } else{
                Gdx.app.debug(TAG,"MapObjectype" + mapObj +" is not supported for startPlayerLocation layer!");




            }


        }



    }

    private void parseGameObjectLayer() {
        //game Objectek beolvásáááása
        final MapLayer gameObjectsLayer = tiledMap.getLayers().get("gameObjects");
        if( gameObjectsLayer == null){

            Gdx.app.debug(TAG, "There is no gameObject layer!");
            return;

        }

        final MapObjects objects = gameObjectsLayer.getObjects();
        for (final MapObject mapObj: objects){

            if (!(mapObj instanceof TiledMapTileMapObject)){

                Gdx.app.debug(TAG, "GameObject of type"+ mapObj + "is not supported!");
                continue;

            }
            final TiledMapTileMapObject tiledMapObj = (TiledMapTileMapObject) mapObj;
            //az instancek tulajdonságai- test,type, stb
            final MapProperties tiledMapObjProperties = tiledMapObj.getProperties();
            //Tileset tulajdonságai
            final MapProperties tileProperties = tiledMapObj.getTile().getProperties();
            final GameObjectType gameObjType;

            if (tiledMapObjProperties.containsKey("type")){

                gameObjType = GameObjectType.valueOf(tiledMapObjProperties.get("type",String.class));


            } else if (tileProperties.containsKey("type")) {

                gameObjType = GameObjectType.valueOf(tileProperties.get("type",String.class));

            }else{

                Gdx.app.debug(TAG, "There is no gameObject type defined for tile! "+ tiledMapObjProperties.get("id",Integer.class));
                continue;
            }

            final int animationIndex = tiledMapObj.getTile().getId();

            if (!createAnimation(animationIndex,tiledMapObj.getTile())){
                Gdx.app.debug(TAG, "Could not create animation for tile!"+ tiledMapObjProperties.get("id",Integer.class));

                continue;
            }

            final float width =tiledMapObjProperties.get("width",Float.class)*UNIT_SCALE;
            final float height =tiledMapObjProperties.get("height",Float.class)*UNIT_SCALE;

            gameObjects.add(new GameObject(gameObjType,new Vector2(tiledMapObj.getX()*UNIT_SCALE,tiledMapObj.getY()*UNIT_SCALE),width,height,tiledMapObj.getRotation(),animationIndex));

        }

    }

    private boolean createAnimation(int animationIndex, TiledMapTile tile) {
        Animation<Sprite> animation = mapAnimations.get(animationIndex);
        if (animation == null){
            Gdx.app.debug(TAG,"Createing new map animations for tile" +tile.getId());

            if (tile instanceof AnimatedTiledMapTile) {
                final AnimatedTiledMapTile aniTile = (AnimatedTiledMapTile) tile;
                final Sprite[] keyFrames = new Sprite[aniTile.getFrameTiles().length];
                int i = 0;
                //visszaadja a staikus képeit az animációnak
                for (final StaticTiledMapTile staticTile : aniTile.getFrameTiles()) {

                    keyFrames[i++] = new Sprite(staticTile.getTextureRegion());
                }
                //itt csak az elsőt olvassuk ki mert a rendszer nem támogatja h kül idő teljen el két frame között
                animation = new Animation<Sprite>(aniTile.getAnimationIntervals()[0] * 0.001f, keyFrames);

                animation.setPlayMode(Animation.PlayMode.LOOP);
                mapAnimations.put(animationIndex, animation);

                }else if (tile instanceof StaticTiledMapTile){
                //itt igazából nincs animáció
                    animation = new Animation<Sprite>(0, new Sprite(tile.getTextureRegion()));
                    mapAnimations.put(animationIndex,animation);
                }else{

                Gdx.app.debug(TAG,"Tile of type" +tile + "is not supported for map animations");
                return false;

            }



        }
        return true;
    }

    private void parsePlayerStartLocation() {

        //Start Locationok kiolvasása
        //ezt átnéz
         MapLayer startLocationLayer = tiledMap.getLayers().get("playerStart");
        if (startLocationLayer == null) {

            Gdx.app.debug(TAG, "There is no startLocationLayer layer!");
            return;
        }

        final MapObjects objects = startLocationLayer.getObjects();

        for (final MapObject mapObj : startLocationLayer.getObjects()) {

            if (mapObj instanceof RectangleMapObject) {
                Gdx.app.debug(TAG, "RECTTANGLE  betoltve?");
                final RectangleMapObject rectangleMapObject = (RectangleMapObject) mapObj;
                final Rectangle rectangle = rectangleMapObject.getRectangle();
                startLocation.set(rectangle.x * UNIT_SCALE, rectangle.y * UNIT_SCALE );
            } else{
                Gdx.app.debug(TAG,"MapObjectype" + mapObj +" is not supported for startPlayerLocation layer!");




            }


        }
    }

    private void parseCollisionLayer() {
        //hozzáférés a tiledmap rétegeihez

        Gdx.app.debug(TAG,"HELLO");
        System.out.println("HeLLOOOOOOOOOO");

        final MapLayer collisionLayer =  tiledMap.getLayers().get("collision");
        if (collisionLayer == null){

            Gdx.app.debug(TAG,"There is no collision layer!");
            return  ;
        }

        //TRy catch ?
        //kiszedjük a tiled layerből a collision objekteinket
        //final Array<EllipseMapObject> ellipseMapObjectArray = collisionLayer.getObjects().getByType(EllipseMapObject.class) ;
        //nemjó a getobject mindig uj instancet hív meg , amit  agarbage collector gyűjt be, ez kerülendő

        //Másik opció

        final MapObjects mapObjects = collisionLayer.getObjects();


        for (final MapObject mapObj : mapObjects){

            if (mapObj instanceof RectangleMapObject){
                //a kérédés h kell e olyan üres négyzet aminek nincs képe de nem kell a koll areának.

                Gdx.app.debug(TAG, "RECTTANGLE  betoltve?");
                final RectangleMapObject rectangleMapObject = (RectangleMapObject) mapObj;
                final Rectangle rectangle = rectangleMapObject.getRectangle();
                final float[] rectVertices = new float[10];
                // left-bottom,

                rectVertices[0]=0;
                rectVertices[1]=0;

                //left top


                rectVertices[2]=0;
                rectVertices[3]=rectangle.height;

                //rigt top

                rectVertices[4]=rectangle.width;
                rectVertices[5]=rectangle.height;


                //right bottom

                rectVertices[6]=rectangle.width;
                rectVertices[7]=0;


                //left bottom

                rectVertices[8]=0;
                rectVertices[9]=0;

                collisionAreas.add(new CollisionArea(rectangle.x,rectangle.y, rectVertices));


            }

            else if (mapObj instanceof EllipseMapObject){
                Gdx.app.debug(TAG, "ELLIPSE betoltve?");
                final EllipseMapObject ellipseMapObject = (EllipseMapObject) mapObj;
                //final Ellipse ellipse = ellipseMapObject.getEllipse().
                // final float elipsVertices=
                // collisionAreas.add(new CollisionArea(ellipse.x,ellipse.y,
                //na ez egy jó kérdés
                // fügvénnyel ?
            }  else if (mapObj instanceof PolylineMapObject){
                Gdx.app.debug(TAG, "POLYLINE betoltve?");
                final PolylineMapObject polyLineMapObject = (PolylineMapObject) mapObj;
                final Polyline polyline = polyLineMapObject.getPolyline();
                collisionAreas.add(new CollisionArea(polyline.getX(), polyline.getY(), polyline.getVertices()));

            }else{
                Gdx.app.debug(TAG,"MapObjectype" + mapObj +" is not supported for collision layer!");

            }




        }

    }



    public Vector2 getStartLocation() {
        return startLocation;
    }

    public Vector2 getEnemyStartLocation() {return enemyStartLocation;}

    public Array<CollisionArea> getCollisionAreas() {
        return collisionAreas;
    }

    public Array<Vector2> getEnemyPath() { return enemyPath; }

    public TiledMap getTiledMap() {
        return this.tiledMap; // nem tuti
    }

    public Array<GameObject> getGameObjects() {
        return gameObjects;
    }

    public IntMap<Animation<Sprite>> getMapAnimations() {
        return mapAnimations;
    }
}
