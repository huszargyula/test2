package com.mygdx.game.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.MyTowerDefenseGame;
import com.mygdx.game.UI.AnimationType;
import com.mygdx.game.ecs.component.AnimationComponent;
import com.mygdx.game.ecs.component.B2DComponent;
import com.mygdx.game.ecs.component.GameObjectComponent;
import com.mygdx.game.ecs.component.PlayerComponent;
import com.mygdx.game.map.GameObject;
import com.mygdx.game.system.AnimationSystem;
import com.mygdx.game.system.PlayerAnimationSystem;
import com.mygdx.game.system.PlayerCameraSystem;
import com.mygdx.game.system.PlayerCollisionSystem;
import com.mygdx.game.system.PlayerMovmentSystem;

import javax.management.relation.RoleList;

import jdk.nashorn.internal.runtime.PropertyAccess;

import static com.mygdx.game.MyTowerDefenseGame.BIT_BOARD;
import static com.mygdx.game.MyTowerDefenseGame.BIT_GAME_OBJECT;
import static com.mygdx.game.MyTowerDefenseGame.BIT_GROUND;
import static com.mygdx.game.MyTowerDefenseGame.BIT_PLAYER;
import static com.mygdx.game.MyTowerDefenseGame.UNIT_SCALE;

public class ECSEngine extends PooledEngine {

    public static final ComponentMapper<PlayerComponent> playerCmpMapper =ComponentMapper.getFor(PlayerComponent.class);
    public static final ComponentMapper<B2DComponent> b2dCmpMapper= ComponentMapper.getFor(B2DComponent.class);
    public static final ComponentMapper<AnimationComponent> aniCmpMapper = ComponentMapper.getFor(AnimationComponent.class);
    public static final ComponentMapper<GameObjectComponent> gameObjCmpMapper = ComponentMapper.getFor(GameObjectComponent.class);



    private final World world;
    public Vector2 localPosition;
    public Vector2 posBeforeRotation;
    public Vector2 posAfterRotation;

    public ECSEngine(final MyTowerDefenseGame context){
        super(); //alapból 100 entity, 100 compomnents
        world = context.getWorld();


        //hozzáadjuk az ECS hez
        //lehet removolni is
        //setProceesing bizonyos Systeemket
        //egyébbként minden dystem updetelődik minden framben az UPDATE methodba a myTowerdefense classban
        this.addSystem(new PlayerMovmentSystem(context));
        this.addSystem(new PlayerCameraSystem(context));
        this.addSystem(new AnimationSystem(context));
        this.addSystem(new PlayerAnimationSystem(context));
        this.addSystem(new PlayerCollisionSystem(context));

        localPosition = new Vector2();
        posBeforeRotation = new Vector2();
        posAfterRotation = new Vector2();


    }




    public void createPlayer(final Vector2 playerSpawnLocation, final float width, final float height){

        final Entity player = this.createEntity();

        //componetn kreálás
       // final PlayerComponent playerComponent = this.createComponent(PlayerComponent.class);
        //entity.add(playerComponent);
        //ez ugyanaz
        //player component
        final PlayerComponent playerComponent = this.createComponent(PlayerComponent.class);
        playerComponent.speed.set(3,3);
        player.add(playerComponent);
        //box2d Component

        MyTowerDefenseGame.resetBodieAndFixtureDefinition();
        final B2DComponent b2DComponent = this.createComponent(B2DComponent.class);


        // kör létrehozás mostmár player
        //poziicó
             MyTowerDefenseGame.BODY_DEF.gravityScale =0;
             MyTowerDefenseGame.BODY_DEF.position.set(playerSpawnLocation.x,playerSpawnLocation.y +height*0.5f);// 0.5  a magassága
             MyTowerDefenseGame.BODY_DEF.fixedRotation = true;
             MyTowerDefenseGame.BODY_DEF.type= BodyDef.BodyType.DynamicBody;
            b2DComponent.body= world.createBody(MyTowerDefenseGame.BODY_DEF);
            b2DComponent.body.setUserData(player);
            b2DComponent.width = width;
            b2DComponent.height =height;
            //anim
            b2DComponent.renderPosition.set(b2DComponent.body.getPosition());

            MyTowerDefenseGame.FIXTURE_DEF.filter.categoryBits = BIT_PLAYER;
            MyTowerDefenseGame.FIXTURE_DEF.filter.maskBits = BIT_GROUND | BIT_GAME_OBJECT;
            //final PolygonShape

            final PolygonShape pShape =  new PolygonShape();
            pShape.setAsBox(width*0.5f,height*0.5f);
             MyTowerDefenseGame.FIXTURE_DEF.shape =pShape;
            b2DComponent.body.createFixture( MyTowerDefenseGame.FIXTURE_DEF);
            pShape.dispose();

            player.add(b2DComponent);

            // animation component
            final AnimationComponent animationComponent = this.createComponent(AnimationComponent.class);
            animationComponent.aniType = AnimationType.HERO_MOVE_LEFT;
            //skálázás
            animationComponent.width=32 *UNIT_SCALE*0.75f;
            animationComponent.height=32*UNIT_SCALE*0.75f;


            //hozzá kell adni az enginhez ,hogy a rész legyen
           player.add(animationComponent);
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
}
