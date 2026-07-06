package com.test.liftoff.Model.Entity;

import com.badlogic.gdx.math.Vector2;

public class Player extends Entity{
    private boolean isMovingLeft = false;
    private boolean joinMovingRight = false; // mapping naming convention
    private boolean isMovingRight = false;
    private boolean isDashing = false;
    private boolean isAttacking = false;
    private boolean isFocusing = false;
    private boolean isLookingDown = false;
    private boolean isWallSliding = false;
    private boolean isPogoAttacking = false;

    private final SoulVessel soulVessel = new SoulVessel();
    private final Vector2 lastSafePosition = new Vector2();

    public Player() {
        super(40f, 90f, 5); // Pure physical dimensions and health
    }

    @Override
    public boolean isIgnoringGravity() {
        return isDashing();
    }

    @Override
    public com.test.liftoff.Enums.AnimationType getAnimationType() {
        switch (this.getCurrentState()) {
            case RUNNING: return com.test.liftoff.Enums.AnimationType.KnightRun;
            case JUMPING: return com.test.liftoff.Enums.AnimationType.KnightRegularJump;
            case DOUBLE_JUMPING: return com.test.liftoff.Enums.AnimationType.KnightDoubleJump;
            case FALLING: return com.test.liftoff.Enums.AnimationType.KnightFall;
            case LANDING: return com.test.liftoff.Enums.AnimationType.KnightRegularLanding;
            case DASHING: return com.test.liftoff.Enums.AnimationType.KnightDash;
            case ATTACKING: return com.test.liftoff.Enums.AnimationType.KnightNailSlash;
            case POGO_ATTACKING: return com.test.liftoff.Enums.AnimationType.KnightPogo;
            case WALL_SLIDING: return com.test.liftoff.Enums.AnimationType.KnightWallSlide;
            case WALL_JUMPING: return com.test.liftoff.Enums.AnimationType.KnightWallJump;
            case FOCUS_START: return com.test.liftoff.Enums.AnimationType.KnightFocusStart;
            case FOCUS_LOOPING: return com.test.liftoff.Enums.AnimationType.KnightFocusLoop;
            case FOCUS_GET: return com.test.liftoff.Enums.AnimationType.KnightFocusGet;
            case FOCUS_END: return com.test.liftoff.Enums.AnimationType.KnightFocusEnd;
            case IDLE:
            default: return com.test.liftoff.Enums.AnimationType.KnightIdle;
        }
    }


    public boolean executeFocusHeal() {
        if (soulVessel.canAffordHeal() && this.health < 5) {
            soulVessel.consumeSoulForHeal();
            this.health += 1;
            return true;
        }
        return false;
    }

    @Override
    public float getSpriteOffsetX(float frameWidth) {
        return ((frameWidth - this.width) / 2f) - 5f;
    }

    // Pure Getters/Setters only
    public int getSoul() { return soulVessel.getSoul(); }
    public void setSoul(int amount) { soulVessel.setSoul(amount); }
    public void addSoul(int amount) { soulVessel.addSoul(amount); }
    public boolean isWallSliding() { return isWallSliding; }
    public void setWallSliding(boolean wallSliding) { isWallSliding = wallSliding; }
    public boolean isLookingDown() { return isLookingDown; }
    public void setLookingDown(boolean lookingDown) { this.isLookingDown = lookingDown; }
    public boolean isDashing() { return isDashing; }
    public void setDashing(boolean dashing) { this.isDashing = dashing; }
    public boolean isAttacking() { return isAttacking; }
    public void setAttacking(boolean attacking) { this.isAttacking = attacking; }
    public boolean isFocusing() { return isFocusing; }
    public void setFocusing(boolean focusing) { this.isFocusing = focusing; }
    public boolean isPogoAttacking() { return isPogoAttacking; }
    public void setPogoAttacking(boolean pogo) { this.isPogoAttacking = pogo; }
    public boolean isMovingLeft() { return isMovingLeft; }
    public void setMovingLeft(boolean movingLeft) { this.isMovingLeft = movingLeft; }
    public boolean isMovingRight() { return isMovingRight; }
    public void setMovingRight(boolean movingRight) { this.isMovingRight = movingRight; }
}
