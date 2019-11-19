package com.mygdx.game.audio;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.mygdx.game.MyTowerDefenseGame;

public class AudioManager {
    private  AudioType currentMusicType; //milyen fajta megy
    private  Music currentMusic;// az éppen menő zene
    private final AssetManager assetManager; //ide kell majd betölteni


    public AudioManager(final MyTowerDefenseGame context){
        this.assetManager = context.getAssetManager();
        currentMusic = null;
        currentMusicType = null;
    }

    public void playAudio(final AudioType type){
        if(type.isMusic()){

            //play Music

            if (currentMusicType == type){
                //given auidotype is already playing

                return;
            } else if (currentMusic != null){

                currentMusic.stop();
            }

            currentMusicType = type;
            currentMusic = assetManager.get(type.getFilePath(), Music.class);
            currentMusic.setLooping(true);
            currentMusic.setVolume(type.getVolume());
            currentMusic.play();

        }else{

            //play Sound
            //hasonlo
            // de itt mehet egybe sok effeket
            //rögtön játszhatja is le
            assetManager.get(type.getFilePath(), Sound.class).play(type.getVolume());


        }


    }


    public void stopCurrentMusic() {

        if (currentMusic!=null){
            currentMusic.stop();
            currentMusic = null;
            currentMusicType =null;

        }

    }
}
