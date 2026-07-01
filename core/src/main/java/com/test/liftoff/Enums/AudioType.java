package com.test.liftoff.Enums;

public enum AudioType {
    TITLE_THEME("sounds/title.wav", true),
    CROSSROADS("sounds/S19 Crossroads Main.wav", true),
    GREENPATH("sounds/S5 Green Path Main.wav", true);

//    SFX_SLASH("audio/sfx/slash.wav", false),
//    SFX_DASH("audio/sfx/dash.wav", false),
//    SFX_JUMP("audio/sfx/jump.wav", false),
//    SFX_HEAL("audio/sfx/heal.wav", false);

    private final String path;
    private final boolean isMusic;

    AudioType(String path, boolean isMusic) {
        this.path = path;
        this.isMusic = isMusic;
    }

    public String getPath() { return path; }
    public boolean isMusic() { return isMusic; }
}
