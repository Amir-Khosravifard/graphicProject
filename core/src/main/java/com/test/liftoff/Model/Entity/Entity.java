package com.test.liftoff.Model.Entity;

import com.badlogic.gdx.math.Vector2;
import com.test.liftoff.Enums.AnimationType;
import com.test.liftoff.Enums.EntityState;

public abstract class Entity {
    protected Vector2 position = new Vector2();
    protected Vector2 velocity = new Vector2();
    protected float width;
    protected float height;
    protected boolean isOnGround = false;
    protected boolean isLookingRight = true;

    protected EntityState currentState = EntityState.IDLE;

    public Entity(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public EntityState getCurrentState() { return currentState; }
    public void setCurrentState(EntityState newState) { this.currentState = newState; }
    public Vector2 getPosition() { return position; }
    public void setPosition(Vector2 position) { this.position.set(position); }
    public void setPositionX(float x) { this.position.x = x; }
    public void setPositionY(float y) { this.position.y = y; }

    public Vector2 getVelocity() { return velocity; }
    public void setVelocityX(float x) { this.velocity.x = x; }
    public void setVelocityY(float y) { this.velocity.y = y; }

    public float getWidth() { return width; }
    public float getHeight() { return height; }

    public boolean isOnGround() { return isOnGround; }
    public void setOnGround(boolean onGround) { isOnGround = onGround; }

    public boolean isLookingRight() { return isLookingRight; }
    public void setLookingRight(boolean lookingRight) { this.isLookingRight = lookingRight; }




}
