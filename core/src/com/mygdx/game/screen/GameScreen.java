package com.mygdx.game.screen;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.game.MyTowerDefenseGame;
import com.mygdx.game.UI.GameUI;
import com.mygdx.game.input.GameKeys;
import com.mygdx.game.input.InputManager;
import com.mygdx.game.map.CollisionArea;
import com.mygdx.game.map.MapCol;

import static com.mygdx.game.MyTowerDefenseGame.BIT_BOARD;
import static com.mygdx.game.MyTowerDefenseGame.BIT_GROUND;
import static com.mygdx.game.MyTowerDefenseGame.BIT_PLAYER;
import static com.mygdx.game.MyTowerDefenseGame.UNIT_SCALE;

public class GameScreen extends AbstractScreen<GameUI>
{   //private final gameTD context;
    private final BodyDef bodyDef; // ez a kettő a leeséshez, fizikai enginhez kell
    private final FixtureDef fixtureDef; // fixture jelentése kellék, alkatrész
    private static final  String TAG =  MyTowerDefenseGame.class.getSimpleName();
    private Body player;
    private final Body bodyBoard;// ? final ????
    private final AssetManager assetManager;
    private final OrthographicCamera gameCamera;
    //profiler
    private final GLProfiler profiler;


    // a karakterhez
    private boolean directionChange;
    private int xFactor;
    private int yFactor;



    //MapCol kirajzolásához mi az egy egység itt 32 a tile map miatt
    private final OrthogonalTiledMapRenderer mapRenderer;
    private MapCol map;

    public GameScreen(final MyTowerDefenseGame context){
        //this.context = context;

        super(context);
        //meg kell mondani h mi
        mapRenderer = new OrthogonalTiledMapRenderer(null,UNIT_SCALE, context.getSpriteBatch());
        assetManager = context.getAssetManager();
        this.gameCamera = context.getGameCamera();

        //profiler
        profiler= new GLProfiler(Gdx.graphics);
        profiler.enable();

        //player
        //létrhozzuk a leeső tárgyakat.
        bodyDef = new BodyDef();
        fixtureDef = new FixtureDef();


        //EZ A SAJÁT JÁTÉK DESZKÁJA
        bodyDef.position.set(4.6f,3);
        //gravitációs skála, alaphelyzetből 1 es
        bodyDef.gravityScale=0;
        // típus
        bodyDef.type= BodyDef.BodyType.DynamicBody;
        //létrehozása a testnek
        bodyBoard= world.createBody(bodyDef);
        bodyBoard.setUserData("DESZKA");

        fixtureDef.density = 1;  //erőhatások mozgatásnák kell
        fixtureDef.isSensor = false;
        //rugalmasság
        fixtureDef.restitution=0;
        //valami ragadás szerű
        fixtureDef.friction=0.2f;
        //filter kategória viz, játékos, fal,
        //. maskbits- mivel ütküzhet.
        //categoryBit. te melyik kategóribán van,.
        fixtureDef.filter.categoryBits = BIT_BOARD;
        fixtureDef.filter.maskBits = BIT_PLAYER ;
        PolygonShape pShape = new PolygonShape();

        pShape.setAsBox(4f,0.5f);


        fixtureDef.shape =pShape;
        bodyBoard.createFixture(fixtureDef);
        pShape.dispose();





        final TiledMap tiledMap = assetManager.get("map/map.tmx", TiledMap.class);

        //ha  már betöltötttük a térképet az assestmengerbe, kapja meg a maprendere
        mapRenderer.setMap(tiledMap);
        map = new MapCol(tiledMap);
        spawnCollisionAreas();
        spawnPlayer();

    }



    @Override
    protected GameUI getScreenUI(final MyTowerDefenseGame context) {
        return new GameUI(context);
    }

    private void resetBodieAndFixtureDefinition(){



    }

    private void spawnPlayer(){
        resetBodieAndFixtureDefinition();

// kör létrehozás mostmár player

        //poziicó

        bodyDef.position.set(map.getStartLocation().x,map.getStartLocation().y +0.5f);// 0.5  a magassága
        bodyDef.fixedRotation = true;
        bodyDef.type= BodyDef.BodyType.DynamicBody;
        player= world.createBody(bodyDef);
        player.setUserData("PLAYER");

        fixtureDef.filter.categoryBits = BIT_PLAYER;
        fixtureDef.filter.maskBits = BIT_GROUND | BIT_BOARD;
        //final PolygonShape

        final PolygonShape pShape =  new PolygonShape();
        pShape.setAsBox(0.5f,0.5f);
        fixtureDef.shape =pShape;
        player.createFixture(fixtureDef);
        pShape.dispose();



    }


    private void spawnCollisionAreas(){
        //későbbiekben a mapmangere class használata

        for (final CollisionArea collisionArea : map.getCollisionAreas()){

            resetBodieAndFixtureDefinition();

            //create room

            //poziicó
            bodyDef.position.set(collisionArea.getX(),collisionArea.getY());
            bodyDef.fixedRotation=true;
            bodyDef.type= BodyDef.BodyType.StaticBody;
            final Body body= world.createBody(bodyDef);
            body.setUserData("GROUND");
            fixtureDef.filter.categoryBits = BIT_GROUND;
            fixtureDef.filter.maskBits = -1 ;
            final ChainShape chainShape = new ChainShape();
            chainShape.createChain(collisionArea.getVertices());
            //összekötjük a kettőt
            fixtureDef.shape =chainShape;
            body.createFixture(fixtureDef);
            chainShape.dispose();


        }

    }




    // dealta time always a time betwen to frames
    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //force, mozgás
        final float speedX;
        final float speedY;

        /*
        if(Gdx.input.isKeyPressed(Input.Keys.A)){

            speedX=-3;
        }else if(Gdx.input.isKeyPressed(Input.Keys.D)) {
            speedX = 3;
        } else {speedX = 0;}

        if(Gdx.input.isKeyPressed(Input.Keys.W)){

            speedY=-3;
        }else if(Gdx.input.isKeyPressed(Input.Keys.S)) {
            speedY = 3;
        } else {speedY = 0;}
*/
        speedX =0;
        if(Gdx.input.isTouched()) {

            //context.setScreen(ScreenType.LOADING); //ez ittmaradt egy demoonstrálésból
            speedY=3;


        } else {speedY=0;}

        bodyBoard.applyLinearImpulse(
                (speedX-bodyBoard.getLinearVelocity().x)*player.getMass(), //Xtengely mentén
                (speedY-bodyBoard.getLinearVelocity().y)*player.getMass(), //Y tengely mentén
                bodyBoard.getWorldCenter().x-2, //melyik poont hat rá
                bodyBoard.getWorldCenter().y, //előző pont Y koordonátája
                true   // ha épp nincs rajta épp erőhatás, felébresztjü
        );

        //erőhatás hozzáadása
       /* player.applyLinearImpulse(
                impluseX //Xtengely mentén
                impulseY //Y tengely mentén
                PointY  //melyik poont hat rá
                PointY //előző pont Y koordonátája
                wakeup  // ha épp nincs rajta épp erőhatás, felébresztjü
                kell a denseti
        );*/


        //PLAYER Mozgatása
        if (directionChange) {
            player.applyLinearImpulse(
                    (xFactor * 3 - player.getLinearVelocity().x * player.getMass()),
                    (yFactor * 3 - player.getLinearVelocity().y * player.getMass()),
                    player.getWorldCenter().x, player.getWorldCenter().y,
                    true
            );
        }


        //   context.setScreen(ScreenType.LOADING); //ez ittmaradt egy demoonstrálésból


        viewport.apply(true);


        mapRenderer.setView(gameCamera);
        //egyik map renderer lehetőség, ekkor mindent rendel pl ami nem object
        //a tile layerek a szok sorrendben megjelennek
        //layerenként is lehetne!
        mapRenderer.render();
        //a második elem: kamera ez lehetne 3d esetén kicsit dönött pl
        box2DDebugRenderer.render(world,viewport.getCamera().combined);

        if (profiler.isEnabled()) {
            //profiler bindigs kiratás
            Gdx.app.debug("RenderInfo", "Bindings" + profiler.getTextureBindings());
            Gdx.app.debug("RenderInfo", "Drawcalls" + profiler.getDrawCalls());
            //reeteleni kell mert...log oután, h a belső érteékit reszeteld
            // 1 Bindig = 1 texture binding
            profiler.reset();
        }


    }
    /*
       // @Override
        public void resize(int width, int height) {

            super.resize(width,height);

        }
    */
    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }



    @Override
    public void dispose() {
        mapRenderer.dispose();
        //itt texture binding
        // GL.profiler!!!!!
    }

    @Override
    public void keyPressed(final InputManager manager,final GameKeys key) {
            switch (key){
                case LEFT:
                    directionChange = true;
                    xFactor =-1;
                    break;


                case RIGHT:
                    directionChange = true;
                    xFactor =1;
                    break;


                case UP:

                    directionChange= true;
                    yFactor =1;
                    break;
                case DOWN:
                    directionChange = true;
                    yFactor=-1;
                    break;


                default:
                    return;




            }


    }

    @Override
    public void keyUp(final InputManager manager,final GameKeys key) {

        switch (key){
            case LEFT:
                directionChange =true;
                xFactor = manager.isKeyPressed(GameKeys.RIGHT) ? 1:0;

                break;

            case RIGHT:
                directionChange =true;
                xFactor = manager.isKeyPressed(GameKeys.LEFT) ? -1:0;


                break;

            case UP:
                directionChange =true;

                yFactor = manager.isKeyPressed(GameKeys.DOWN) ? -1:0;

                break;

            case DOWN:
                directionChange =true;
                yFactor = manager.isKeyPressed(GameKeys.UP) ? 1:0;

                break;

            default:
                break;


        }


    }
}
