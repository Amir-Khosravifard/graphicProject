package com.test.liftoff.Model.Entity;

import com.badlogic.gdx.math.Vector2;

public class Player extends Entity{
    private boolean isMovingLeft = false;
    private boolean isMovingRight = false;

    public Player() {
        super(40f, 90f);
    }

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
