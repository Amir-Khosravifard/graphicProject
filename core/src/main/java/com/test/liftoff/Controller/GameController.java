package com.test.liftoff.Controller;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.test.liftoff.Enums.EnemyType;
import com.test.liftoff.Enums.EntityState;
import com.test.liftoff.Model.Enemy.*;
import com.test.liftoff.Model.Entity.Entity;
import com.test.liftoff.Model.Entity.Player;
import com.test.liftoff.Model.Entity.Spike;
import com.test.liftoff.Physics.CollisionResult;
import com.test.liftoff.Physics.PhysicsEngine;

import java.util.ArrayList;

public class GameController {
    private final Player player;
    private final PlayerController playerController;
    private final EnemyController enemyController = new EnemyController();

    private ArrayList<Entity> entities = new ArrayList<>();
    private ArrayList<Rectangle> platforms = new ArrayList<>();
    private ArrayList<Spike> spikes = new ArrayList<>();
    private Vector2 spawnPoint = new Vector2();

    private boolean wallLeft = false;
    private boolean wallRight = false;
    private boolean isPaused = false;

    // 💡 ADDED: Prevents a single swing from hitting an enemy multiple times per animation cycle
    private boolean playerHasHitThisSwing = false;

    public GameController(Player player) {
        this.player = player;
        this.entities.add(player);
        this.playerController = new PlayerController(player);
        // Clear old hardcoded rows from here!
    }

    private void checkEnemyContactDamage() {
        Rectangle playerBox = new Rectangle(player.getPosition().x, player.getPosition().y, player.getWidth(), player.getHeight());

        for (Enemy enemy : enemyController.getActiveEnemies()) {
            // 💡 Ignore contact: Player safely passes through corpses seamlessly
            if (enemy.isDying()) continue;

            Rectangle enemyBox = new Rectangle(enemy.getPosition().x, enemy.getPosition().y, enemy.getWidth(), enemy.getHeight());

            boolean hitByLaser = false;
            // Handle the Crystal Guardian's enraged laser boundary check
            if (enemy instanceof CrystalGuardian) {
                CrystalGuardian guardian = (CrystalGuardian) enemy;
                if (guardian.isEnraged) {
                    hitByLaser = true;
                    guardian.isEnraged = false; // Reset burst flag
                }
            }

            // If the player touches the enemy's body box or gets clipped by a boss laser
            if (playerBox.overlaps(enemyBox) || hitByLaser) {
                damagePlayer(enemy.getContactDamage());
            }
        }
    }


    public boolean isPaused() {
        return isPaused;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    public void spawnEnemy(Enemy enemy) {
        enemyController.addEnemy(enemy);
        this.entities.add(enemy);
    }

    public float getInvincibilityTimer() {
        return playerController.invincibilityTimer;
    }

    public void update(float delta) {
        if (player.isDead() || isPaused) return;

        // 1. Environmental hazard scanning
        if (spikes != null && playerController.invincibilityTimer <= 0f) {
            Rectangle playerBounds = new Rectangle(player.getPosition().x, player.getPosition().y, player.getWidth(), player.getHeight());
            for (Spike spike : spikes) {
                if (playerBounds.overlaps(spike.getBounds())) {
                    damagePlayer(1);
                    break;
                }
            }
        }

        // 2. Clock ticks and independent AI processing
        playerController.updateTimersAndMovement(delta);
        enemyController.updateEnemies(delta, player, platforms);

        // 3. Entity master list cleanups
        for (int i = entities.size() - 1; i >= 0; i--) {
            Entity entity = entities.get(i);
            if (entity instanceof Enemy && entity.isDead()) {
                entities.remove(i);
            }
        }

        // 4. Rigid physics calculations
        for (Entity entity : entities) {
            applyPhysics(entity, delta);
        }

        // 5. Reset input latch triggers when weapon animations finish
        if (!player.isAttacking() && !player.isPogoAttacking()) {
            playerHasHitThisSwing = false;
        }

        // 6. Extracted Combat and Impact Processing Loops
        checkPlayerAttackCollision(); // 💡 Cleanly separated right here!
        checkEnemyContactDamage();

        if (player.isOnGround()) {
            playerController.resetGroundState();
        }
    }

    public void damagePlayer(int amount) {
        if (player.isDead() || playerController.invincibilityTimer > 0f) return;

        if (player.isFocusing()) {
            player.setFocusing(false);
            playerController.focusTimer = 0f;
            player.setCurrentState(EntityState.IDLE);
        }

        player.setHealth(player.getHealth() - amount);
        playerController.invincibilityTimer = 1.0f;

        // =========================================================================
        // 💡 KNOCKBACK ENGINE: Push player back vertically and horizontally
        // =========================================================================
        playerController.knockbackTimer = 0.15f; // Active recoil lasts for ~5-10 frames
        player.getVelocity().y = 280f;           // Slight vertical lift off the ground

        if (player.getHealth() <= 0) {
            player.getVelocity().set(0, 0);
            player.getPosition().set(spawnPoint);
            player.setHealth(5);
            player.setSoul(0);
            playerController.knockbackTimer = 0f; // Clear state on total death reset
        }
    }

    private void applyPhysics(Entity entity, float delta) {
        CollisionResult result = PhysicsEngine.resolveMovement(entity, delta, platforms);
        if (entity == player) {
            this.wallLeft = result.isTouchingLeft() && !player.isOnGround();
            this.wallRight = result.isTouchingRight() && !player.isOnGround();
            boolean scrapingWall = (wallRight && player.isMovingRight()) || (wallLeft && player.isMovingLeft());
            if (scrapingWall && player.getVelocity().y < 0) {
                player.setWallSliding(true);
                player.getVelocity().y = -140f;
                if (wallRight) player.setLookingRight(true);
                if (wallLeft)  player.setLookingRight(false);
            } else {
                player.setWallSliding(false);
            }
        }
    }

    public void jumpPlayer() {
        if (player.isDead() || player.isFocusing()) return;
        if (player.isOnGround()) {
            player.getVelocity().y = 500f;
        } else if (player.isWallSliding() || wallLeft || wallRight) {
            player.getVelocity().y = 480f;
            playerController.wallJumpControlLockout = 0.15f;
            if (wallRight) {
                player.getVelocity().x = -420f;
                player.setLookingRight(false);
            } else if (wallLeft) {
                player.getVelocity().x = 420f;
                player.setLookingRight(true);
            }
            playerController.hasDoubleJumped = false;
            playerController.hasDashedInAir = false;
            player.setWallSliding(false);
        } else if (!playerController.hasDoubleJumped) {
            player.getVelocity().y = 520f;
            playerController.hasDoubleJumped = true;
        }
    }

    public void handlePlayerDash() {
        if (player.isFocusing() || player.isAttacking() || player.isDead()) return;
        if (playerController.dashCooldownTimer <= 0f && (!playerController.hasDashedInAir || player.isOnGround())) {
            player.setDashing(true);
            playerController.dashTimer = 0.15f;
            playerController.dashCooldownTimer = 0.6f;
            if (!player.isOnGround()) playerController.hasDashedInAir = true;
            player.getVelocity().x = player.isLookingRight() ? 800f : -800f;
            player.getVelocity().y = 0f;
        }
    }

    private void checkPlayerAttackCollision() {
        if ((!player.isAttacking() && !player.isPogoAttacking()) || playerHasHitThisSwing) {
            return;
        }

        Rectangle attackBox;
        float attackRange = 45f; // 💡 Buffed this a bit based on your reach preferences!

        if (player.isPogoAttacking()) {
            // Downward slash box under feet
            attackBox = new Rectangle(player.getPosition().x, player.getPosition().y - 25f, player.getWidth(), 25f);
        } else if (player.isLookingRight()) {
            // Forward slash box to the right (lowered and widened to comfortably hit low targets)
            attackBox = new Rectangle(player.getPosition().x + player.getWidth(), player.getPosition().y + 5f, attackRange, player.getHeight() - 10f);
        } else {
            // Forward slash box to the left
            attackBox = new Rectangle(player.getPosition().x - attackRange, player.getPosition().y + 5f, attackRange, player.getHeight() - 10f);
        }

        for (Enemy enemy : enemyController.getActiveEnemies()) {
            if (enemy.isDying()) continue;

            Rectangle enemyBox = new Rectangle(enemy.getPosition().x, enemy.getPosition().y, enemy.getWidth(), enemy.getHeight());

            if (attackBox.overlaps(enemyBox)) {
                enemy.takeDamage(1);
                player.addSoul(11);
                playerHasHitThisSwing = true;

                if (player.isPogoAttacking()) {
                    player.getVelocity().y = 550f;
                    player.setPogoAttacking(false);
                    player.setAttacking(false);
                    playerController.hasDoubleJumped = false;
                    playerController.hasDashedInAir = false;
                }
                break; // Stop calculating hits for this frame loop once an overlap registers
            }
        }
    }

    public void handlePlayerAttack() {
        if (player.isDashing() || player.isFocusing() || player.isDead()) return;

        if (!player.isAttacking() && !player.isPogoAttacking()) {
            // =========================================================================
            // 💡 THE FIX: If airborne and holding DOWN, initiate a pogo attack state!
            // =========================================================================
            if (!player.isOnGround() && player.isLookingDown()) {
                player.setPogoAttacking(true);
            } else {
                player.setAttacking(true);
            }

            playerController.attackTimer = playerController.attackDuration;
            playerHasHitThisSwing = false;
        }
    }

    public void setFocusActive(boolean active) {
        if (player.isDashing() || player.isAttacking() || !player.isOnGround() || player.isDead()) {
            player.setFocusing(false);
            return;
        }
        if (active && !player.isFocusing()) {
            player.setFocusing(true);
            player.setCurrentState(EntityState.FOCUS_START);
            playerController.focusTimer = 0f;
        } else if (!active && player.isFocusing()) {
            if (player.getCurrentState() == EntityState.FOCUS_START || player.getCurrentState() == EntityState.FOCUS_LOOPING) {
                player.setCurrentState(EntityState.FOCUS_END);
                playerController.focusTimer = 0f;
            }
            player.setFocusing(false);
        }
    }

    // 💡 ADD THIS METHOD ANYWHERE INSIDE GAMECONTROLLER
    public Rectangle getAttackBox() {
        if (!player.isAttacking() && !player.isPogoAttacking()) {
            return null;
        }

        float attackRange = 45f; // Matches your current reach setting

        if (player.isPogoAttacking()) {
            return new Rectangle(player.getPosition().x, player.getPosition().y - 25f, player.getWidth(), 25f);
        } else if (player.isLookingRight()) {
            return new Rectangle(player.getPosition().x + player.getWidth(), player.getPosition().y + 5f, attackRange, player.getHeight() - 10f);
        } else {
            return new Rectangle(player.getPosition().x - attackRange, player.getPosition().y + 5f, attackRange, player.getHeight() - 10f);
        }
    }

    public Player getPlayer() { return player; }
    public ArrayList<Entity> getEntities() { return entities; }
    public void setPlatforms(ArrayList<Rectangle> platforms) { this.platforms = platforms; }
    public void setSpikes(ArrayList<Spike> spikes) { this.spikes = spikes; }
    public void setSpawnPoint(Vector2 spawnPoint) { this.spawnPoint.set(spawnPoint); }
    public void setMovingRight(boolean movingRight) { player.setMovingRight(movingRight); }
    public void setMovingLeft(boolean movingLeft) { player.setMovingLeft(movingLeft); }
    public void setLookingDown(boolean lookingDown) { player.setLookingDown(lookingDown); }
    public Vector2 getPlayerPosition() { return player.getPosition(); }
}
