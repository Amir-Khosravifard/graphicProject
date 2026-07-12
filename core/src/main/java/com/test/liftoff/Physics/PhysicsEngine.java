package com.test.liftoff.Physics;

import com.badlogic.gdx.math.Rectangle;
import com.test.liftoff.Model.Entity.Entity;
import com.test.liftoff.Model.Entity.Player;

import java.util.ArrayList;

public class PhysicsEngine {
    private static final int GRAVITY = 1000;

    public static CollisionResult resolveMovement(Entity entity, float delta, ArrayList<Rectangle> platforms) {
        CollisionResult result = new CollisionResult();


        if (entity instanceof Player && ((Player) entity).isNoclip()) {
            entity.setPositionX(entity.getPosition().x + entity.getVelocity().x * delta);
            entity.setPositionY(entity.getPosition().y + entity.getVelocity().y * delta);
            return result;
        }

        boolean ignoreGravity = entity.isIgnoringGravity();

        if (!entity.isOnGround() && !ignoreGravity) {
            entity.setVelocityY(entity.getVelocity().y - GRAVITY * delta);
        }

        float maxStepTime = 0.003f;
        float accumulator = delta;
        entity.setOnGround(false);

        while (accumulator > 0) {
            float stepDelta = Math.min(accumulator, maxStepTime);
            accumulator -= stepDelta;

            entity.setPositionX(entity.getPosition().x + entity.getVelocity().x * stepDelta);
            if (platforms != null) {
                Rectangle bounds = new Rectangle(entity.getPosition().x, entity.getPosition().y, entity.getWidth(), entity.getHeight());
                for (Rectangle platform : platforms) {
                    if (bounds.overlaps(platform)) {
                        if (entity.getVelocity().x > 0) {
                            entity.setPositionX(platform.x - entity.getWidth());
                            result.setTouchingRight(true);
                        } else if (entity.getVelocity().x < 0) {
                            entity.setPositionX(platform.x + platform.width);
                            result.setTouchingLeft(true);
                        }
                        entity.setVelocityX(0);
                        break;
                    }
                }
            }

            entity.setPositionY(entity.getPosition().y + entity.getVelocity().y * stepDelta);
            if (platforms != null) {
                Rectangle bounds = new Rectangle(entity.getPosition().x, entity.getPosition().y, entity.getWidth(), entity.getHeight());
                for (Rectangle platform : platforms) {
                    if (bounds.overlaps(platform)) {
                        if (entity.getVelocity().y <= 0) {
                            entity.setPositionY(platform.y + platform.height);
                            entity.setVelocityY(0);
                            entity.setOnGround(true);
                        } else if (entity.getVelocity().y > 0) {
                            entity.setPositionY(platform.y - entity.getHeight());
                            entity.setVelocityY(0);
                        }
                        break;
                    }
                }
            }
        }

        if (platforms != null) {
            Rectangle groundCheck = new Rectangle(entity.getPosition().x, entity.getPosition().y - 1f, entity.getWidth(), 1f);
            for (Rectangle platform : platforms) {
                if (groundCheck.overlaps(platform)) {
                    entity.setOnGround(true);
                    entity.setVelocityY(0);
                    break;
                }
            }
        }
        return result;
    }
}
