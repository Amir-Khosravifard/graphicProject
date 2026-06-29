package com.test.liftoff.View;

import com.test.liftoff.Enums.EntityState;

public class RenderClock {
    private float animTime = 0f;
    private EntityState lastState = EntityState.IDLE;

    public void update(EntityState currentState, float delta) {
        if (currentState != lastState) {
            animTime = 0f;         // Reset frame to 0 when state changes!
            lastState = currentState;
        } else {
            animTime += delta;     // Otherwise tick the frame forward smoothly
        }
    }

    public float getAnimTime() {
        return animTime;
    }
}
