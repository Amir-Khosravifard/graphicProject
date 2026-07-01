package com.test.liftoff.Enums;

public enum LevelType {
    CROSSROADS("tiledMaps/firstMap.tmx", AudioType.CROSSROADS, "Object Layer 1", "spawnPlayer"),
    GREENPATH("tiledMaps/greenpath.tmx", AudioType.GREENPATH, "Object Layer 1", "spawnPlayer");

    private final String mapPath;
    private final AudioType backGroundMusicType;
    private final String objectLayerName;
    private final String spawnPointName;

    LevelType(String mapPath, AudioType bgmType, String objectLayerName, String spawnPointName) {
        this.mapPath = mapPath;
        this.backGroundMusicType = bgmType;
        this.objectLayerName = objectLayerName;
        this.spawnPointName = spawnPointName;
    }

    public String getMapPath() { return mapPath; }
    public AudioType getBackGroundMusicType() { return backGroundMusicType; }
    public String getObjectLayerName() { return objectLayerName; }
    public String getSpawnPointName() { return spawnPointName; }
}
