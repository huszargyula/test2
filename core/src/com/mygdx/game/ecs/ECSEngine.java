package com.mygdx.game.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.MyTowerDefenseGame;
import com.mygdx.game.UI.AnimationType;
import com.mygdx.game.ecs.component.AnimationComponent;
import com.mygdx.game.ecs.component.B2DComponent;
import com.mygdx.game.ecs.component.EnemyComponent;
import com.mygdx.game.ecs.component.GameButtonComponent;
import com.mygdx.game.ecs.component.GameObjectComponent;
import com.mygdx.game.ecs.component.HealthBarComponent;
import com.mygdx.game.ecs.component.PlayerComponent;
import com.mygdx.game.ecs.component.PlayerIconComponent;
import com.mygdx.game.ecs.component.TankAbleComponent;
import com.mygdx.game.ecs.component.TankComponent;
import com.mygdx.game.map.GameObject;
import com.mygdx.game.system.AnimationSystem;
import com.mygdx.game.system.EnemyFightSystem;
import com.mygdx.game.system.PlayerFightSystem;
import com.mygdx.game.system.RemoveEntitySystem;
import com.mygdx.game.system.SelectPlayerIconOrPlayerBodySystem;
import com.mygdx.game.system.SelectedButtonAnimationSystem;
import com.mygdx.game.system.EnemyAnimationSystem;
import com.mygdx.game.system.EnemyMovmentSystem;
import com.mygdx.game.system.PlayerAnimationSystem;
import com.mygdx.game.system.PlayerCollisionSystem;
import com.mygdx.game.system.PlayerMovmentSystem;

import box2dLight.RayHandler;

import static com.mygdx.game.MyTowerDefenseGame.BIT_BUTTON;
import static com.mygdx.game.MyTowerDefenseGame.BIT_ENEMY;
import static com.mygdx.game.MyTowerDefenseGame.BIT_GAME_OBJECT;
import static com.mygdx.game.MyTowerDefenseGame.BIT_GROUND;
import static com.mygdx.game.MyTowerDefenseGame.BIT_PLAYER;
import static com.mygdx.game.MyTowerDefenseGame.UNIT_SCALE;

public class ECSEngine extends PooledEngine {

    public static final ComponentMapper<PlayerComponent> playerCmpMapper =ComponentMapper.getFor(PlayerComponent.class);
    public static final ComponentMapper<B2DComponent> b2dCmpMapper= ComponentMapper.getFor(B2DComponent.class);
    public static final ComponentMapper<AnimationComponent> aniCmpMapper = ComponentMapper.getFor(AnimationComponent.class);
    public static final ComponentMapper<GameObjectComponent> gameObjCmpMapper = ComponentMapper.getFor(GameObjectComponent.class);
    public static final ComponentMapper<EnemyComponent> enemyCmpMapper = ComponentMapper.getFor(EnemyComponent.class);
    //public static final ComponentMapper<SelectAbleComponent> selectabCmpMapper = ComponentMapper.getFor(SelectAbleComponent.class);
    public static final  ComponentMapper<GameButtonComponent> gameButtonCmpMapper= ComponentMapper.getFor(GameButtonComponent.class);
    public static final ComponentMapper<PlayerIconComponent> playerIconCmpMapper = ComponentMapper.getFor(PlayerIconComponent.class);

    public static final ComponentMapper<HealthBarComponent> healthBarCmpMapper = ComponentMapper.getFor(HealthBarComponent.class);
    public static final ComponentMapper<TankAbleComponent> tankAbleCmpMapper = ComponentMapper.getFor(TankAbleComponent.class);
    public static final ComponentMapper<TankComponent> tankCmpMapper = ComponentMapper.getFor(TankComponent.class);


    //public ImmutableArray<Entity> entites;
    private int playerCounter;



    private final World world;
    private final Vector2 localPosition;
    private final Vector2 posBeforeRotation;
    private final Vector2 posAfterRotation;
    private boolean isPressed = true;
    MyTowerDefenseGame context;
    private ProgressBar healthBar;
    //világitás
    private final RayHandler rayHandler;

    public ECSEngine(final MyTowerDefenseGame context){
       super();
       // super(100,250,100,5000); //alapból 100 entity, 100 compomnents

        world = context.getWorld();
        rayHandler = context.getRayHandler();
        playerCounter=0;
         this.context = context;

        this.addSystem(new AnimationSystem(context));// Ez csak a delta timot növeli
         //TODO  fight vagy moving van elöbb az alpaján irják felül egymást
        this.addSystem(new PlayerFightSystem(context));
        this.addSystem(new EnemyFightSystem(context));
        this.addSystem(new PlayerMovmentSystem(context));
      // this.addSystem(new PlayerCameraSystem(context));
       this.addSystem(new PlayerAnimationSystem(context));

        //lighting
     // this.addSystem(new LightSystem());
      this.addSystem(new EnemyMovmentSystem(context));
        this.addSystem(new EnemyAnimationSystem(context));
       // this.getEntities().get();
    //    this.addSystem(new SelectAbleEntitySystem(context));
        this.addSystem(new SelectedButtonAnimationSystem(context));

        this.addSystem(new SelectPlayerIconOrPlayerBodySystem(context));
        this.addSystem(new PlayerCollisionSystem(context));


        //TODO kell egy Destroy ENTITI SYSTEM ami UTOLJÁRA FUT le!
        //igy utoljára törli a entititket.
        //jelenleg ez ez

        this.addSystem(new RemoveEntitySystem(context));

        localPosition = new Vector2();
        posBeforeRotation = new Vector2();
        posAfterRotation = new Vector2();


    }




    public void createEnemy(final Vector2 playerSpawnLocation, final float bodyWidth, final float bodyHeight, float animWidth, float animHeight, Array<Vector2> enemyPath){

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
        enemy.add(setB2DcomponantBox(b2DComponent,playerSpawnLocation.x,playerSpawnLocation.y,bodyWidth,bodyHeight,enemy,BIT_ENEMY, (short)(BIT_GROUND | BIT_GAME_OBJECT|BIT_PLAYER),true));


        // animation component
        final AnimationComponent animationComponent = this.createComponent(AnimationComponent.class);
        //hozzá kell adni az enginhez ,hogy a rész legyen
        enemy.add(setAnimationComponent(animationComponent,animWidth,animHeight,AnimationType.MANO_MAGE_BASIC_MOVE_LEFT));

        final TankAbleComponent tankAbleComponent = this.createComponent(TankAbleComponent.class);
        tankAbleComponent.isTanked=false;
        enemy.add(tankAbleComponent);

        final HealthBarComponent healthBarComponent = this.createComponent(HealthBarComponent.class);


        healthBarComponent.healtBar=  new ProgressBar(0,1,0.01f ,false, context.getSkin() ,"hp");
      //TODO ezt beállítni widht height
      //  healthBarComponent.healtBar.setSize(30,5);
        healthBarComponent.healtBar.setSize(bodyWidth*32,32/5);

        context.getStage().addActor(healthBarComponent.healtBar);
        healthBarComponent.healthBarPercentege =1;
        //MOZGATÁS AZ ENEMY MOVINGSYSTEMBEN
        enemy.add(healthBarComponent);



    this.addEntity(enemy);

    }

    public void createPlayerIconButton(final Vector2 gameButtonSpawnLocation, final float width, final float height, int ID){


        final Entity button = this.createEntity();

        final B2DComponent b2DComponent = this.createComponent(B2DComponent.class);
        button.add(setB2DcomponantBox(b2DComponent,gameButtonSpawnLocation.x,gameButtonSpawnLocation.y,width,height,button,BIT_BUTTON, BIT_GROUND,true ));



        // animation component
        final AnimationComponent animationComponent = this.createComponent(AnimationComponent.class);
        button.add(setAnimationComponent(animationComponent,width,height,AnimationType.BUTTON_IS_NOT_SELECTED));

        //selec coomp
        final PlayerIconComponent playerIconComponent = this.createComponent(PlayerIconComponent.class);
        playerIconComponent.isSelected = false;
        playerIconComponent.playerIconId =ID;
        button.add(playerIconComponent);


        //button comp
        final GameButtonComponent buttonComponent = this.createComponent(GameButtonComponent.class);
        button.add(buttonComponent);



        final HealthBarComponent healthBarComponent = this.createComponent(HealthBarComponent.class);


        healthBarComponent.healtBar=  new ProgressBar(0,1,0.01f ,false, context.getSkin() ,"hp");
        //TODO ezt beállítni widht height
        //TODO EnemyMovingSystemben is hard Coded
        healthBarComponent.healtBar.setSize(80,5);

        context.getStage().addActor(healthBarComponent.healtBar);
        healthBarComponent.healthBarPercentege =1;

        Vector3 vector = new Vector3(b2DComponent.body.getPosition().x,   b2DComponent.body.getPosition().y+height/2,1);
        Vector3 vectorUp = context.getScreenViewport().getCamera().project(vector,0,0, Gdx.graphics.getWidth()-1,(Gdx.graphics.getHeight()-1));

        //Vector3 vectorUp = context.getScreenViewport().project(vector);
        //HEALTBAR vele együtt mozog

        healthBarComponent.healtBar.setPosition(vectorUp.x,vectorUp.y,0);
        button.add(healthBarComponent);

        this.addEntity(button);

    }

                                                                //b2s box hány egységnyi (*32pixel)                  ez pedig az animáció negyítása kicsinytése ez is egységnyi *32
    public void createPlayer(final Vector2 playerSpawnLocation, final float bodyWidth, final float bodyHeight, final float animWidth, final float animHeight, int buttonID){




        final Entity player = this.createEntity();
        playerCounter++;
        final PlayerComponent playerComponent = this.createComponent(PlayerComponent.class);
        playerComponent.speed =3;
        //TODO tank
        playerComponent.setDirection.set(playerSpawnLocation.x,playerSpawnLocation.y,0);
        player.add(playerComponent);
        //box2d Component
        playerComponent.playerId = playerCounter;
        final B2DComponent b2DComponent = this.createComponent(B2DComponent.class);
        player.add(setB2DcomponantBox(b2DComponent,playerSpawnLocation.x,playerSpawnLocation.y,bodyWidth,bodyHeight,player,BIT_PLAYER, (short)(BIT_GROUND | BIT_GAME_OBJECT|BIT_ENEMY),false));



        //Tank component
        final TankComponent tankComponent = this.createComponent(TankComponent.class);
        tankComponent.tankId =buttonID;
        //tankComponent.currentTankSlots=0;
        tankComponent.maximumTankSlots= 2;
        player.add(tankComponent);

            // animation component
        final AnimationComponent animationComponent = this.createComponent(AnimationComponent.class);
        player.add(setAnimationComponent(animationComponent,animWidth,animHeight,AnimationType.HERO_MOVE_LEFT));

     //   final SelectAbleComponent selectAbleComponent = this.createComponent(SelectAbleComponent.class);
     //   selectAbleComponent.isSelected = false;

     //     player.add(selectAbleComponent);

            this.addEntity(player);

            //TODO VIGYÁZZ: itt a width, height az külön meg van adva ahrd kodban
           createPlayerIconButton(new Vector2(30.5f- (float)(playerCounter*2.5f ),1),1,1, playerCounter);
      // köv  new Vector2(28f,1),2,2)



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
        animationComponent.playMode = Animation.PlayMode.LOOP;

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
     //   b2DComponent.renderPosition.set(b2DComponent.body.getPosition().x-animationComponent.width*0.5f,b2DComponent.body.getPosition().y-b2DComponent.height*0.5f);
        b2DComponent.renderPosition.set(b2DComponent.body.getPosition());

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


    public B2DComponent setB2DcomponantBox(B2DComponent b2DComponent,float spawnLoacationX, float spawnLocationY, float width, float height,Entity userData ,short categoryBits, short maskBits, boolean isSensor ){
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
        MyTowerDefenseGame.FIXTURE_DEF.isSensor=isSensor;
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
        animationComponent.playMode = Animation.PlayMode.LOOP;
      //TODO
        animationComponent.width=32*UNIT_SCALE*width;
        animationComponent.height=32*UNIT_SCALE*height;
        //                        32 *1/32

       // animationComponent.width= UNIT_SCALE*32;
        // animationComponent.height=UNIT_SCALE*32;


        return animationComponent;
    }



}
