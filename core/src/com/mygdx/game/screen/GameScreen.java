package com.mygdx.game.screen;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.mygdx.game.MyTowerDefenseGame;
import com.mygdx.game.PreferenceManager;
import com.mygdx.game.UI.GameUI;
import com.mygdx.game.input.GameKeys;
import com.mygdx.game.input.InputManager;
import com.mygdx.game.map.MapLoader;
import com.mygdx.game.map.MapListener;
import com.mygdx.game.map.MapManager;
import com.mygdx.game.map.MapType;


import static com.mygdx.game.MyTowerDefenseGame.UNIT_SCALE;
import static com.mygdx.game.UI.GameRenderer.TAG;

public class GameScreen extends AbstractScreen<GameUI> implements MapListener
{   //private final gameTD context;
   // private final AssetManager assetManager;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final OrthographicCamera gameCamera;
    //profiler
    //private final GLProfiler profiler;
    private final MapManager mapManager;

    private  final PreferenceManager preferenceManager;
    int i ;
    float health;




    private  boolean button1;
    private  boolean button2;
    private  boolean button3;
    private int x;
    private int y;

    private  int enemySpawner=0;
    private int enemyCounter =0;

    public GameScreen(final MyTowerDefenseGame context){
        //this.context = context;

        super(context);
        Gdx.app.debug("GAMESCRRE","MOST JÖTT LÉTRE");

        //meg kell mondani h mi

        mapRenderer = new OrthogonalTiledMapRenderer(null,UNIT_SCALE, context.getSpriteBatch());//létezik
        this.gameCamera = context.getGameCamera(); //létezik

        //profiler
       // profiler= new GLProfiler(Gdx.graphics); //nemtommi ez csk a debugra
      //  profiler.enable(); //semtudom


        preferenceManager = context.getPreferenceManager(); //load save


       // context.getMapManager().setEcsEngine(context.getEcsEngine());
        mapManager =context.getMapManager();
        mapManager.addMapListener(this);

       // initMap();



     i=1;
     health =1;

    }


    @Override
    protected GameUI getScreenUI(final MyTowerDefenseGame context) {
        return new GameUI(context);
    }



    // dealta time always a time betwen to frames
    @Override
    public void render(float delta) {



                // context.getRenderingInfo().setPosition(10,context.getScreenViewport().getScreenHeight()-context.renderingInfo.getHeight());

        enemySpawner++;
        if (context.gameStarter & enemyCounter <3) {
            //50 enemy hozza a 60at
            //70nél ius  hozz a 60at
            //80 már ingadozik 52-60 között
            //90nél 40-50 között
            //100enemy 30-35 40 fps
            //200enemy 11-7-8 fps
            if (enemySpawner > 100) {
                context.getEcsEngine().createEnemy(context.getMapManager().getCurrentMap().getEnemyStartLocation(), 1.2f, 1.2f,0.6f,0.6f, context.getMapManager().getCurrentMap().getEnemyPath());
                enemyCounter++;
                enemySpawner=0;
            }
        }



//IDE akartama touch screent
    /*    Gdx.app.debug("RenderInfo", "Bindings" + profiler.getTextureBindings());
        Gdx.app.debug("RenderInfo", "Drawcalls" + profiler.getDrawCalls());
        //reeteleni kell mert...log oután, h a belső érteékit reszeteld
        // 1 Bindig = 1 texture binding


        Gdx.app.debug("NATIVE HEAP", "HEAP "+Gdx.app.getNativeHeap()/1024);
        Gdx.app.debug("RenderInfo", ""+Gdx.graphics.getFramesPerSecond()+" FPS");
        */

     //   Gdx.app.debug("JAVA HEAP", "HEAP "+Gdx.app.getJavaHeap()/1024);



//ide átpakolni a screen touch dolgokat.



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

        /*

        //TODO remove mapChange TEST stuff
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)){
            mapManager.setMap(MapType.MAP_1);


        }else if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)){
            mapManager.setMap(MapType.MAP_2);

         }else if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)){
            //preferenceManager.saveGameState();
            //a creat playert külön elmenti
        }

        // Toucing screen


    */




}



    /*
       // @Override
        public void resize(int width, int height) {

            super.resize(width,height);

        }
    */


    public boolean isButton1() {
        return button1;
    }

    public boolean isButton3() {
        return button3;
    }

    public boolean isButton2() {
        return button2;
    }


    public void setButton1 ( boolean bool){

        button1 = bool ;
    }

    public void setButton2 ( boolean bool){

        button2 = bool ;
    }

    public void setButton3 ( boolean bool){

        button3 = bool ;
    }

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
    public void mapChanged(MapLoader map) {

    }

    @Override
    public void hide(){

       super.hide();


    }

    public  void show(){

        super.show();
        context.mapShown = true;
    }


}
