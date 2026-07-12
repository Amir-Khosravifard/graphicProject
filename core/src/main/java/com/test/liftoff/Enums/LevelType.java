package com.test.liftoff.Enums;

public enum LevelType {
    CROSSROADS("tiledMaps/forgottenCrossroad.tmx", AudioType.CROSSROADS, "playerSpawnPoints", "spawnPoint1", "forgottenCrossroads/cd_room_BG_01.png"),

    GREENPATH("tiledMaps/greenPath.tmx", AudioType.GREENPATH, "playerSpawnPoints", "spawnPoint1", "greenpath/Slug_Shrine_0000_a.png");

    private final String mapPath;
    private final AudioType backGroundMusicType;
    private final String objectLayerName;
    private final String spawnPointName;
    private final String bgPath;

    LevelType(String mapPath, AudioType bgmType, String objectLayerName, String spawnPointName, String bgPath) {
        this.mapPath = mapPath;
        this.backGroundMusicType = bgmType;
        this.objectLayerName = objectLayerName;
        this.spawnPointName = spawnPointName;
        this.bgPath = bgPath;
    }

    public String getMapPath() {
        return mapPath;
    }

    public AudioType getBackGroundMusicType() {
        return backGroundMusicType;
    }

    public String getObjectLayerName() {
        return objectLayerName;
    }

    public String getSpawnPointName() {
        return spawnPointName;
    }

    public String getBgPath() {
        return bgPath;
    }
}
