package com.test.liftoff.Model;

import com.badlogic.gdx.math.Vector2;

public class Player {
    private Vector2 position = new Vector2();
    private Vector2 velocity = new Vector2();
    private boolean isOnGround = false;
    private boolean isMovingLeft = false;
    private boolean isMovingRight = false;
    private boolean isLookingRight = true;

    public boolean isLookingRight() {
        return isLookingRight;
    }


    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public void setVelocityY(float y) {
        this.velocity.y = y;
    }

    public void setVelocityX(float x) {
        this.velocity.x = x;
    }

    public void setPositionY(float y) {
        this.position.y = y;
    }

    public void setPositionX(float x) {
        this.position.x = x;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public boolean isOnGround() {
        return isOnGround;
    }

    public boolean isMovingLeft() {
        return isMovingLeft;
    }

    public boolean isMovingRight() {
        return isMovingRight;
    }

    public void setOnGround(boolean onGround) {
        isOnGround = onGround;
    }

    public void setMovingLeft(boolean movingLeft) {
        isMovingLeft = movingLeft;
        if(movingLeft)
            isLookingRight = false;
    }

    public void setMovingRight(boolean movingRight) {
        isMovingRight = movingRight;
        if(movingRight)
            isLookingRight = true;
    }

    public void setLookingRight(boolean lookingRight) {
        isLookingRight = lookingRight;
    }

    public void jump(){
        if(isOnGround)
            velocity.y = 500;
    }
}
