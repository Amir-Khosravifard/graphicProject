package com.test.liftoff.Controller;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.test.liftoff.Enums.EntityState;
import com.test.liftoff.Model.Entity.Entity;
import com.test.liftoff.Model.Entity.Player;

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
    public void jumpPlayer() {
        if (player.isDead() || player.isFocusing()) return;

        if (player.isOnGround()) {
            player.getVelocity().y = 500f;
        } else if (!hasDoubleJumped) {
            player.getVelocity().y = 520f;
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

    public void setFocusActive(boolean active) {
        if (player.isDashing() || player.isAttacking() || !player.isOnGround() || player.isDead()) {
            player.setFocusing(false);
            return;
        }

        if (player.getCurrentState() == EntityState.FOCUS_GET || player.getCurrentState() == EntityState.FOCUS_END) {
            return;
        }

        if (active) {
            player.setFocusing(true);
        } else {
            if (player.isFocusing() && (player.getCurrentState() == EntityState.FOCUS_START || player.getCurrentState() == EntityState.FOCUS_LOOPING)) {
                player.setCurrentState(EntityState.FOCUS_END);
                focusTimer = 0f;
            }
            player.setFocusing(false);
        }
    }


    private void applyPhysics(Entity entity, float delta) {

        boolean ignoreGravity = (entity == player && player.isDashing());


        if(!entity.isOnGround() && !ignoreGravity) {
            entity.setVelocityY(entity.getVelocity().y - gravity * delta);
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
                        if (entity.getVelocity().x > 0) entity.setPositionX(platform.x - entity.getWidth());
                        else if (entity.getVelocity().x < 0) entity.setPositionX(platform.x + platform.width);
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

        entity.setOnGround(false);
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

        if (isPogoAttacking && player.getVelocity().y <= 0) {
            Rectangle pogoHitbox = new Rectangle(player.getPosition().x, player.getPosition().y - 15f, player.getWidth(), 15f);
            if (platforms != null) {
                for (Rectangle platform : platforms) {
                    if (pogoHitbox.overlaps(platform)) {
                        player.getVelocity().y = 550f;
                        isPogoAttacking = false;
                        player.setAttacking(false);

                        hasDashedInAir = false;
                        hasDoubleJumped = false;
                        break;
                    }
                }
            }
        }
    }


    private void updateEntityAnimation(Entity entity, float delta) {
        EntityState nextState = entity.getCurrentState();

        if (entity == player && player.isDashing()) {
            nextState = EntityState.DASHING;
            landingTimers.remove(entity);

        } else if (entity == player && isPogoAttacking) {
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
        else if (player.isFocusing()) {
            player.setVelocityX(0f);
            player.setVelocityY(0f);
            focusTimer += delta;
            if (focusTimer >= focusRequiredTime) {
                if (player.getSoul() >= 33 && player.getHealth() < 5) {
                    player.setSoul(player.getSoul() - 33);
                    player.setHealth(player.getHealth() + 1);
                }
                focusTimer = 0f;
            }
        }
        else {
            if (player.isMovingLeft()) player.setVelocityX(-400);
            else if (player.isMovingRight()) player.setVelocityX(400);
            else player.setVelocityX(0);

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
        }
    }


}
