package com.test.liftoff.Model.Entity;

import com.test.liftoff.Enums.AnimationType;

public class Zote extends Entity {
    public Zote(float x, float y) {
        super(64f, 90f, 5);
        this.position.set(x, y);
    }

    @Override
    public AnimationType getAnimationType() {
        return AnimationType.Zote_Idle;
    }

    @Override
    public float getSpriteOffsetX(float frameWidth) {
        return (frameWidth - this.width) / 2f;
    }


}
