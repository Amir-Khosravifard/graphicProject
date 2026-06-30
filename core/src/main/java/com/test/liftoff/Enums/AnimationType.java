package com.test.liftoff.Enums;

import com.badlogic.gdx.graphics.g2d.Animation;

public enum AnimationType {
    KnightIdle("animations/animation/Idle.png", 9, Animation.PlayMode.LOOP),
    KnightRun("animations/animation/Run.png", 13, Animation.PlayMode.LOOP),
    KnightRegularJump("animations/animation/Airborne.png", 12, Animation.PlayMode.NORMAL),
    KnightRegularLanding("animations/animation/Landing.png", 4, Animation.PlayMode.NORMAL),
    KnightFall("animations/animation/Fall.png", 6, Animation.PlayMode.LOOP_PINGPONG),
    KnightDash("animations/animation/Dash.png", 12, Animation.PlayMode.NORMAL),
    KnightSlash("animations/animation/Slash.png", 5, Animation.PlayMode.LOOP),
    KnightFocus("animations/animation/Focus.png", 4, Animation.PlayMode.LOOP);


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
