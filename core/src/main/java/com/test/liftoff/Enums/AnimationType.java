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
    KnightPogo("animations/animation/DownSlash.png", 5, Animation.PlayMode.NORMAL),
    KnightWallSlide("animations/animation/Wall Slide.png", 4, Animation.PlayMode.LOOP),
    KnightWallJump("animations/animation/WallJump.png", 9, Animation.PlayMode.NORMAL),

    Crawlid_walk("animations/animation/Crawlid/Walk.png", 4, Animation.PlayMode.LOOP),
    Crawlid_turn("animations/animation/Crawlid/Turn.png", 2, Animation.PlayMode.NORMAL),
    Crawlid_death_land("animations/animation/Crawlid/Death Land.png", 2, Animation.PlayMode.NORMAL),
    Crawlid_death_air("animations/animation/Crawlid/Death Air.png", 3, Animation.PlayMode.NORMAL),


    Mosquito_idle("animations/animation/Mosquito/Idle.png", 8, Animation.PlayMode.LOOP),
    Mosquito_attack_anticipate("animations/animation/Mosquito/Attack Anticipate.png", 6, Animation.PlayMode.NORMAL),
    Mosquito_attack("animations/animation/Mosquito/Attack.png", 3, Animation.PlayMode.LOOP),
    Mosquito_death_air("animations/animation/Mosquito/Death Air.png", 3, Animation.PlayMode.NORMAL),
    Mosquito_death_land("animations/animation/Mosquito/Death Land.png", 2, Animation.PlayMode.NORMAL),


    Hornhead_idle("animations/animation/Husk_Hornhead/Idle.png", 6, Animation.PlayMode.LOOP),
    Hornhead_walk("animations/animation/Husk_Hornhead/Walk.png", 7, Animation.PlayMode.LOOP),
    Hornhead_attack_anticipate("animations/animation/Husk_Hornhead/Attack Anticipate.png", 5, Animation.PlayMode.NORMAL),
    Hornhead_attack_lunge("animations/animation/Husk_Hornhead/Attack Lunge.png", 12, Animation.PlayMode.LOOP),
    Hornhead_death_land("animations/animation/Husk_Hornhead/Death Land.png", 8, Animation.PlayMode.NORMAL),


    Crystallized_idle("animations/animation/Crystallized/Idle.png", 5, Animation.PlayMode.LOOP),
    Crystallized_run("animations/animation/Crystallized/Run.png", 6, Animation.PlayMode.LOOP),
    Crystallized_shoot("animations/animation/Crystallized/Shoot.png", 7, Animation.PlayMode.NORMAL),
    Crystallized_laser("animations/animation/Effects/CrystalLaser.png", 5, Animation.PlayMode.LOOP),
    Crystallized_death("animations/animation/Crystallized/Death Land.png", 3, Animation.PlayMode.LOOP),

    DashEffect("animations/animation/Effects/Dash Effect.png", 8, Animation.PlayMode.NORMAL),
    SideSlashEffect("animations/animation/Effects/SlashEffect.png", 4, Animation.PlayMode.NORMAL),
    DownSlashEffect("animations/animation/Effects/DownSlashEffect.png", 6, Animation.PlayMode.NORMAL),


    FK_Idle("animations/animation/False_knight/Idle.png", 5, Animation.PlayMode.LOOP),
    FK_Turn("animations/animation/False_knight/Turn.png", 2, Animation.PlayMode.NORMAL),
    FK_RunAntic("animations/animation/False_knight/Run Antic.png", 2, Animation.PlayMode.NORMAL),
    FK_Run("animations/animation/False_knight/Run.png", 5, Animation.PlayMode.LOOP),
    FK_AttackAntic("animations/animation/False_knight/Attack Antic.png", 6, Animation.PlayMode.NORMAL),
    FK_Attack("animations/animation/False_knight/Attack.png", 3, Animation.PlayMode.NORMAL),
    FK_AttackRecover("animations/animation/False_knight/Attack Recover.png", 5, Animation.PlayMode.NORMAL),
    FK_JumpAntic("animations/animation/False_knight/Jump Antic.png", 3, Animation.PlayMode.NORMAL),
    FK_Jump("animations/animation/False_knight/Jump.png", 4, Animation.PlayMode.LOOP),
    FK_JumpAttack("animations/animation/False_knight/Jump Attack.png", 8, Animation.PlayMode.NORMAL),
    FK_Land("animations/animation/False_knight/Land.png", 5, Animation.PlayMode.NORMAL),
    FK_StunRecover("animations/animation/False_knight/Stun Recover.png", 6, Animation.PlayMode.NORMAL),
    FK_BodyOpen("animations/animation/False_knight/Body.png", 5, Animation.PlayMode.LOOP),
    FK_DeathHit("animations/animation/False_knight/DeathHit.png", 3, Animation.PlayMode.NORMAL),
    FK_DeathFall("animations/animation/False_knight/DeathFall.png", 3, Animation.PlayMode.LOOP),
    FK_DeathLand("animations/animation/False_knight/DeathLand.png", 11, Animation.PlayMode.NORMAL),

    Zote_Idle("animations/animation/Zote/Idle.png", 5, Animation.PlayMode.LOOP);


    private final String path;
    private final int frameCount;
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
