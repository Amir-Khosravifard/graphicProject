package com.test.liftoff.View;

import com.test.liftoff.Enums.AnimationType;
import com.test.liftoff.Enums.EntityState;

public class RenderClock {
    private float animTime = 0f;
    private AnimationType lastAnimation = null; // 💡 Track the visual asset sheet instead!

    public void update(AnimationType currentAnimation, float delta) {
        if (currentAnimation != lastAnimation) {
            animTime = 0f;         // Only reset to frame 0 if the actual sheet changes
            lastAnimation = currentAnimation;
        } else {
            animTime += delta;     // Otherwise tick forward smoothly
        }
    }

    public float getAnimTime() {
        return animTime;
    }
}
