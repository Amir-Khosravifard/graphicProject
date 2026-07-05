package com.test.liftoff.Controller;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.test.liftoff.Enums.EntityState;
import com.test.liftoff.Model.Entity.Entity;
import com.test.liftoff.Model.Entity.Player;
import com.test.liftoff.Model.Entity.Spike;
import com.test.liftoff.Physics.CollisionResult;
import com.test.liftoff.Physics.PhysicsEngine;

import java.util.ArrayList;
import java.util.HashMap;

public class GameController {
    private static int gravity = 1000;
    private Player player;


    private HashMap<Entity, Float> landingTimers = new HashMap<>();

    private ArrayList<Entity> entities = new ArrayList<>();
    private ArrayList<Rectangle> platforms = new ArrayList<>();

    private float dashTimer = 0f;
    private float dashCooldownTimer = 0f;
    private boolean hasDashedInAir = false;

    private float attackTimer = 0f;
    private final float attackDuration = 0.2f;

    private float focusTimer = 0f;
    private final float focusRequiredTime = 1.5f;

    private boolean hasDoubleJumped = false;
    private boolean isPogoAttacking = false;

    private boolean wallLeft = false;
    private boolean wallRight = false;

    private ArrayList<Spike> spikes = new ArrayList<>();
    private Vector2 spawnPoint = new Vector2();
    private float invincibilityTimer = 0f;

    private float wallJumpControlLockout = 0f;

    public void setPlatforms(ArrayList<Rectangle> platforms) {
        this.platforms = platforms;
    }


    public ArrayList<Entity> getEntities() {
        return entities;
    }

    public Player getPlayer() {
        return player;
    }


    public GameController(Player player) {
        this.player = player;
        this.entities.add(player);
    }

    public Vector2 getPlayerPosition(){
        return player.getPosition();
    }
    public void setMovingRight(boolean movingRight){
        player.setMovingRight(movingRight);
    }
    public void setMovingLeft(boolean movingLeft){
        player.setMovingLeft(movingLeft);
    }

    public float getInvincibilityTimer() {
        return invincibilityTimer;
    }

    public void jumpPlayer() {
        if (player.isDead() || player.isFocusing()) return;

        if (player.isOnGround()) {
            player.getVelocity().y = 500f; // Standard Jump
        }
        else if (player.isWallSliding() || wallLeft || wallRight) {
            // 💡 WALL KICK TRIGGER: Propels player up and out away from the wall surface!
            player.getVelocity().y = 480f;
            wallJumpControlLockout = 0.15f; // Lock manual arrow controls briefly for game-feel
//            player.setCurrentState(EntityState.WALL_JUMPING); // 💡 Set Wall Jump state immediately

            if (wallRight) {
                player.getVelocity().x = -420f;
                player.setLookingRight(false); // Face left away from right wall
            } else if (wallLeft) {
                player.getVelocity().x = 420f;
                player.setLookingRight(true);  // Face right away from left wall
            }

            // Hollow Knight Feature: Wall jumping completely refreshes your Double Jump capability!
            hasDoubleJumped = false;
            hasDashedInAir = false;
            player.setWallSliding(false);
        }
        else if (!hasDoubleJumped) {
            player.getVelocity().y = 520f; // Monarch Wings Double Jump
            hasDoubleJumped = true;
        }
    }

    public void handlePlayerDash() {
        if (player.isFocusing() || player.isAttacking() || player.isDead()) return;

        if (dashCooldownTimer <= 0f && (!hasDashedInAir || player.isOnGround())) {
            player.setDashing(true);
            dashTimer = 0.15f;
            dashCooldownTimer = 0.6f;

            if (!player.isOnGround()) {
                hasDashedInAir = true;
            }

            player.getVelocity().x = player.isLookingRight() ? 800f : -800f;
            player.getVelocity().y = 0f;
        }
    }

    public void setLookingDown(boolean lookingDown){
        player.setLookingDown(lookingDown);
    }




    public void handlePlayerAttack() {
        if (player.isDashing() || player.isFocusing() || player.isDead()) return;

        if (!player.isAttacking() && !isPogoAttacking) {

            if (!player.isOnGround() && player.isLookingDown()) {
                isPogoAttacking = true;
                player.setAttacking(true);
            } else {
                player.setAttacking(true);
                player.addSoul(11);
            }
            attackTimer = attackDuration;
        }
    }

    public void damagePlayer(int amount) {
        if (player.isDead() || invincibilityTimer > 0f) return;

        if (player.isFocusing()) {
            player.setFocusing(false);
            focusTimer = 0f;
        }

        player.setHealth(player.getHealth() - amount);

        if (player.getHealth() <= 0) {
            player.getVelocity().set(0, 0);
            player.getPosition().set(spawnPoint);
            player.setHealth(5);
            player.setSoul(0);
            invincibilityTimer = 1.5f;
        } else {
            invincibilityTimer = 1.0f;
        }
    }

    public void setFocusActive(boolean active) {
        if (player.isDashing() || player.isAttacking() || !player.isOnGround() || player.isDead()) {
            player.setFocusing(false);
            return;
        }

        if (player.getCurrentState() == EntityState.FOCUS_GET || player.getCurrentState() == EntityState.FOCUS_END) {
            return;
        }

        if (active) {
            if (!player.isFocusing()) {
                player.setFocusing(true);
                player.setCurrentState(EntityState.FOCUS_START);
                focusTimer = 0f;
            }
        } else {
            if (player.isFocusing() && (player.getCurrentState() == EntityState.FOCUS_START || player.getCurrentState() == EntityState.FOCUS_LOOPING)) {
                player.setCurrentState(EntityState.FOCUS_END);
                focusTimer = 0f;
            }
            player.setFocusing(false);
        }
    }

    public void setSpikes(ArrayList<Spike> spikes) {
        this.spikes = spikes;
    }

    public void setSpawnPoint(Vector2 spawnPoint) {
        this.spawnPoint.set(spawnPoint);
    }


    private void applyPhysics(Entity entity, float delta) {

        CollisionResult result = PhysicsEngine.resolveMovement(entity, delta, platforms);

        // Process state updates for the player based on what the physics engine encountered
        if (entity == player) {
            // Adjust these to match the exact getter names you wrote inside your CollisionResult class
            this.wallLeft = result.isTouchingLeft() && !player.isOnGround();
            this.wallRight = result.isTouchingRight() && !player.isOnGround();

            boolean scrapingWall = (wallRight && player.isMovingRight()) || (wallLeft && player.isMovingLeft());

            if (scrapingWall && player.getVelocity().y < 0) {
                player.setWallSliding(true);
                player.getVelocity().y = -140f;

                // 💡 FIXED: Inverted these two lines to correctly align your WallSlide sheet orientation!
                if (wallRight) player.setLookingRight(true);
                if (wallLeft)  player.setLookingRight(false);
            } else {
                player.setWallSliding(false);
            }
        }
    }


    private void updateEntityAnimation(Entity entity, float delta) {
        EntityState nextState = entity.getCurrentState();

        if (entity == player && player.isDashing()) {
            nextState = EntityState.DASHING;
            landingTimers.remove(entity);
        }
        else if (entity == player && wallJumpControlLockout > 0) {
            nextState = EntityState.WALL_JUMPING;
            landingTimers.remove(entity);
        }
        else if (entity == player && player.isWallSliding()) {
            nextState = EntityState.WALL_SLIDING;
            landingTimers.remove(entity);
        }
        else if (entity == player && isPogoAttacking) {
            nextState = EntityState.POGO_ATTACKING;
            landingTimers.remove(entity);
        } else if (entity == player && player.isAttacking()) {
            nextState = EntityState.ATTACKING;
            landingTimers.remove(entity);
        } else if (entity == player && player.isFocusing()) {
            nextState = EntityState.FOCUS_LOOPING;
            landingTimers.remove(entity);
        } else if (!entity.isOnGround()) {
            if (entity == player && hasDoubleJumped && entity.getVelocity().y > 0) {
                nextState = EntityState.DOUBLE_JUMPING;
            } else {
                nextState = entity.getVelocity().y > 0 ? EntityState.JUMPING : EntityState.FALLING;
            }
            landingTimers.remove(entity);}
        else {
            if (entity.getVelocity().x != 0) {
                nextState = EntityState.RUNNING;
                landingTimers.remove(entity);
            } else {
                if (entity.getCurrentState() == EntityState.FALLING || entity.getCurrentState() == EntityState.JUMPING) {
                    nextState = EntityState.LANDING;
                    landingTimers.put(entity, 0f);
                } else if (entity.getCurrentState() == EntityState.LANDING) {
                    float currentLandingTime = landingTimers.getOrDefault(entity, 0f) + delta;
                    if (currentLandingTime >= 0.1333f) {
                        nextState = EntityState.IDLE;
                        landingTimers.remove(entity);
                    } else {
                        landingTimers.put(entity, currentLandingTime);
                        nextState = EntityState.LANDING;
                    }
                } else {
                    nextState = EntityState.IDLE;
                }
            }
        }
        entity.setCurrentState(nextState);
    }



    public void update(float delta) {
        if (player.isDead()) return;
        if (dashCooldownTimer > 0) dashCooldownTimer -= delta;
        if (invincibilityTimer > 0) {
            invincibilityTimer -= delta;
        }
        if (wallJumpControlLockout > 0) wallJumpControlLockout -= delta;

        if (spikes != null && invincibilityTimer <= 0f) {
            Rectangle playerBounds = new Rectangle(player.getPosition().x, player.getPosition().y, player.getWidth(), player.getHeight());
            for (Spike spike : spikes) {
                if (playerBounds.overlaps(spike.getBounds())) {
                    damagePlayer(1);
                    break;
                }
            }
        }

        if (isPogoAttacking && player.getVelocity().y <= 0) {
            Rectangle pogoHitbox = new Rectangle(player.getPosition().x, player.getPosition().y - 15f, player.getWidth(), 15f);
            if (platforms != null) {
                for (Rectangle platform : platforms) {
                    if (pogoHitbox.overlaps(platform)) {
                        player.getVelocity().y = 550f;
                        isPogoAttacking = false;

                        hasDashedInAir = false;
                        hasDoubleJumped = false;
                        break;
                    }
                }
            }
        }

        if (player.isDashing()) {
            dashTimer -= delta;
            if (dashTimer <= 0) {
                player.setDashing(false);
            } else {
                player.setVelocityX(player.isLookingRight() ? 800f : -800f);
                player.setVelocityY(0f);
            }
        }
        else if (player.isFocusing() || player.getCurrentState() == EntityState.FOCUS_END || player.getCurrentState() == EntityState.FOCUS_GET) {
            player.setVelocityX(0f);
            player.setVelocityY(0f);
            focusTimer += delta;

            if (player.getCurrentState() == EntityState.FOCUS_START && focusTimer >= 0.10f) {
                player.setCurrentState(EntityState.FOCUS_LOOPING);
                focusTimer = 0f;
            }
            else if (player.getCurrentState() == EntityState.FOCUS_LOOPING) {
                if (focusTimer >= focusRequiredTime) { // Requires 1.5 seconds

                    // 💡 DELEGATION: Ask the player model to handle its own math internally
                    if (player.executeFocusHeal()) {
                        player.setCurrentState(EntityState.FOCUS_GET); // Play success burst
                    } else {
                        player.setCurrentState(EntityState.FOCUS_END);
                        player.setFocusing(false);
                    }
                    focusTimer = 0f;
                }
            }
            else if (player.getCurrentState() == EntityState.FOCUS_GET && focusTimer >= 0.20f) {
                player.setCurrentState(EntityState.FOCUS_END);
                player.setFocusing(false);
                focusTimer = 0f;
            }
            // Phase D: Standing clean reset window exit
            else if (player.getCurrentState() == EntityState.FOCUS_END && focusTimer >= 0.10f) {
                player.setCurrentState(EntityState.IDLE);
                focusTimer = 0f;
            }


        }
        else {
            if (wallJumpControlLockout <= 0) {
                if (player.isMovingLeft()) {
                    player.setVelocityX(-400);
                    player.setLookingRight(false);
                } else if (player.isMovingRight()) {
                    player.setVelocityX(400);
                    player.setLookingRight(true);
                } else {
                    player.setVelocityX(0);
                }
            }

            if (player.isAttacking() || isPogoAttacking) {
                attackTimer -= delta;
                if (attackTimer <= 0) {
                    player.setAttacking(false);
                    isPogoAttacking = false;
                }
            }
        }

        for (Entity entity : entities) {
            applyPhysics(entity, delta);
            updateEntityAnimation(entity, delta);
        }

        if (player.isOnGround()) {
            hasDashedInAir = false;
            hasDoubleJumped = false;
            wallJumpControlLockout = 0f;
        }
    }


}
