package com.mygdx.game.UI;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.MyTowerDefenseGame;
import com.mygdx.game.ecs.ECSEngine;
import com.mygdx.game.ecs.component.AnimationComponent;
import com.mygdx.game.ecs.component.B2DComponent;
import com.mygdx.game.ecs.component.GameObjectComponent;
import com.mygdx.game.ecs.component.HealthBarComponent;
import com.mygdx.game.map.MapLoader;
import com.mygdx.game.map.MapListener;

import java.util.EnumMap;

import box2dLight.RayHandler;

import static com.mygdx.game.MyTowerDefenseGame.UNIT_SCALE;

public class GameRenderer implements Disposable, MapListener {
    public static final String TAG = GameRenderer.class.getSimpleName();

    private final OrthographicCamera gameCamera;
    private final FitViewport viewport;
    private final SpriteBatch spriteBatch;
    private final AssetManager assetManager;

    private final EnumMap<AnimationType, Animation<Sprite>>  animationCache;

    private final OrthogonalTiledMapRenderer mapRenderer;
    private final GLProfiler profiler;
    private final Box2DDebugRenderer box2DDebugRenderer;
    private final World world;
    //animáciokhoz
    private  ImmutableArray<Entity> animatedEntities;
    private  ImmutableArray<Entity> gameObjectEntities;
    private ImmutableArray<Entity> healtBarEntities;


    private final Array<TiledMapTileLayer> tiledMapLayers;
    private final ObjectMap<String, TextureRegion[][]> regionCache;
    public  IntMap<Animation<Sprite>> mapAnimations;
    protected final MyTowerDefenseGame context;
    //világítás
    private final RayHandler rayHandler;

    //proba Dummy sprite
 //   private Sprite dummySprite;
    private Sprite keyframeSprite2DArray[] ;
    private Sprite keyframeSprite ;



    public GameRenderer(final MyTowerDefenseGame context){
        assetManager = context.getAssetManager();
        viewport = context.getScreenViewport();
        gameCamera = context.getGameCamera();
        this.context = context;
        keyframeSprite = new Sprite();

        //  itt 0, gamescreen!  Gdx.app.debug(TAG,"Screen Height"+ viewport.getScreenHeight());
     //   Gdx.app.debug(TAG,"Screen Width"+ viewport.getScreenWidth());
        gameCamera.position.set(16,9,0); // ez igy 0, 0
        //gameCamera.update();

        spriteBatch = context.getSpriteBatch();

        animationCache = new EnumMap<AnimationType, Animation<Sprite>>(AnimationType.class);
        regionCache =new ObjectMap<String,TextureRegion[][]>();
        //renderere elérje az animációs componetnet => ezeket regosztrálja


        mapRenderer = new OrthogonalTiledMapRenderer(null,UNIT_SCALE,spriteBatch );
        context.getMapManager().addMapListener(this);
        tiledMapLayers= new Array<TiledMapTileLayer>();

        profiler = new GLProfiler(Gdx.graphics);
       profiler.enable();

        if(profiler.isEnabled()){
            box2DDebugRenderer = new Box2DDebugRenderer();
            world = context.getWorld();


        }else{
            box2DDebugRenderer = null;
            world = null;


        }
        rayHandler= context.getRayHandler();



    }


    public void initGameEntities(MyTowerDefenseGame context){

        gameObjectEntities = context.getEcsEngine().getEntitiesFor(Family.all(GameObjectComponent.class, B2DComponent.class,  AnimationComponent.class).get());
        animatedEntities = context.getEcsEngine().getEntitiesFor(Family.all(AnimationComponent.class, B2DComponent.class).exclude(GameObjectComponent.class).get());
        healtBarEntities= context.getEcsEngine().getEntitiesFor(Family.all(HealthBarComponent.class).get());
                //TODO kirajzol.


    }

    public void render(final float alpha){
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);



        viewport.apply(false);
        mapRenderer.setView(gameCamera);
        //statikus nézés



        //TODO : alábbi sor
        //    texture binding monitor 11es vid vége


        spriteBatch.begin(); //ami a begin és end köztt van azokata spritokat rajzolja
        if (context.mapShown & mapRenderer.getMap() != null ) {
            AnimatedTiledMapTile.updateAnimationBaseTime(); //a tiled map animációnak updetelése
            ///AMI NEM OBJECT!!
            //egyik map renderer lehetőség, ekkor mindent rendel pl
            //a tile layerek a szok sorrendben megjelennek
            //layerenként is lehetne!
            //mapRenderer.render();

            for (final TiledMapTileLayer layer :tiledMapLayers){
                mapRenderer.renderTileLayer(layer);

            }

        }
        else{
        //    Gdx.app.debug("RenderInfo", "NINCSMAP");



        }
        //számít a sorrend h elsőnek a gameObj, aztán azn anient, mert ugyanazt a texture használják
        for (final Entity entity:gameObjectEntities){
            renderGameObject(entity,alpha);

        }

        //ANimatinon player
        for (final Entity entity:animatedEntities){
            renderEntity(entity,alpha);

        }

        for (final Entity entity:healtBarEntities){


            setProgressHealtBar(entity);
        }


        spriteBatch.end();

        //világitás
        rayHandler.setCombinedMatrix(gameCamera);
        rayHandler.updateAndRender();

        /*
        if (profiler.isEnabled()) {
            //profiler bindigs kiratás

           Gdx.app.debug("RenderInfo", "Bindings" + profiler.getTextureBindings());
            Gdx.app.debug("RenderInfo", "Drawcalls" + profiler.getDrawCalls());
            //reeteleni kell mert...log oután, h a belső érteékit reszeteld
            // 1 Bindig = 1 texture binding


            Gdx.app.debug("JAVA HEAP", "HEAP "+Gdx.app.getJavaHeap()/1024);
            Gdx.app.debug("NATIVE HEAP", "HEAP "+Gdx.app.getNativeHeap()/1024);
            Gdx.app.debug("RenderInfo", ""+Gdx.graphics.getFramesPerSecond()+" FPS");


            //a második elem: kamera ez lehetne 3d esetén kicsit dönött pl


        }


            */

        if (profiler.isEnabled()) {
            context.heapAndFPSinfo(Gdx.graphics.getFramesPerSecond(), profiler.getDrawCalls(), profiler.getTextureBindings(), Gdx.app.getJavaHeap() / 1024, Gdx.app.getNativeHeap() / 1024);
            profiler.reset();
            box2DDebugRenderer.render(world,gameCamera.combined);


        }

    }

    private void setProgressHealtBar(Entity entity){

        final HealthBarComponent healthBarComponent =ECSEngine.healthBarCmpMapper.get(entity);

        healthBarComponent.healtBar.setValue(healthBarComponent.healthBarPercentege);



    }

    private void renderGameObject(Entity entity, float alpha) {

        final B2DComponent b2DComponent =ECSEngine.b2dCmpMapper.get(entity);
        final  AnimationComponent aniComponent = ECSEngine.aniCmpMapper.get(entity);
        final  GameObjectComponent gameObjectComponent = ECSEngine.gameObjCmpMapper.get(entity);

        if (gameObjectComponent.animationIndex != -1){
            //TODO EZEKET IS RENDBE RAKNI
            final  Animation<Sprite> animation = mapAnimations.get(gameObjectComponent.animationIndex);
            final Sprite frame = animation.getKeyFrame(aniComponent.aniTime);
            //set bounds before origin and rotaion althought doc sasy its slightly les eff
            //But otherwise there is a strance flickering for the first fww sec
            //
            frame.setBounds(b2DComponent.renderPosition.x,b2DComponent.renderPosition.y,aniComponent.width,aniComponent.height);
            frame.setOriginCenter();
            frame.setRotation(b2DComponent.body.getAngle()* MathUtils.radDeg);
            frame.draw(spriteBatch);


        }



    }





    private void renderEntity(Entity entity, float alpha) {
        //mozgása animálása a Playernek!!

        final B2DComponent b2DComponent =ECSEngine.b2dCmpMapper.get(entity);
        final  AnimationComponent aniComponent = ECSEngine.aniCmpMapper.get(entity);
//hova menjenű
//gameCamera.unproject(new Vector3(Gdx.input.getX(),Gdx.input.getY(),0)).x;
        //gameCamera.unproject(new Vector3(Gdx.input.getX(),Gdx.input.getY(),0)).y;



        if (aniComponent.aniType != null){

            final  Animation<Sprite> animation = getAnimation(aniComponent.aniType);
            final Sprite frame = animation.getKeyFrame(aniComponent.aniTime);

           b2DComponent.renderPosition.lerp(b2DComponent.body.getPosition(),alpha);

           frame.setBounds(b2DComponent.renderPosition.x- b2DComponent.width*0.5f,b2DComponent.renderPosition.y - b2DComponent.height*0.5f,aniComponent.width,aniComponent.height);
           frame.draw(spriteBatch);


        }else{

        //    error

        }

        //b2DComponent.renderPosition.lerp(b2DComponent.body.getPosition(),alpha);


        //animation widht height component
       // dummySprite.setBounds(b2DComponent.renderPosition.x- b2DComponent.width*0.5f,b2DComponent.renderPosition.y - b2DComponent.height*0.5f,b2DComponent.width,b2DComponent.height);
       // dummySprite.draw(spriteBatch);



    }

    private Animation<Sprite> getAnimation(AnimationType aniType) {
        Animation<Sprite> animation = animationCache.get(aniType);
        if (animation ==null){
            Gdx.app.debug(TAG,"Creating new animation of type"+aniType);
           TextureRegion[][] textureRegions = regionCache.get(aniType.getAtlasKey());
           if (textureRegions ==null){
               Gdx.app.debug(TAG,"Creating texture region for"+aniType.getAtlasKey());

               //ez az egész négyzet!!
               final TextureAtlas.AtlasRegion atlasRegion =assetManager.get(aniType.getAtlasPath(),TextureAtlas.class).findRegion(aniType.getAtlasKey());
               //daraboljuk:
               textureRegions = atlasRegion.split(32,32);
               regionCache.put(aniType.getAtlasKey(),textureRegions);


           }
            //creat animation
            animation = new Animation<Sprite>(aniType.getFrameTime(),getKeyFrames(textureRegions[aniType.getRowIndex()]));
            animation.setPlayMode(Animation.PlayMode.LOOP);
            animationCache.put(aniType,animation);
        }


    return  animation;
    }

    //2D-s tömböt 1_D spritetá
    private Sprite [] getKeyFrames(TextureRegion[] textureRegion) {
      //TODO ezt átirni h ne kreáljon mindig uj spritot FONTOS
      //itt minidig uj spr
        getKeyFremesSPriteReset();
      //  final  Sprite[] keyFrames = new Sprite[textureRegion.length];
       // keyframeSprite2DArray
        final  Sprite[] keyFrames = new Sprite[textureRegion.length];
             int i =0;
            for (final TextureRegion region:textureRegion){
               final Sprite sprite = new Sprite(region);
              //  keyframeSprite.setRegion(region);
               // keyframeSprite.setColor(1, 1, 1, 1);
               // keyframeSprite.setSize(region.getRegionWidth(), region.getRegionHeight());
               // keyframeSprite.setOrigin(keyframeSprite.getWidth() / 2, keyframeSprite.getHeight() / 2);


                sprite.setOriginCenter();
                keyFrames[i++]=sprite;


            }

            return keyFrames;


    }


    public void getKeyFremesSPriteReset(){
        keyframeSprite2DArray = null;


    }

    @Override
    public void dispose() {

        if (box2DDebugRenderer !=null){

            box2DDebugRenderer.dispose();
        }
        mapRenderer.dispose();
    }

    @Override
    public void mapChanged(MapLoader map) {
        mapRenderer.setMap(map.getTiledMap());

        map.getTiledMap().getLayers().getByType(TiledMapTileLayer.class,tiledMapLayers);
        mapAnimations =map.getMapAnimations();
    //     if (dummySprite == null){

    //        dummySprite =  assetManager.get("charracters_effects/effect.atlas", TextureAtlas.class ).createSprite("exp2");
            //width , height /2 lesz a rotation center
    //        dummySprite.setOriginCenter();
      //  }


    }
}
