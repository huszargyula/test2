package com.mygdx.game.audio;

public enum AudioType {
    // 2 féle hangfáj típus szokott lenni:
    // rövid: egy effekt pl
    //hosszabb: háttérzene
    // true: ez egy háttérzene
    // 0.3f: le kell halkítni mert alapból gecihangos

    INTRO("audio/intro.mp3", true, 0.3f),
    SELECT("audio/select.wav",false,0.5f);


    private final String filePath;
    private final boolean isMusic;
    private final float volume;

    AudioType(final String filePath, final boolean isMusic, final float volume) {

        this.filePath = filePath;
        this.isMusic = isMusic;
        this.volume = volume;


    }

    public String getFilePath() {
        return filePath;
    }

    public float getVolume() {
        return volume;
    }

    public boolean isMusic() {
        return isMusic;
    }
}