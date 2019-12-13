package com.mygdx.game.UI;

import com.mygdx.game.audio.AudioType;
/*

egy png be 1 sorniy animácó vagy több sornyonyi
ezeket a png ket öszefűzni egy atlassal

*/

public enum AnimationType {

    //elérséi ut, region név , idő a framek között
    HERO_MOVE_RIGHT("anim/hero_all_animation.atlas","hero_anim", 0.12f , 0,70,64),
    HERO_MOVE_LEFT("anim/hero_all_animation.atlas","hero_anim", 0.12f , 1,70,64),

    HERO_FIGHT_RIGHT("anim/hero_all_animation.atlas","hero_anim_fight", 0.5f , 0,110,70),
    HERO_FIGHT_LEFT("anim/hero_all_animation.atlas","hero_anim_fight", 0.5f , 1,110,70),

    MANO_MAGE_BASIC_DEATH_LEFT("anim/mano_all.atlas","mano_mage_death", 0.15f , 0,150,125),

    MANO_MAGE_BASIC_FIGHT_LEFT("anim/mano_all.atlas","mano_mage_fight", 0.4f , 0,98,113),

    MANO_MAGE_BASIC_MOVE_LEFT("anim/mano_all.atlas","mano_mage_moving", 0.15f , 0,93,105),

    MANO_MAGE_BASIC_WAIT_LEFT("anim/mano_all.atlas","mano_mage_wait", 0.15f , 0,75,108),

    MANO_FIGHTER_BASIC_DEATH_LEFT("anim/mano_all.atlas","mano_mage_death", 0.15f , 0,175,125),

    MANO_FIGHTER_BASIC_FIGHT_LEFT("anim/mano_all.atlas","mano_mage_fight", 0.4f , 0,130,120),

    MANO_FIGHTER_BASIC_MOVE_LEFT("anim/mano_all.atlas","mano_mage_moving", 0.15f , 0,125,120),

    MANO_FIGHTER_BASIC_WAIT_LEFT("anim/mano_all.atlas","mano_mage_wait", 0.15f , 0,125,120),



    BIRD_MOVE_LEFT("anim/bird_anim2.atlas","bird_anim_all", 0.05f , 1,32,32),
    BIRD_MOVE_RIGHT("anim/bird_anim2.atlas","bird_anim_all", 0.05f , 0,32,32),
    BUTTON_IS_SELECTED("anim/button.atlas","gomb_anim",0.20f,0,32,32),
    BUTTON_IS_NOT_SELECTED("anim/button.atlas","gomb",0.50f,0,32,32);



    private final String atlasPath;
    private final String atlasKey;
    private final float frameTime;
    private final  int rowIndex;
    private final int atlasSplitWidth;
    private final int atlasSplitHeight;

AnimationType(final String atlasPath,final String atlasKey, final float frameTime, final  int rowIndex, final int splitWidth, final int splitHeight){
    this.atlasKey =atlasKey;
    this.atlasPath =atlasPath;
    this.frameTime =frameTime;
    this.rowIndex =rowIndex;
    this.atlasSplitWidth = splitWidth;
    this.atlasSplitHeight = splitHeight;




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

    public int getAtlasSplitHeight() { return atlasSplitHeight; }

    public int getAtlasSplitWidth() {  return atlasSplitWidth; }
}
