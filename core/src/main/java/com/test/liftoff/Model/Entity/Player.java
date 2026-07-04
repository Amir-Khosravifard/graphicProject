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

    private final Vector2 lastSafePosition = new Vector2();

    public Player() {
        super(40f, 90f, 5);
    }

    public int getSoul() { return soul; }
    public void setSoul(int soul) { this.soul = soul; }
    public void addSoul(int amount) {
        this.soul += amount;
        if (this.soul > 99) this.soul = 99;
    }
    // Add this field at the top:

    // Add these getters and setters:
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
}
