package com.test.liftoff.Model.Entity;

import com.badlogic.gdx.math.Rectangle;

public class Spike {
    private final Rectangle bounds;
    private final int damageValue = 1;

    public Spike(float x, float y, float width, float height) {
        this.bounds = new Rectangle(x, y, width, height);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public int getDamageValue() {
        return damageValue;
    }
}
