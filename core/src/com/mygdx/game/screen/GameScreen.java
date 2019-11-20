package com.mygdx.game.screen;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.game.MyTowerDefenseGame;
import com.mygdx.game.PreferenceManager;
import com.mygdx.game.UI.GameUI;
import com.mygdx.game.input.GameKeys;
import com.mygdx.game.input.InputManager;
import com.mygdx.game.map.CollisionArea;
import com.mygdx.game.map.MapCol;
import com.mygdx.game.map.MapListener;
import com.mygdx.game.map.MapManager;
import com.mygdx.game.map.MapType;


import static com.mygdx.game.MyTowerDefenseGame.UNIT_SCALE;

public class GameScreen extends AbstractScreen<GameUI> implements MapListener
{   //private final gameTD context;
   // private final AssetManager assetManager;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final OrthographicCamera gameCamera;
    //profiler
    private final GLProfiler profiler;
    private final MapManager mapManager;

    private  final PreferenceManager preferenceManager;

    public GameScreen(final MyTowerDefenseGame context){
        //this.context = context;

        super(context);
        //meg kell mondani h mi
        mapRenderer = new OrthogonalTiledMapRenderer(null,UNIT_SCALE, context.getSpriteBatch());
        this.gameCamera = context.getGameCamera();

        //profiler
        profiler= new GLProfiler(Gdx.graphics);
        profiler.enable();


        preferenceManager = context.getPreferenceManager();
        //player
        //létrhozzuk a leeső tárgyakat.

        /* TÖRÖLHETŐ, CSAK PÉLDÁNAK MARAD BENT
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

*/


        mapManager =context.getMapManager();
        mapManager.addMapListener(this);
        mapManager.setMap(MapType.MAP_1);
        //width az one world unit
        context.getEcsEngine().createPlayer(mapManager.getCurrentMap().getStartLocation(),1,1);


    }



    @Override
    protected GameUI getScreenUI(final MyTowerDefenseGame context) {
        return new GameUI(context);
    }







    // dealta time always a time betwen to frames
    @Override
    public void render(float delta) {


        //force, mozgás

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


        /*

        ezt mind ki lehet szedni csak a péda miatt maradt bent

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

        */

        //erőhatás hozzáadása
       /* player.applyLinearImpulse(
                impluseX //Xtengely mentén
                impulseY //Y tengely mentén
                PointY  //melyik poont hat rá
                PointY //előző pont Y koordonátája
                wakeup  // ha épp nincs rajta épp erőhatás, felébresztjü
                kell a denseti
        );*/





        //   context.setScreen(ScreenType.LOADING); //ez ittmaradt egy demoonstrálésból


        //true: fix camera
        //false: mozgathato



        //TODO remove mapChange TEST stuff
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)){
            mapManager.setMap(MapType.MAP_1);


        }else if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)){
            mapManager.setMap(MapType.MAP_2);

         }else if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)){
            //preferenceManager.saveGameState();
            //a creat playert külön elmenti
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

    }


    @Override
    public void keyPressed(final InputManager manager,final GameKeys key) {

    }


    @Override
    public void keyUp(final InputManager manager,final GameKeys key) {
    }


    @Override
    public void mapChanged(MapCol map) {

    }
}
