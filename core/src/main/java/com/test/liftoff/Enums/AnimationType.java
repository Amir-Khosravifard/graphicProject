package com.test.liftoff.Enums;

import com.badlogic.gdx.graphics.g2d.Animation;

public enum AnimationType {
    KnightIdle("animations/animation/Idle.png", 9, Animation.PlayMode.LOOP),
    KnightRun("animations/animation/Run.png", 13, Animation.PlayMode.LOOP),
    KnightRegularJump("animations/animation/Airborne.png", 12, Animation.PlayMode.NORMAL),
    KnightRegularLanding("animations/animation/Landing.png", 4, Animation.PlayMode.NORMAL),
    KnightFall("animations/animation/Fall.png", 6, Animation.PlayMode.LOOP_PINGPONG),
    KnightDash("animations/animation/Dash.png", 12, Animation.PlayMode.NORMAL),
    KnightNailSlash("animations/animation/SlashAlt.png", 5, Animation.PlayMode.LOOP),
    KnightFocusStart("animations/animation/Focus Start.png", 3, Animation.PlayMode.NORMAL),
    KnightFocusLoop("animations/animation/Focus.png", 4, Animation.PlayMode.LOOP),
    KnightFocusGet("animations/animation/Focus Get.png", 6, Animation.PlayMode.NORMAL),
    KnightFocusEnd("animations/animation/Focus End.png", 3, Animation.PlayMode.NORMAL),
    KnightDoubleJump("animations/animation/Double Jump.png", 8, Animation.PlayMode.NORMAL),
    KnightPogo("animations/animation/DownSlash.png", 5, Animation.PlayMode.NORMAL);


    private final String path;
    private final  int frameCount;
    private final Animation.PlayMode playMode;

    AnimationType(String path, int frameCount, Animation.PlayMode playMode) {
        this.path = path;
        this.frameCount = frameCount;
        this.playMode = playMode;
    }

    public String getPath() {
        return path;
    }

    public int getFrameCount() {
        return frameCount;
    }

    public Animation.PlayMode getPlayMode() {
        return playMode;
    }
}
