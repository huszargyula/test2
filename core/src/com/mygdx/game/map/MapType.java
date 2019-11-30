package com.mygdx.game.map;

public enum MapType {
    MAP_1("map/map_ID_1.tmx"),
    MAP_2("map/map_ID_2.tmx"),
    MAP_3("map/map_ID_3.tmx");

    private final String filePath;

    MapType(final String filePath){this.filePath = filePath;}
    public String getFilePath(){ return  filePath;}

}
