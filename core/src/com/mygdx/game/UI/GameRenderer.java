package com.mygdx.game.UI;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
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
import com.badlogic.gdx.physics.box2d.FixtureDef;
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
import com.mygdx.game.map.GameObject;
import com.mygdx.game.map.MapCol;
import com.mygdx.game.map.MapListener;
import com.mygdx.game.map.MapManager;

import java.util.ArrayList;
import java.util.EnumMap;

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
    private final ImmutableArray<Entity> animatedEntities;
    private final ImmutableArray<Entity> gameObjectEntities;


    private final Array<TiledMapTileLayer> tiledMapLayers;
    private final ObjectMap<String, TextureRegion[][]> regionCache;
    public  IntMap<Animation<Sprite>> mapAnimations;


    //proba Dummy sprite
 //   private Sprite dummySprite;



    public GameRenderer(final MyTowerDefenseGame context){
        assetManager = context.getAssetManager();
        viewport = context.getScreenViewport();
        gameCamera = context.getGameCamera();
        spriteBatch = context.getSpriteBatch();

        animationCache = new EnumMap<AnimationType, Animation<Sprite>>(AnimationType.class);
        regionCache =new ObjectMap<String,TextureRegion[][]>();
        //renderere elérje az animációs componetnet => ezeket regosztrálja

        gameObjectEntities = context.getEcsEngine().getEntitiesFor(Family.all(GameObjectComponent.class, B2DComponent.class,  AnimationComponent.class).get());
        animatedEntities = context.getEcsEngine().getEntitiesFor(Family.all(AnimationComponent.class, B2DComponent.class).exclude(GameObjectComponent.class).get());


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


    }

    public void render(final float alpha){
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        viewport.apply(false);

        mapRenderer.setView(gameCamera);

        spriteBatch.begin(); //ami a begin és end köztt van azokata spritokat rajzolja
        if (mapRenderer.getMap() != null) {
            AnimatedTiledMapTile.updateAnimationBaseTime();

            //egyik map renderer lehetőség, ekkor mindent rendel pl ami nem object
            //a tile layerek a szok sorrendben megjelennek
            //layerenként is lehetne!
            //mapRenderer.render();

            for (final TiledMapTileLayer layer :tiledMapLayers){
                mapRenderer.renderTileLayer(layer);

            }

        }
        else{
            Gdx.app.debug("RenderInfo", "NINCSMAP");



        }
        //számít a sorrend h elsőnek a gameObj, aztán azn anient, mert ugyanazt a texture használják
        for (final Entity entity:gameObjectEntities){
            renderGameObject(entity,alpha);

        }

        //ANimatinon
        for (final Entity entity:animatedEntities){
            renderEntity(entity,alpha);

        }
        spriteBatch.end();
        if (profiler.isEnabled()) {
            //profiler bindigs kiratás
            Gdx.app.debug("RenderInfo", "Bindings" + profiler.getTextureBindings());
            Gdx.app.debug("RenderInfo", "Drawcalls" + profiler.getDrawCalls());
            //reeteleni kell mert...log oután, h a belső érteékit reszeteld
            // 1 Bindig = 1 texture binding
            profiler.reset();
            //a második elem: kamera ez lehetne 3d esetén kicsit dönött pl
            box2DDebugRenderer.render(world,gameCamera.combined);

        }


    }

    private void renderGameObject(Entity entity, float alpha) {

        final B2DComponent b2DComponent =ECSEngine.b2dCmpMapper.get(entity);
        final  AnimationComponent aniComponent = ECSEngine.aniCmpMapper.get(entity);
        final  GameObjectComponent gameObjectComponent = ECSEngine.gameObjcmpMapper.get(entity);

        if (gameObjectComponent.animationIndex != -1){

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

        final B2DComponent b2DComponent =ECSEngine.b2dCmpMapper.get(entity);
        final  AnimationComponent aniComponent = ECSEngine.aniCmpMapper.get(entity);

        if (aniComponent.aniType != null){

            final  Animation<Sprite> animation = getAnimation(aniComponent.aniType);
            final Sprite frame = animation.getKeyFrame(aniComponent.aniTime);

            b2DComponent.renderPosition.lerp(b2DComponent.body.getPosition(),alpha);
            frame.setBounds(b2DComponent.renderPosition.x- b2DComponent.width*0.5f,b2DComponent.renderPosition.y - b2DComponent.height*0.5f,aniComponent.width,aniComponent.height);
            frame.draw(spriteBatch);


        }else{

        //    error

        }

        b2DComponent.renderPosition.lerp(b2DComponent.body.getPosition(),alpha);
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
        final  Sprite[] keyFrames = new Sprite[textureRegion.length];


             int i =0;
            for (final TextureRegion region:textureRegion){
                final Sprite sprite = new Sprite(region);
                sprite.setOriginCenter();
                keyFrames[i++]=sprite;


            }

            return keyFrames;


    }


    @Override
    public void dispose() {

        if (box2DDebugRenderer !=null){

            box2DDebugRenderer.dispose();
        }
        mapRenderer.dispose();
    }

    @Override
    public void mapChanged(MapCol map) {
        mapRenderer.setMap(map.getTiledMap());

        map.getTiledMap().getLayers().getByType(TiledMapTileLayer.class,tiledMapLayers);
        mapAnimations =map.getMapAnimations();
  //      if (dummySprite == null){

    //        dummySprite =  assetManager.get("charracters_effects/effect.atlas", TextureAtlas.class ).createSprite("exp2");
            //width , height /2 lesz a rotation center
    //        dummySprite.setOriginCenter();
      //  }


    }
}
