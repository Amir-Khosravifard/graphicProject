package com.test.liftoff.View;

import com.test.liftoff.Enums.AnimationType;
import com.test.liftoff.Enums.EntityState;

public class RenderClock {
    private float animTime = 0f;
    private AnimationType lastAnimation = null;

    public void update(AnimationType currentAnimation, float delta) {
        if (currentAnimation != lastAnimation) {
            animTime = 0f;
            lastAnimation = currentAnimation;
        } else {
            animTime += delta;
        }
    }

    public float getAnimTime() {
        return animTime;
    }
}
