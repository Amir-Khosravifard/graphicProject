package com.test.liftoff.Controller;

import com.test.liftoff.Enums.EntityState;
import com.test.liftoff.Model.Entity.Player;

public class PlayerController {
    private final Player player;

    public float dashTimer = 0f;
    public float dashCooldownTimer = 0f;
    public boolean hasDashedInAir = false;
    public float attackTimer = 0f;
    public final float attackDuration = 0.2f;
    public float focusTimer = 0f;
    public final float focusRequiredTime = 1.5f;
    public boolean hasDoubleJumped = false;
    public float wallJumpControlLockout = 0f;
    public float invincibilityTimer = 0f;
    private float landingTimer = 0f;

    public float knockbackTimer = 0f;

    public PlayerController(Player player) {
        this.player = player;
    }

    public void updateTimersAndMovement(float delta) {
        if (dashCooldownTimer > 0) dashCooldownTimer -= delta;
        if (invincibilityTimer > 0) invincibilityTimer -= delta;
        if (wallJumpControlLockout > 0) wallJumpControlLockout -= delta;
        if (knockbackTimer > 0) knockbackTimer -= delta;

        if (player.isAttacking() || player.isPogoAttacking()) {
            attackTimer -= delta;
            if (attackTimer <= 0) {
                player.setAttacking(false);
                player.setPogoAttacking(false);
            }
        }


        if (player.isNoclip()) {
            if (player.isMovingLeft()) player.setVelocityX(-700f);
            else if (player.isMovingRight()) player.setVelocityX(700f);
            else player.setVelocityX(0f);

            if (player.isMovingUp()) player.setVelocityY(700f);
            else if (player.isLookingDown()) player.setVelocityY(-700f);
            else player.setVelocityY(0f);

            player.setCurrentState(EntityState.JUMPING);
            return;
        }

        if (player.isDashing()) {
            dashTimer -= delta;
            if (dashTimer <= 0) {
                player.setDashing(false);
            } else {
                player.setVelocityX(player.isLookingRight() ? 800f : -800f);
                player.setVelocityY(0f);
            }
        } else if (knockbackTimer > 0) {
            player.setVelocityX(player.isLookingRight() ? -380f : 380f);
        } else if (player.isFocusing() || isFocusState(player.getCurrentState())) {
            player.setVelocityX(0f);
            player.setVelocityY(0f);
            focusTimer += delta;

            if (player.getCurrentState() == EntityState.FOCUS_START && focusTimer >= 0.10f) {
                player.setCurrentState(EntityState.FOCUS_LOOPING);
                focusTimer = 0f;
            } else if (player.getCurrentState() == EntityState.FOCUS_LOOPING) {
                if (focusTimer >= focusRequiredTime) {
                    if (player.executeFocusHeal()) {
                        player.setCurrentState(EntityState.FOCUS_GET);
                    } else {
                        player.setCurrentState(EntityState.FOCUS_END);
                        player.setFocusing(false);
                    }
                    focusTimer = 0f;
                }
            } else if (player.getCurrentState() == EntityState.FOCUS_GET && focusTimer >= 0.20f) {
                player.setCurrentState(EntityState.FOCUS_END);
                player.setFocusing(false);
                focusTimer = 0f;
            } else if (player.getCurrentState() == EntityState.FOCUS_END && focusTimer >= 0.10f) {
                player.setCurrentState(EntityState.IDLE);
                focusTimer = 0f;
            }
        } else {
            if (wallJumpControlLockout <= 0) {
                if (player.isMovingLeft()) {
                    player.setVelocityX(-400f);
                    player.setLookingRight(false);
                } else if (player.isMovingRight()) {
                    player.setVelocityX(400f);
                    player.setLookingRight(true);
                } else {
                    player.setVelocityX(0f);
                }
            }
        }
        player.setCurrentState(calculateNextState(delta));
    }

    private EntityState calculateNextState(float delta) {
        EntityState current = player.getCurrentState();

        if (player.isDashing()) return EntityState.DASHING;
        if (wallJumpControlLockout > 0) return EntityState.WALL_JUMPING;
        if (player.isWallSliding()) return EntityState.WALL_SLIDING;
        if (player.isPogoAttacking()) return EntityState.POGO_ATTACKING;
        if (player.isAttacking()) return EntityState.ATTACKING;

        if (player.isFocusing() || isFocusState(current)) return current;

        if (!player.isOnGround()) {
            if (hasDoubleJumped && player.getVelocity().y > 0) return EntityState.DOUBLE_JUMPING;
            return player.getVelocity().y > 0 ? EntityState.JUMPING : EntityState.FALLING;
        }

        if (player.getVelocity().x != 0) return EntityState.RUNNING;

        if (current == EntityState.FALLING || current == EntityState.JUMPING) {
            landingTimer = 0f;
            return EntityState.LANDING;
        }

        if (current == EntityState.LANDING) {
            landingTimer += delta;
            return landingTimer >= 0.1333f ? EntityState.IDLE : EntityState.LANDING;
        }

        return EntityState.IDLE;
    }

    private boolean isFocusState(EntityState state) {
        return state == EntityState.FOCUS_START ||
            state == EntityState.FOCUS_LOOPING ||
            state == EntityState.FOCUS_GET ||
            state == EntityState.FOCUS_END;
    }

    public void resetGroundState() {
        hasDashedInAir = false;
        hasDoubleJumped = false;
        wallJumpControlLockout = 0f;
    }
}
