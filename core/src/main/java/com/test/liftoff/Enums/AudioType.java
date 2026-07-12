package com.test.liftoff.Enums;

public enum AudioType {
    TITLE_THEME("sounds/title.wav", true),
    CROSSROADS("sounds/S19 Crossroads Main.wav", true),
    GREENPATH("sounds/S5 Green Path Main.wav", true),

    DAMAGE("sounds/hero_damage.wav", false),
    FOCUS_HEAL("sounds/focus_health_heal.wav", false),


    SFX_SLASH("sounds/sword_1.wav", false),
    SFX_ENEMY_HIT("sounds/hero_damage.wav", false),
    SFX_SOUL_GAIN("sounds/soul_pickup_1.wav", false),
    ZOTE_01("sounds/Zote_01.wav", false),
    ZOTE_02("sounds/Zote_02.wav", false),
    ZOTE_03("sounds/Zote_03.wav", false);

    private final String path;
    private final boolean isMusic;

    AudioType(String path, boolean isMusic) {
        this.path = path;
        this.isMusic = isMusic;
    }

    public String getPath() {
        return path;
    }

    public boolean isMusic() {
        return isMusic;
    }
}
