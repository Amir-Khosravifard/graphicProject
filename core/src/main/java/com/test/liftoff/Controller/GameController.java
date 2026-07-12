package com.test.liftoff.Controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.test.liftoff.Audio.SoundManager;
import com.test.liftoff.Enums.AudioType;
import com.test.liftoff.Enums.BossSubState;
import com.test.liftoff.Enums.EnemyType;
import com.test.liftoff.Enums.EnemyState;
import com.test.liftoff.Enums.EntityState;
import com.test.liftoff.Enums.LevelType;
import com.test.liftoff.Model.AchievementData;
import com.test.liftoff.Model.Enemy.*;
import com.test.liftoff.Model.Entity.Entity;
import com.test.liftoff.Model.Entity.Player;
import com.test.liftoff.Model.Entity.Spike;
import com.test.liftoff.Model.Entity.Zote;
import com.test.liftoff.Model.SaveData;
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
    private Vector2 currentCheckpoint = new Vector2();
    private ArrayList<Vector2> checkpointPositions = new ArrayList<>();
    private int activeSlot = 1;
    private LevelType levelType = LevelType.CROSSROADS;
    private ArrayList<Rectangle> bossRoomTriggers = new ArrayList<>();
    private final Vector2 bossCameraPosition = new Vector2();
    private boolean bossCameraLocked = false;
    private int deathCount = 0;
    private int killCount = 0;
    private float totalPlayTime = 0f;
    private boolean gameWon = false;
    private boolean cameraShakeTriggered = false;

    private Zote zoteNPC;
    private boolean nearZote = false;
    private boolean dialogueActive = false;
    private int currentDialogueLine = 0;
    private final String[] zoteDialogues = {
        "Preki basto ladd... Do not stand in my light, traveler bug!",
        "I am Zote the Mighty, a grand knight of supreme renown and absolute justice!",
        "Tremble before my nail, 'Life Ender'! It has slain thousands of void beasts!"
    };

    private static class EnemySpawn {
        String name;
        float x, y;

        EnemySpawn(String name, float x, float y) {
            this.name = name;
            this.x = x;
            this.y = y;
        }
    }

    private final ArrayList<EnemySpawn> masterEnemySpawns = new ArrayList<>();
    private boolean wallLeft = false;
    private boolean wallRight = false;
    private boolean isPaused = false;
    private boolean playerHasHitThisSwing = false;
    private boolean godMode = false;

    public GameController(Player player) {
        this.player = player;
        this.entities.add(player);
        this.playerController = new PlayerController(player);
    }

    public void saveCurrentGame() {
        SaveData data = new SaveData();
        data.health = player.getHealth();
        data.soul = player.getSoul();
        data.checkpointX = currentCheckpoint.x;
        data.checkpointY = currentCheckpoint.y;
        data.levelName = levelType.name();
        SaveManager.save(activeSlot, data);
    }

    public void resetStats() {
        this.deathCount = 0;
        this.killCount = 0;
        this.totalPlayTime = 0f;
        this.gameWon = false;
        this.dialogueActive = false;
        this.currentDialogueLine = 0;
    }

    public void setZoteNPC(Zote zote) {
        this.zoteNPC = zote;
    }

    public Zote getZoteNPC() {
        return zoteNPC;
    }

    public boolean isNearZote() {
        return nearZote;
    }

    public boolean isDialogueActive() {
        return dialogueActive;
    }

    public void setDialogueActive(boolean active) {
        this.dialogueActive = active;
    }

    public int getCurrentDialogueLine() {
        return currentDialogueLine;
    }

    public String getCurrentDialogueText() {
        if (currentDialogueLine >= 0 && currentDialogueLine < zoteDialogues.length) {
            return zoteDialogues[currentDialogueLine];
        }
        return "";
    }

    public void advanceDialogue() {
        currentDialogueLine++;
        if (currentDialogueLine >= zoteDialogues.length) {
            dialogueActive = false;
            currentDialogueLine = 0;
        }
    }

    public int getDeathCount() {
        return deathCount;
    }

    public int getKillCount() {
        return killCount;
    }

    public float getTotalPlayTime() {
        return totalPlayTime;
    }

    public boolean isGameWon() {
        return gameWon;
    }

    public boolean checkAndPopCameraShake() {
        if (cameraShakeTriggered) {
            cameraShakeTriggered = false;
            return true;
        }
        return false;
    }

    public void setActiveSlot(int slot) {
        this.activeSlot = slot;
    }

    public int getActiveSlot() {
        return activeSlot;
    }

    public void setLevelType(LevelType type) {
        this.levelType = type;
    }

    public void setBossRoomTriggers(ArrayList<Rectangle> triggers) {
        this.bossRoomTriggers = triggers;
    }

    public void setBossCameraPosition(float x, float y) {
        this.bossCameraPosition.set(x, y);
    }

    public Vector2 getBossCameraPosition() {
        return bossCameraPosition;
    }

    public boolean isBossCameraLocked() {
        return bossCameraLocked;
    }


    public void cheatTeleportToBoss() {
        if (bossRoomTriggers != null && !bossRoomTriggers.isEmpty()) {
            Rectangle bossBox = bossRoomTriggers.get(0);
            player.getPosition().set(bossBox.x, bossBox.y);
        }
    }

    public void cheatToggleNoclip() {
        player.setNoclip(!player.isNoclip());
        if (player.isNoclip()) player.getVelocity().set(0, 0);
    }

    public void cheatEmergencyHeal() {
        player.setHealth(Math.min(5, player.getHealth() + 1));
    }

    public void cheatRefillSoul() {
        player.setSoul(99);
    }

    public void cheatToggleGodMode() {
        this.godMode = !this.godMode;
    }

    public void cheatKillAllEnemies() {
        for (Enemy e : enemyController.getActiveEnemies()) {
            e.takeDamage(999);
        }
    }

    public void recordEnemySpawn(String name, float x, float y) {
        masterEnemySpawns.add(new EnemySpawn(name, x, y));
    }

    public void resetAllEnemies() {
        ArrayList<Enemy> active = new ArrayList<>(enemyController.getActiveEnemies());
        for (Enemy enemy : active) {
            entities.remove(enemy);
        }
        enemyController.getActiveEnemies().clear();
        for (EnemySpawn spawn : masterEnemySpawns) {
            if ("boss".equalsIgnoreCase(spawn.name)) {
                spawnEnemy(new FalseKnight(spawn.x, spawn.y));
            } else if ("crawlid".equalsIgnoreCase(spawn.name)) {
                spawnEnemy(new Crawlid(spawn.x, spawn.y));
            } else if ("hornhead".equalsIgnoreCase(spawn.name)) {
                spawnEnemy(new Hornhead(spawn.x, spawn.y));
            } else if ("mosquito".equalsIgnoreCase(spawn.name)) {
                spawnEnemy(new Mosquito(spawn.x, spawn.y));
            } else if ("crystalGuardian".equalsIgnoreCase(spawn.name)) {
                spawnEnemy(new CrystalGuardian(spawn.x, spawn.y));
            }
        }
    }

    public void updateCheckpoint(Vector2 newPoint) {
        this.currentCheckpoint.set(newPoint);
    }

    public void setCheckpointPositions(ArrayList<Vector2> positions) {
        this.checkpointPositions = positions;
    }

    private void checkEnemyContactDamage() {
        if (player.getHealth() <= 0 || dialogueActive) return;
        if (playerHasHitThisSwing) return;
        Rectangle playerBox = new Rectangle(player.getPosition().x, player.getPosition().y, player.getWidth(), player.getHeight());
        ArrayList<Enemy> enemiesCopy = new ArrayList<>(enemyController.getActiveEnemies());
        for (Enemy enemy : enemiesCopy) {
            if (enemy.isDying() || enemy.isDead()) continue;
            if (enemy instanceof FalseKnight) {
                FalseKnight fk = (FalseKnight) enemy;
                if (fk.getCurrentSubState() == BossSubState.ATTACK_ANTIC ||
                    fk.getCurrentSubState() == BossSubState.ATTACK_ACTIVE ||
                    fk.getCurrentSubState() == BossSubState.ATTACK_RECOVER) {
                    continue;
                }
            }
            Rectangle enemyBox = new Rectangle(enemy.getPosition().x, enemy.getPosition().y, enemy.getWidth(), enemy.getHeight());
            boolean hitByLaser = false;
            if (enemy instanceof CrystalGuardian) {
                CrystalGuardian guardian = (CrystalGuardian) enemy;
                if (guardian.getEnemyState() == EnemyState.ATTACK) {
                    float beamLength = 2500f;
                    float beamHeight = 95f;
                    float laserY = guardian.getPosition().y + (guardian.getHeight() / 2f) - (beamHeight / 2f);
                    float laserX = guardian.isLookingRight() ?
                        (guardian.getPosition().x + guardian.getWidth() - 15f) :
                        (guardian.getPosition().x + 15f - beamLength);
                    Rectangle laserHitbox = new Rectangle(laserX, laserY, beamLength, beamHeight);
                    if (playerBox.overlaps(laserHitbox)) {
                        hitByLaser = true;
                    }
                }
            }
            if (playerBox.overlaps(enemyBox) || hitByLaser) {
                boolean playerDied = damagePlayer(enemy.getContactDamage());
                if (playerDied) break;
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
        if (player.isDead() || isPaused || gameWon) return;
        totalPlayTime += delta;

        if (zoteNPC != null) {
            float distance = Vector2.dst(player.getPosition().x, player.getPosition().y, zoteNPC.getPosition().x, zoteNPC.getPosition().y);
            nearZote = (distance <= 150f);
            if (!nearZote && dialogueActive) {
                dialogueActive = false;
            }
        }

        if (dialogueActive) {
            player.getVelocity().set(0, 0);
            player.setCurrentState(EntityState.IDLE);
            enemyController.updateEnemies(delta, player, platforms, spikes);
        } else {
            playerController.updateTimersAndMovement(delta);
            enemyController.updateEnemies(delta, player, platforms, spikes);
        }

        Rectangle playerBounds = new Rectangle(player.getPosition().x, player.getPosition().y, player.getWidth(), player.getHeight());
        if (!bossCameraLocked && bossRoomTriggers != null) {
            for (Rectangle trigger : bossRoomTriggers) {
                if (playerBounds.overlaps(trigger)) {
                    bossCameraLocked = true;
                    break;
                }
            }
        }
        if (spikes != null && playerController.invincibilityTimer <= 0f && !dialogueActive) {
            for (Spike spike : spikes) {
                if (playerBounds.overlaps(spike.getBounds())) {
                    boolean playerDied = damagePlayer(1);
                    if (!playerDied) {
                        player.getPosition().set(currentCheckpoint);
                        player.getVelocity().set(0, 0);
                        resetAllEnemies();
                    }
                    break;
                }
            }
        }
        if (checkpointPositions != null && !dialogueActive) {
            float playerCenterX = player.getPosition().x + player.getWidth() / 2f;
            for (Vector2 pt : checkpointPositions) {
                float distY = Math.abs(player.getPosition().y - pt.y);
                if (distY < 180f) {
                    if (playerCenterX >= pt.x && player.getPosition().x - pt.x < 150f) {
                        currentCheckpoint.set(pt.x, pt.y);
                    }
                }
            }
        }

        boolean roomBossPresent = false;
        for (int i = enemyController.getActiveEnemies().size() - 1; i >= 0; i--) {
            Enemy e = enemyController.getActiveEnemies().get(i);
            if (e instanceof FalseKnight) {
                FalseKnight fk = (FalseKnight) e;
                if (fk.getCurrentSubState() == BossSubState.ATTACK_ACTIVE) {
                    cameraShakeTriggered = true;
                }
            }
            if (e.isDead()) {
                AchievementData achievements = AchievementManager.load();
                if (e instanceof FalseKnight) {
                    gameWon = true;
                    achievements.defeat_false_knight = true;
                    achievements.killed_false_knight = true;
                    if (totalPlayTime <= 300f) {
                        achievements.speedrun = true;
                    }
                    if (achievements.killed_crystal_guardian) {
                        achievements.completion = true;
                    }
                } else if (e instanceof CrystalGuardian) {
                    achievements.killed_crystal_guardian = true;
                    if (achievements.killed_false_knight) {
                        achievements.completion = true;
                    }
                } else if (e instanceof Crawlid) {
                    achievements.killed_crawlid = true;
                } else if (e instanceof Hornhead) {
                    achievements.killed_hornhead = true;
                } else if (e instanceof Mosquito) {
                    achievements.killed_mosquito = true;
                }
                if (achievements.killed_false_knight &&
                    achievements.killed_crystal_guardian &&
                    achievements.killed_crawlid &&
                    achievements.killed_hornhead &&
                    achievements.killed_mosquito) {
                    achievements.true_hunter = true;
                }
                AchievementManager.save();
                enemyController.getActiveEnemies().remove(i);
                killCount++;
                continue;
            }
            if ((e instanceof FalseKnight || e instanceof CrystalGuardian) && !e.isDying()) {
                roomBossPresent = true;
            }
        }
        if (bossCameraLocked && !roomBossPresent) {
            bossCameraLocked = false;
        }

        for (Entity entity : entities) {
            applyPhysics(entity, delta);
        }
        if (!player.isAttacking() && !player.isPogoAttacking()) {
            playerHasHitThisSwing = false;
        }
        checkPlayerAttackCollision();
        checkEnemyContactDamage();
        handleFalseKnightDamage(delta);
        handleFalseKnightShockwaves(delta);
        if (player.isOnGround()) {
            playerController.resetGroundState();
        }
    }

    public boolean damagePlayer(int amount) {
        if (player.isDead() || playerController.invincibilityTimer > 0f || godMode || dialogueActive) return false;
        if (player.isFocusing()) {
            player.setFocusing(false);
            playerController.focusTimer = 0f;
            player.setCurrentState(EntityState.IDLE);
        }
        player.setHealth(player.getHealth() - amount);
        playerController.invincibilityTimer = 1.0f;
        SoundManager.playSound(AudioType.DAMAGE);
        playerController.knockbackTimer = 0.15f;
        player.getVelocity().y = 280f;
        if (player.getHealth() <= 0) {
            deathCount++;
            bossCameraLocked = false;
            dialogueActive = false;
            player.getVelocity().set(0, 0);
            player.getPosition().set(spawnPoint);
            currentCheckpoint.set(spawnPoint);
            player.setHealth(5);
            player.setSoul(0);
            playerController.knockbackTimer = 0f;
            resetAllEnemies();
            return true;
        }
        return false;
    }

    private void handleFalseKnightDamage(float delta) {
        if (player.getHealth() <= 0 || dialogueActive) return;
        ArrayList<Entity> entitiesCopy = new ArrayList<>(entities);
        for (Entity entity : entitiesCopy) {
            if (entity instanceof FalseKnight) {
                FalseKnight fk = (FalseKnight) entity;
                if (fk.getCurrentSubState() == BossSubState.ATTACK_ACTIVE) {
                    Rectangle hammerHitbox = getFalseKnightHammerBox(fk);
                    if (hammerHitbox == null) continue;
                    Rectangle playerBox = new Rectangle(player.getPosition().x, player.getPosition().y, player.getWidth(), player.getHeight());
                    if (hammerHitbox.overlaps(playerBox)) {
                        boolean playerDied = damagePlayer(1);
                        if (playerDied) break;
                    }
                }
            }
        }
    }

    public Rectangle getFalseKnightHammerBox(FalseKnight fk) {
        if (fk.getCurrentSubState() != BossSubState.ATTACK_ACTIVE) return null;
        float hammerWidth = 120f;
        float hammerHeight = 160f;
        float hammerX;
        if (fk.isLookingRight()) {
            hammerX = fk.getPosition().x + fk.getWidth() + 45f;
        } else {
            hammerX = fk.getPosition().x - hammerWidth - 45f;
        }
        return new Rectangle(hammerX, fk.getPosition().y - 150f, hammerWidth, hammerHeight);
    }

    private void handleFalseKnightShockwaves(float delta) {
        if (player.getHealth() <= 0 || dialogueActive) return;
        ArrayList<Entity> entitiesCopy = new ArrayList<>(entities);
        for (Entity entity : entitiesCopy) {
            if (entity instanceof FalseKnight) {
                FalseKnight fk = (FalseKnight) entity;
                if (fk.getCurrentSubState() == BossSubState.ATTACK_ACTIVE &&
                    fk.getSelectedMoveQueue() == FalseKnight.MOVE_HEAVY_SLAM_JUMP) {
                    Rectangle shockwaveBox = new Rectangle(
                        fk.getPosition().x - 120f,
                        fk.getPosition().y - 150f,
                        fk.getWidth() + 240f,
                        40f
                    );
                    Rectangle playerBounds = new Rectangle(player.getPosition().x, player.getPosition().y, player.getWidth(), player.getHeight());
                    if (shockwaveBox.overlaps(playerBounds)) {
                        boolean playerDied = damagePlayer(2);
                        if (playerDied) break;
                    }
                }
            }
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
                if (wallLeft) player.setLookingRight(false);
            } else {
                player.setWallSliding(false);
            }
        }
    }

    public void jumpPlayer() {
        if (player.isDead() || player.isFocusing() || dialogueActive) return;
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

    public void cutPlayerJump() {
        if (player.getVelocity().y > 0 && !player.isOnGround() && !player.isWallSliding()) {
            player.getVelocity().y *= 0.4f;
        }
    }

    public void handlePlayerDash() {
        if (player.isFocusing() || player.isAttacking() || player.isDead() || dialogueActive) return;
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
        if ((!player.isAttacking() && !player.isPogoAttacking()) || playerHasHitThisSwing || dialogueActive) {
            return;
        }
        Rectangle attackBox;
        float attackRange = 45f;
        if (player.isPogoAttacking()) {
            attackBox = new Rectangle(player.getPosition().x, player.getPosition().y - 25f, player.getWidth(), 25f);
        } else if (player.isLookingRight()) {
            attackBox = new Rectangle(player.getPosition().x + player.getWidth(), player.getPosition().y + 5f, attackRange, player.getHeight() - 10f);
        } else {
            attackBox = new Rectangle(player.getPosition().x - attackRange, player.getPosition().y + 5f, attackRange, player.getHeight() - 10f);
        }
        ArrayList<Enemy> enemiesCopy = new ArrayList<>(enemyController.getActiveEnemies());
        for (Enemy enemy : enemiesCopy) {
            if (enemy.isDying() || enemy.isDead()) continue;
            Rectangle enemyBox = new Rectangle(enemy.getPosition().x, enemy.getPosition().y, enemy.getWidth(), enemy.getHeight());
            if (attackBox.overlaps(enemyBox)) {
                enemy.takeDamage(1);
                player.addSoul(11);
                playerHasHitThisSwing = true;
                SoundManager.playSound(AudioType.SFX_ENEMY_HIT);
                SoundManager.playSound(AudioType.SFX_SOUL_GAIN);
                if (!(enemy instanceof FalseKnight) && !(enemy instanceof CrystalGuardian)) {
                    float kbDir = player.isLookingRight() ? 1f : -1f;
                    enemy.getVelocity().x = 350f * kbDir;
                    enemy.getVelocity().y = 150f;
                }
                if (player.isPogoAttacking()) {
                    player.getVelocity().y = 550f;
                    playerController.hasDoubleJumped = false;
                    playerController.hasDashedInAir = false;
                }
                break;
            }
        }
    }

    public void handlePlayerAttack() {
        if (player.isDashing() || player.isFocusing() || player.isDead() || dialogueActive) return;
        if (!player.isAttacking() && !player.isPogoAttacking()) {
            if (!player.isOnGround() && player.isLookingDown()) {
                player.setPogoAttacking(true);
            } else {
                player.setAttacking(true);
            }
            playerController.attackTimer = playerController.attackDuration;
            playerHasHitThisSwing = false;
            SoundManager.playSound(AudioType.SFX_SLASH);
        }
    }

    public void setFocusActive(boolean active) {
        if (player.isDashing() || player.isAttacking() || !player.isOnGround() || player.isDead() || dialogueActive) {
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

    public Rectangle getAttackBox() {
        if (!player.isAttacking() && !player.isPogoAttacking()) {
            return null;
        }
        float attackRange = 45f;
        if (player.isPogoAttacking()) {
            return new Rectangle(player.getPosition().x, player.getPosition().y - 25f, player.getWidth(), 25f);
        } else if (player.isLookingRight()) {
            return new Rectangle(player.getPosition().x + player.getWidth(), player.getPosition().y + 5f, attackRange, player.getHeight() - 10f);
        } else {
            return new Rectangle(player.getPosition().x - attackRange, player.getPosition().y + 5f, attackRange, player.getHeight() - 10f);
        }
    }

    public Player getPlayer() {
        return player;
    }

    public ArrayList<Entity> getEntities() {
        return entities;
    }

    public void setPlatforms(ArrayList<Rectangle> platforms) {
        this.platforms = platforms;
    }

    public void setSpikes(ArrayList<Spike> spikes) {
        this.spikes = spikes;
    }

    public void setSpawnPoint(Vector2 FriendSpawn) {
        this.spawnPoint.set(FriendSpawn);
    }

    public void setMovingRight(boolean movingRight) {
        player.setMovingRight(movingRight);
    }

    public void setMovingLeft(boolean movingLeft) {
        player.setMovingLeft(movingLeft);
    }

    public void setLookingDown(boolean lookingDown) {
        player.setLookingDown(lookingDown);
    }

    public void setMovingUp(boolean up) {
        player.setMovingUp(up);
    }

    public Vector2 getPlayerPosition() {
        return player.getPosition();
    }
}
