package com.test.liftoff.Physics;

public class CollisionResult {
    private boolean touchingLeft = false;
    private boolean touchingRight = false;

    public boolean isTouchingLeft() {
        return touchingLeft;
    }

    public void setTouchingLeft(boolean touchingLeft) {
        this.touchingLeft = touchingLeft;
    }

    public boolean isTouchingRight() {
        return touchingRight;
    }

    public void setTouchingRight(boolean touchingRight) {
        this.touchingRight = touchingRight;
    }
}
