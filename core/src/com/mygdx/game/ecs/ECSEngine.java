package com.mygdx.game.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.utils.Ray;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.MyTowerDefenseGame;
import com.mygdx.game.UI.AnimationType;
import com.mygdx.game.ecs.component.AnimationComponent;
import com.mygdx.game.ecs.component.B2DComponent;
import com.mygdx.game.ecs.component.EnemyComponent;
import com.mygdx.game.ecs.component.GameButtonComponent;
import com.mygdx.game.ecs.component.GameObjectComponent;
import com.mygdx.game.ecs.component.PlayerComponent;
import com.mygdx.game.ecs.component.SelectAbleComponent;
import com.mygdx.game.map.GameObject;
import com.mygdx.game.system.AnimationSystem;
import com.mygdx.game.system.ButtonSelectedSystem;
import com.mygdx.game.system.EnemyAnimationSystem;
import com.mygdx.game.system.EnemyMovmentSystem;
import com.mygdx.game.system.LightSystem;
import com.mygdx.game.system.PlayerAnimationSystem;
import com.mygdx.game.system.PlayerCameraSystem;
import com.mygdx.game.system.PlayerCollisionSystem;
import com.mygdx.game.system.PlayerMovmentSystem;
import com.mygdx.game.system.SelectAbleEntitySystem;

import javax.management.relation.RoleList;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import jdk.nashorn.internal.runtime.PropertyAccess;

import static com.mygdx.game.MyTowerDefenseGame.BIT_BOARD;
import static com.mygdx.game.MyTowerDefenseGame.BIT_BUTTON;
import static com.mygdx.game.MyTowerDefenseGame.BIT_GAME_OBJECT;
import static com.mygdx.game.MyTowerDefenseGame.BIT_GROUND;
import static com.mygdx.game.MyTowerDefenseGame.BIT_PLAYER;
import static com.mygdx.game.MyTowerDefenseGame.UNIT_SCALE;
import static com.mygdx.game.UI.GameRenderer.TAG;
import static sun.audio.AudioPlayer.player;

public class ECSEngine extends PooledEngine {

    public static final ComponentMapper<PlayerComponent> playerCmpMapper =ComponentMapper.getFor(PlayerComponent.class);
    public static final ComponentMapper<B2DComponent> b2dCmpMapper= ComponentMapper.getFor(B2DComponent.class);
    public static final ComponentMapper<AnimationComponent> aniCmpMapper = ComponentMapper.getFor(AnimationComponent.class);
    public static final ComponentMapper<GameObjectComponent> gameObjCmpMapper = ComponentMapper.getFor(GameObjectComponent.class);
    public static final ComponentMapper<EnemyComponent> enemyCmpMapper = ComponentMapper.getFor(EnemyComponent.class);
    public static final ComponentMapper<SelectAbleComponent> selectabCmpMapper = ComponentMapper.getFor(SelectAbleComponent.class);
    public static final  ComponentMapper<GameButtonComponent> gameButtonCmpMapper= ComponentMapper.getFor(GameButtonComponent.class);


    public ImmutableArray<Entity> entites;




    private final World world;
    private final Vector2 localPosition;
    private final Vector2 posBeforeRotation;
    private final Vector2 posAfterRotation;
    private boolean isPressed = true;


    //világitás
    private final RayHandler rayHandler;

    public ECSEngine(final MyTowerDefenseGame context){
        super(100,100,100,100); //alapból 100 entity, 100 compomnents
        world = context.getWorld();
        rayHandler = context.getRayHandler();

        //hozzáadjuk az ECS hez
        //lehet removolni is
        //setProceesing bizonyos Systeemket
        //egyébbként minden dystem updetelődik minden framben az UPDATE methodba a myTowerdefense classban
        this.addSystem(new PlayerMovmentSystem(context));
      // this.addSystem(new PlayerCameraSystem(context));
        this.addSystem(new AnimationSystem(context));
       this.addSystem(new PlayerAnimationSystem(context));
       this.addSystem(new PlayerCollisionSystem(context));
        //lighting
     // this.addSystem(new LightSystem());
      this.addSystem(new EnemyMovmentSystem(context));
        this.addSystem(new EnemyAnimationSystem(context));
       // this.getEntities().get();
        this.addSystem(new SelectAbleEntitySystem(context));
        this.addSystem(new ButtonSelectedSystem(context));

        localPosition = new Vector2();
        posBeforeRotation = new Vector2();
        posAfterRotation = new Vector2();


    }




    public void createEnemy(final Vector2 playerSpawnLocation, final float width, final float height, Array<Vector2> enemyPath){

        final Entity enemy = this.createEntity();

        //componetn kreálás
        //ez ugyanaz
        //player component
        final EnemyComponent enemyComponent = this.createComponent(EnemyComponent.class);
        enemyComponent.speed.set(2,2);
        enemyComponent.pathPointCounter = 0;
        enemyComponent.nextPath = enemyPath;
        enemy.add(enemyComponent);

        //box2d Component

        final B2DComponent b2DComponent = this.createComponent(B2DComponent.class);
        enemy.add(setB2DcomponantBox(b2DComponent,playerSpawnLocation.x,playerSpawnLocation.y,width,height,enemy,BIT_PLAYER, (short)(BIT_GROUND | BIT_GAME_OBJECT)));

        // animation component
        final AnimationComponent animationComponent = this.createComponent(AnimationComponent.class);
        //hozzá kell adni az enginhez ,hogy a rész legyen
        enemy.add(setAnimationComponent(animationComponent,width,height,AnimationType.HERO_MOVE_LEFT));
        this.addEntity(enemy);
    }

    public void createGameButton(final Vector2 gameButtonSpawnLocation, final float width, final float height){


        final Entity button = this.createEntity();

        final B2DComponent b2DComponent = this.createComponent(B2DComponent.class);
        button.add(setB2DcomponantBox(b2DComponent,gameButtonSpawnLocation.x,gameButtonSpawnLocation.y,width,height,button,BIT_BUTTON, BIT_GROUND ));

        // animation component
        final AnimationComponent animationComponent = this.createComponent(AnimationComponent.class);
        button.add(setAnimationComponent(animationComponent,width,height,AnimationType.BUTTON_IS_NOT_SELECTED));

        //selec coomp
        final SelectAbleComponent selectAbleComponent = this.createComponent(SelectAbleComponent.class);
        selectAbleComponent.isSelected = false;

        button.add(selectAbleComponent);


        //button comp
        final GameButtonComponent buttonComponent = this.createComponent(GameButtonComponent.class);
        buttonComponent.isPressed = false;

        button.add(buttonComponent);

        this.addEntity(button);


    }


    public void createPlayer(final Vector2 playerSpawnLocation, final float width, final float height){

        final Entity player = this.createEntity();

        final PlayerComponent playerComponent = this.createComponent(PlayerComponent.class);
        playerComponent.speed.set(3,3);
        player.add(playerComponent);
        //box2d Component

        final B2DComponent b2DComponent = this.createComponent(B2DComponent.class);
        player.add(setB2DcomponantBox(b2DComponent,playerSpawnLocation.x,playerSpawnLocation.y,width,height,player,BIT_PLAYER, (short)(BIT_GROUND | BIT_GAME_OBJECT)));

            // animation component
        final AnimationComponent animationComponent = this.createComponent(AnimationComponent.class);
        player.add(setAnimationComponent(animationComponent,width,height,AnimationType.HERO_MOVE_LEFT));

        final SelectAbleComponent selectAbleComponent = this.createComponent(SelectAbleComponent.class);
        selectAbleComponent.isSelected = false;

        player.add(selectAbleComponent);

            this.addEntity(player);
        }


    public void createGameObject(final GameObject gameObject){

        final Entity gameObjectEntity = this.createEntity();

        final GameObjectComponent gameObjectComponent = this.createComponent(GameObjectComponent.class);
        gameObjectComponent.animationIndex = gameObject.getAnimationIndex();
        gameObjectComponent.type = gameObject.getType();
        gameObjectEntity.add(gameObjectComponent);

        //Animation Component
       final AnimationComponent animationComponent = this.createComponent(AnimationComponent.class);
       animationComponent.aniType = null;
       animationComponent.width = gameObject.getWidth();
        animationComponent.height = gameObject.getHeight();
        gameObjectEntity.add(animationComponent);

        //box2d component
        MyTowerDefenseGame.resetBodieAndFixtureDefinition();

        final float halfW = gameObject.getWidth()*0.5f;
        final float halfH = gameObject.getHeight()*0.5f;
        final float angleRad = -gameObject.getRotDegree()* MathUtils.degreesToRadians;

        final B2DComponent b2DComponent = this.createComponent(B2DComponent.class);

        MyTowerDefenseGame.BODY_DEF.type= BodyDef.BodyType.StaticBody;

        MyTowerDefenseGame.BODY_DEF.position.set(gameObject.getPosition().x+halfW,gameObject.getPosition().y +halfH);// 0.5  a magassága

        MyTowerDefenseGame.BODY_DEF.gravityScale =0;
        b2DComponent.body= world.createBody(MyTowerDefenseGame.BODY_DEF);

        b2DComponent.body.setUserData(gameObjectEntity);
        b2DComponent.width = gameObject.getWidth();
        b2DComponent.height =gameObject.getHeight();
        //save position before rotation - Tild is roating around the bottom left corner insted of the center of Tile
        localPosition.set(-halfW,-halfH);
        posBeforeRotation.set(b2DComponent.body.getWorldPoint(localPosition));
        //roatate body
        b2DComponent.body.setTransform(b2DComponent.body.getPosition(),angleRad);
        //getposition after rot
        posAfterRotation.set(b2DComponent.body.getWorldPoint(localPosition));

        //TODO itt valami más renderpozi van
        b2DComponent.body.setTransform(b2DComponent.body.getPosition().add(posBeforeRotation).sub(posAfterRotation),angleRad);
        b2DComponent.renderPosition.set(b2DComponent.body.getPosition().x-animationComponent.width*0.5f,b2DComponent.body.getPosition().y-b2DComponent.height*0.5f);


        MyTowerDefenseGame.FIXTURE_DEF.filter.categoryBits = BIT_GAME_OBJECT ;
        MyTowerDefenseGame.FIXTURE_DEF.filter.maskBits = BIT_PLAYER;
        //final PolygonShape

        final PolygonShape pShape =  new PolygonShape();
        pShape.setAsBox(halfW,halfH);
        MyTowerDefenseGame.FIXTURE_DEF.shape =pShape;
        b2DComponent.body.createFixture( MyTowerDefenseGame.FIXTURE_DEF);
        pShape.dispose();

        gameObjectEntity.add(b2DComponent);



        this.addEntity(gameObjectEntity);
    }


    public B2DComponent setB2DcomponantBox(B2DComponent b2DComponent,float spawnLoacationX, float spawnLocationY, float width, float height,Entity userData ,short categoryBits, short maskBits ){
        MyTowerDefenseGame.resetBodieAndFixtureDefinition();


        // kör létrehozás mostmár player
        //poziicó

        MyTowerDefenseGame.BODY_DEF.gravityScale =0;
        MyTowerDefenseGame.BODY_DEF.position.set(spawnLoacationX,spawnLocationY +height*0.5f);// 0.5  a magassága
        MyTowerDefenseGame.BODY_DEF.fixedRotation = true;
        MyTowerDefenseGame.BODY_DEF.type= BodyDef.BodyType.DynamicBody;
        b2DComponent.body= world.createBody(MyTowerDefenseGame.BODY_DEF);
        b2DComponent.body.setUserData(userData);
        b2DComponent.width = width;
        b2DComponent.height =height;
        //anim
        b2DComponent.renderPosition.set(b2DComponent.body.getPosition());

        MyTowerDefenseGame.FIXTURE_DEF.filter.categoryBits = categoryBits;
        MyTowerDefenseGame.FIXTURE_DEF.filter.maskBits = maskBits ;
        //final PolygonShape

        final PolygonShape pShape =  new PolygonShape();
        pShape.setAsBox(width*0.5f,height*0.5f);
        MyTowerDefenseGame.FIXTURE_DEF.shape =pShape;
        b2DComponent.body.createFixture( MyTowerDefenseGame.FIXTURE_DEF);
        pShape.dispose();


        //player light
        //típusok
        //pointlight pontfény
        // Directionlight
        // Cone light
        //chain Light
        //mennyi ray, enyhén light
/*
        b2DComponent.lightDistance =6;
        b2DComponent.lightFluctuacionSpeed=4;
        b2DComponent.light = new PointLight(rayHandler, 64,new Color(1,1,1,0.7f),b2DComponent.lightDistance,b2DComponent.body.getPosition().x,b2DComponent.body.getPosition().y);
        b2DComponent.lightFluctuacionDistance =b2DComponent.light.getDistance()*0.16f;


        //a fény kövesse ajátékost
        b2DComponent.light.attachToBody(b2DComponent.body);



*/

        return b2DComponent;
    }

    public AnimationComponent setAnimationComponent(AnimationComponent animationComponent, float width,float height, AnimationType animationTypeDefault){


        animationComponent.aniType = animationTypeDefault;
        //skálázás
        animationComponent.width=32 *UNIT_SCALE*width;
        animationComponent.height=32*UNIT_SCALE*height;


        return animationComponent;
    }

}
