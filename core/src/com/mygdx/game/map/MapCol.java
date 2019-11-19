package com.mygdx.game.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import static com.mygdx.game.MyTowerDefenseGame.UNIT_SCALE;

public class MapCol {

    public static final String TAG = MapCol.class.getSimpleName();

    private final TiledMap tiledMap;
    private  final Array<CollisionArea> collisionAreas;
    private final Vector2 startLocation;


    public MapCol(final TiledMap tiledMap) {
        this.tiledMap = tiledMap;
        collisionAreas = new Array<CollisionArea>();
        startLocation = new Vector2();
        parseCollisionLayer();
        parsePlayerStartLocation();


    }

    private void parsePlayerStartLocation() {

        //Start Locationok kiolvasása

        final MapLayer startLocationLayer = tiledMap.getLayers().get("playerStart");
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

    public Array<CollisionArea> getCollisionAreas() {
        return collisionAreas;
    }

    public TiledMap getTiledMap() {
        return this.tiledMap; // nem tuti
    }
}
