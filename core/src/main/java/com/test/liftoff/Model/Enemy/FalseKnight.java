package com.test.liftoff.Model.Enemy;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.test.liftoff.Enums.AnimationType;
import com.test.liftoff.Enums.BossSubState;
import com.test.liftoff.Enums.EnemyState;
import com.test.liftoff.Enums.EntityState;
import com.test.liftoff.Model.Entity.Player;

public class FalseKnight extends Enemy {
    public static final int MOVE_MACE_SLAM = 1;
    public static final int MOVE_CHARGE_RUN = 2;
    public static final int MOVE_OFFENSIVE_LEAP = 3;
    public static final int MOVE_DEFENSIVE_LEAP = 4;
    public static final int MOVE_HEAVY_SLAM_JUMP = 5;

    private BossSubState currentSubState = BossSubState.IDLE;
    private int currentPhase = 1;
    private int lastExecutedMove = -1;
    private int selectedMoveQueue = -1;
    private boolean hasBeenStunnedThisFight = false;
    private boolean isVulnerableStunned = false;

    private float stateTimer = 0f;
    private float recentDamageWindow = 0f;
    private int rapidDamageCount = 0;
    private float speedMultiplier = 1.0f;
    private float targetJumpDirection = 0f;

    public FalseKnight(float x, float y) {
        super(x, y, 200, 220f, 30, 90f);
        setEnemyState(EnemyState.PATROL);
    }

    @Override
    public void updateAI(float delta, Player player, boolean atEdgeOrWall) {
        stateTimer += delta;
        if (recentDamageWindow > 0) recentDamageWindow -= delta;
        else rapidDamageCount = 0;

        if (getHealth() <= 15 && !hasBeenStunnedThisFight) {
            triggerStunSequence();
        }

        if (isVulnerableStunned) {
            handleStunLoop(player);
            return;
        }

        if (!isOnGround() && currentSubState != BossSubState.JUMP_AIRBORNE && currentSubState != BossSubState.JUMP_ANTIC) {
            currentSubState = BossSubState.JUMP_AIRBORNE;
        }

        float playerCenter = player.getPosition().x + player.getWidth() / 2f;
        float bossCenter = getPosition().x + getWidth() / 2f;
        float horizontalDifference = playerCenter - bossCenter;

        float verticalDistance = Math.abs(player.getPosition().y - (getPosition().y - 150f));
        float directDistance = Vector2.dst(player.getPosition().x, player.getPosition().y, getPosition().x, getPosition().y - 150f);

        if (currentSubState == BossSubState.IDLE || currentSubState == BossSubState.TURN || currentSubState == BossSubState.RUN_ACTIVE) {
            if (directDistance > 1000f || verticalDistance > 350f) {
                currentSubState = BossSubState.IDLE;
                setCurrentState(EntityState.IDLE);
                selectedMoveQueue = -1;
                getVelocity().x = 0;
                return;
            }
        }

        getVelocity().x = 0;

        switch (currentSubState) {
            case IDLE:
                setCurrentState(EntityState.IDLE);
                if (Math.abs(horizontalDifference) > 50f && isLookingRight() != (horizontalDifference > 0)) {
                    currentSubState = BossSubState.TURN;
                    stateTimer = 0f;
                    setLookingRight(horizontalDifference > 0);
                } else if (stateTimer >= (0.8f / speedMultiplier)) {
                    selectNextAction(horizontalDifference, player);
                }
                break;
            case TURN:
                setCurrentState(EntityState.IDLE);
                if (stateTimer >= 0.10f) {
                    currentSubState = BossSubState.IDLE;
                    stateTimer = 0f;
                }
                break;
            case RUN_ANTIC:
                setCurrentState(EntityState.ATTACKING);
                if (stateTimer >= (0.12f / speedMultiplier)) {
                    currentSubState = BossSubState.RUN_ACTIVE;
                    stateTimer = 0f;
                }
                break;
            case RUN_ACTIVE:
                setCurrentState(EntityState.RUNNING);
                float chargeDir = isLookingRight() ? 1f : -1f;
                getVelocity().x = speed * 2.8f * speedMultiplier * chargeDir;
                if (atEdgeOrWall || stateTimer >= 1.5f) {
                    currentSubState = BossSubState.IDLE;
                    stateTimer = 0f;
                }
                break;
            case ATTACK_ANTIC:
                setCurrentState(EntityState.ATTACKING);
                if (stateTimer >= (0.16f / speedMultiplier)) {
                    currentSubState = BossSubState.ATTACK_ACTIVE;
                    stateTimer = 0f;
                }
                break;
            case ATTACK_ACTIVE:
                setCurrentState(EntityState.ATTACKING);
                if (stateTimer >= (0.20f / speedMultiplier)) {
                    currentSubState = BossSubState.ATTACK_RECOVER;
                    stateTimer = 0f;
                }
                break;
            case ATTACK_RECOVER:
                setCurrentState(EntityState.ATTACKING);
                if (stateTimer >= (0.13f / speedMultiplier)) {
                    currentSubState = BossSubState.IDLE;
                    stateTimer = 0f;
                    selectedMoveQueue = -1;
                }
                break;
            case JUMP_ANTIC:
                setCurrentState(EntityState.JUMPING);
                if (stateTimer >= 0.13f) {
                    currentSubState = BossSubState.JUMP_AIRBORNE;
                    stateTimer = 0f;
                    setOnGround(false);
                    if (selectedMoveQueue == MOVE_DEFENSIVE_LEAP) {
                        getVelocity().y = 400f;
                        getVelocity().x = (isLookingRight() ? -350f : 350f) * speedMultiplier;
                    } else if (selectedMoveQueue == MOVE_HEAVY_SLAM_JUMP) {
                        getVelocity().y = 650f;
                        getVelocity().x = (targetJumpDirection > 0 ? 150f : -150f) * speedMultiplier;
                    } else {
                        getVelocity().y = 550f;
                        getVelocity().x = (targetJumpDirection > 0 ? 300f : -300f) * speedMultiplier;
                    }
                }
                break;
            case JUMP_AIRBORNE:
                setCurrentState(EntityState.JUMPING);
                if (selectedMoveQueue == MOVE_DEFENSIVE_LEAP) {
                    getVelocity().x = (isLookingRight() ? -350f : 350f) * speedMultiplier;
                } else if (selectedMoveQueue == MOVE_HEAVY_SLAM_JUMP) {
                    getVelocity().x = (targetJumpDirection > 0 ? 150f : -150f) * speedMultiplier;
                } else {
                    getVelocity().x = (targetJumpDirection > 0 ? 300f : -300f) * speedMultiplier;
                }
                if (isOnGround()) {
                    getVelocity().set(0, 0);
                    stateTimer = 0f;
                    if (selectedMoveQueue == MOVE_OFFENSIVE_LEAP || selectedMoveQueue == MOVE_HEAVY_SLAM_JUMP) {
                        currentSubState = BossSubState.ATTACK_ACTIVE;
                    } else {
                        currentSubState = BossSubState.JUMP_LAND;
                        selectedMoveQueue = -1;
                    }
                }
                break;
            case JUMP_LAND:
                setCurrentState(EntityState.LANDING);
                if (stateTimer >= 0.25f) {
                    currentSubState = BossSubState.IDLE;
                    stateTimer = 0f;
                    selectedMoveQueue = -1;
                }
                break;
        }
    }

    public BossSubState getCurrentSubState() {
        return currentSubState;
    }

    public int getSelectedMoveQueue() {
        return selectedMoveQueue;
    }

    @Override
    public float getSpriteOffsetY(float frameHeight) {
        return (frameHeight - this.height) / 2f - 150;
    }

    @Override
    public float getSpriteOffsetX(float frameWidth) {
        return (frameWidth - this.height) / 2f;
    }

    private void selectNextAction(float horizontalDifference, Player player) {
        stateTimer = 0f;
        float distance = Math.abs(horizontalDifference);
        setLookingRight(horizontalDifference > 0);
        if (rapidDamageCount >= 3 && lastExecutedMove != MOVE_DEFENSIVE_LEAP) {
            selectedMoveQueue = MOVE_DEFENSIVE_LEAP;
            currentSubState = BossSubState.JUMP_ANTIC;
            rapidDamageCount = 0;
            return;
        }
        float weightSlam = 20, weightCharge = 20, weightOffLeap = 20, weightHeavySlam = 20;
        if (distance < 160f) {
            weightSlam = 65;
            weightCharge = 5;
            weightOffLeap = 10;
        } else {
            weightCharge = 50;
            weightOffLeap = 40;
            weightSlam = 5;
        }
        if (currentPhase < 2) weightHeavySlam = 0;
        else weightHeavySlam = 30;
        if (lastExecutedMove == MOVE_MACE_SLAM) weightSlam = 0;
        if (lastExecutedMove == MOVE_CHARGE_RUN) weightCharge = 0;
        if (lastExecutedMove == MOVE_OFFENSIVE_LEAP) weightOffLeap = 0;
        if (lastExecutedMove == MOVE_HEAVY_SLAM_JUMP) weightHeavySlam = 0;
        float totalWeight = weightSlam + weightCharge + weightOffLeap + weightHeavySlam;
        float rolledChoice = MathUtils.random(0f, totalWeight);
        if (rolledChoice <= weightSlam) {
            lastExecutedMove = MOVE_MACE_SLAM;
            selectedMoveQueue = MOVE_MACE_SLAM;
            currentSubState = BossSubState.ATTACK_ANTIC;
        } else if (rolledChoice <= weightSlam + weightCharge) {
            lastExecutedMove = MOVE_CHARGE_RUN;
            selectedMoveQueue = MOVE_CHARGE_RUN;
            currentSubState = BossSubState.RUN_ANTIC;
        } else if (rolledChoice <= weightSlam + weightCharge + weightOffLeap) {
            lastExecutedMove = MOVE_OFFENSIVE_LEAP;
            selectedMoveQueue = MOVE_OFFENSIVE_LEAP;
            targetJumpDirection = horizontalDifference;
            currentSubState = BossSubState.JUMP_ANTIC;
        } else {
            lastExecutedMove = MOVE_HEAVY_SLAM_JUMP;
            selectedMoveQueue = MOVE_HEAVY_SLAM_JUMP;
            targetJumpDirection = horizontalDifference;
            currentSubState = BossSubState.JUMP_ANTIC;
        }
    }

    @Override
    public void takeDamage(int amount) {
        if (isDead || isDying) return;
        if (!isVulnerableStunned) {
            rapidDamageCount++;
            recentDamageWindow = 1.5f;
            if (rapidDamageCount >= 3 && currentSubState != BossSubState.JUMP_AIRBORNE && currentSubState != BossSubState.JUMP_ANTIC) {
                currentSubState = BossSubState.JUMP_ANTIC;
                selectedMoveQueue = MOVE_DEFENSIVE_LEAP;
                lastExecutedMove = MOVE_DEFENSIVE_LEAP;
                stateTimer = 0f;
                rapidDamageCount = 0;
            }
            super.takeDamage(amount);
            return;
        }
        this.health -= (amount * 2);
        if (this.health <= 0) {
            this.health = 0;
            this.isDying = true;
            this.deathTimer = 2.0f;
            getVelocity().set(0, 0);
        }
    }

    private void triggerStunSequence() {
        hasBeenStunnedThisFight = true;
        isVulnerableStunned = true;
        currentSubState = BossSubState.STUN_START;
        getVelocity().set(0, 0);
        stateTimer = 0f;
    }

    private void handleStunLoop(Player player) {
        if (currentSubState == BossSubState.STUN_START && stateTimer >= 0.5f) {
            currentSubState = BossSubState.STUN_LOOP;
            stateTimer = 0f;
        } else if (currentSubState == BossSubState.STUN_LOOP && stateTimer >= 3.5f) {
            currentSubState = BossSubState.STUN_RECOVER;
            stateTimer = 0f;
        } else if (currentSubState == BossSubState.STUN_RECOVER && stateTimer >= 0.8f) {
            isVulnerableStunned = false;
            currentPhase = 2;
            speedMultiplier = 1.85f;
            currentSubState = BossSubState.IDLE;
            stateTimer = 0f;
        }
    }

    @Override
    public float getWidth() {
        return this.width;
    }

    @Override
    public AnimationType getAnimationType() {
        if (isDying()) return AnimationType.FK_DeathFall;
        switch (currentSubState) {
            case TURN:
                return AnimationType.FK_Turn;
            case RUN_ANTIC:
                return AnimationType.FK_RunAntic;
            case RUN_ACTIVE:
                return AnimationType.FK_Run;
            case ATTACK_ANTIC:
                return AnimationType.FK_AttackAntic;
            case ATTACK_ACTIVE:
                return AnimationType.FK_Attack;
            case ATTACK_RECOVER:
                return AnimationType.FK_AttackRecover;
            case JUMP_ANTIC:
                return AnimationType.FK_JumpAntic;
            case JUMP_AIRBORNE:
                return getVelocity().y > 0 ? AnimationType.FK_Jump : AnimationType.FK_JumpAttack;
            case JUMP_LAND:
                return AnimationType.FK_Land;
            case STUN_START:
                return AnimationType.FK_DeathHit;
            case STUN_LOOP:
                return AnimationType.FK_BodyOpen;
            case STUN_RECOVER:
                return AnimationType.FK_StunRecover;
            case IDLE:
            default:
                return AnimationType.FK_Idle;
        }
    }
}
