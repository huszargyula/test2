package com.mygdx.game.UI;

import com.mygdx.game.audio.AudioType;
/*

egy png be 1 sorniy animácó
ezeket a png ket öszefűzni egy atlassal

*/

public enum AnimationType {
    //elérséi ut, region név , idő a framek között
    HERO_MOVE_LEFT("anim/bird_anim.atlas","bird_an", 0.05f , 0),
    HERO_MOVE_RIGHT("anim/bird_anim.atlas","bird_an", 0.05f , 1),
    BUTTON_IS_SELECTED("anim/button.atlas","gomb_anim",0.20f,0),
    BUTTON_IS_NOT_SELECTED("anim/button.atlas","gomb",1.20f,0);



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
