package com.test.liftoff.Model.Entity;

import com.badlogic.gdx.math.Vector2;

public class Player extends Entity{
    private boolean isMovingLeft = false;
    private boolean isMovingRight = false;

    private int soul = 0;
    private boolean isDashing = false;
    private boolean isAttacking = false;
    private boolean isFocusing = false;
    private boolean isLookingDown = false;
    private boolean isWallSliding = false;


    private final SoulVessel soulVessel = new SoulVessel();
    private final Vector2 lastSafePosition = new Vector2();

    public Player() {
        super(40f, 90f, 5);
    }

    public int getSoul() { return soulVessel.getSoul(); }
    public void setSoul(int amount) { soulVessel.setSoul(amount); }
    public void addSoul(int amount) { soulVessel.addSoul(amount); }
    public SoulVessel getSoulVessel() { return soulVessel; }


    public boolean isWallSliding() {
        return isWallSliding;
    }

    public void setWallSliding(boolean wallSliding) {
        isWallSliding = wallSliding;
    }

    public boolean isLookingDown() { return isLookingDown; }
    public void setLookingDown(boolean lookingDown) { this.isLookingDown = lookingDown; }

    public boolean isDashing() { return isDashing; }
    public void setDashing(boolean dashing) { this.isDashing = dashing; }

    public boolean isAttacking() { return isAttacking; }
    public void setAttacking(boolean attacking) { this.isAttacking = attacking; }

    public boolean isFocusing() { return isFocusing; }
    public void setFocusing(boolean focusing) { this.isFocusing = focusing; }

    public Vector2 getLastSafePosition() { return lastSafePosition; }
    public void saveSafePosition(float x, float y) { this.lastSafePosition.set(x, y); }

    public boolean isMovingLeft() { return isMovingLeft; }
    public void setMovingLeft(boolean movingLeft) {
        this.isMovingLeft = movingLeft;
        if(movingLeft) setLookingRight(false);
    }

    public boolean isMovingRight() { return isMovingRight; }
    public void setMovingRight(boolean movingRight) {
        this.isMovingRight = movingRight;
        if(movingRight) setLookingRight(true);
    }

    public void jump(){
        if(isOnGround) velocity.y = 500;
    }

    public boolean executeFocusHeal() {
        // 💡 FIX: Check the active soulVessel component instead of the dead local field!
        if (soulVessel.canAffordHeal() && this.health < 5) {
            soulVessel.consumeSoulForHeal(); // Safely decrements exactly 33 points internally
            this.health += 1;
            return true; // Successfully healed!
        }
        return false; // Conditions not met (full health or out of soul)
    }
}
