package com.mygdx.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.game.UI.AnimationType;

public class AnimationComponent implements Component, Pool.Poolable{
    public AnimationType aniType;
    public float aniTime;
    public float width; // ez a gameObjectekhez van!
    public float height; //ez a Gamerenderer setframe boundba hozzá van rendelve egyfajtas skálázással, a bodyhoz
    public Animation.PlayMode playMode;
    public boolean startAnimationCyle;
    public int specificAnimationFrame;
    public boolean finishAnimationCycle;
    public boolean isSpecificFrameStarted;
    public boolean isSpecificFrameFinished;
    public boolean isHorizontalyFlipped;
    public boolean isVerticallyFlipped;
    public boolean isFliped;
    public boolean isFaceingRight;
    public boolean previoslyFacingRight;


    //TODO ezeket skálázni majd jeleneleg hard koded a gamscrenn new player inic és a ECS cretbutonban.



    @Override
    public void reset() {
        aniType = null;
        aniTime = 0;
        width =0;
        height=0;
        playMode=null;
        startAnimationCyle = false;
        finishAnimationCycle=false;
        specificAnimationFrame = -1;
        isSpecificFrameStarted= false;
        isSpecificFrameFinished= false;
        isVerticallyFlipped= false;
        isHorizontalyFlipped = false;
        isFliped= false;
        isFaceingRight = false;
        previoslyFacingRight =false;

    }
}
