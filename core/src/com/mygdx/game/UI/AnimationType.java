package com.mygdx.game.UI;

import com.mygdx.game.audio.AudioType;

public enum AnimationType {
    //elérséi ut, region név , idő
    HERO_MOVE_LEFT("anim/bird_anim.atlas","bird_an", 0.05f , 0),
    HERO_MOVE_RIGHT("anim/bird_anim.atlas","bird_an", 0.05f , 1);

    private final String atlasPath;
    private final String atlasKey;
    private final float frameTime;
    private final  int rowIndex;

AnimationType(final String atlasPath,final String atlasKey, final float frameTime, final  int rowIndex){
    this.atlasKey =atlasKey;
    this.atlasPath =atlasPath;
    this.frameTime =frameTime;
    this.rowIndex =rowIndex;




}

    public String getAtlasPath() {
        return atlasPath;
    }

    public float getFrameTime() {
    return  frameTime;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public String getAtlasKey() {
        return atlasKey;
    }
}
