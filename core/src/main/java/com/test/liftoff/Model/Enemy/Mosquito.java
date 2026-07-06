package com.test.liftoff.Model.Enemy;

import com.badlogic.gdx.math.Vector2;
import com.test.liftoff.Enums.AnimationType;
import com.test.liftoff.Enums.EnemyState;
import com.test.liftoff.Enums.EntityState;
import com.test.liftoff.Model.Entity.Player;

public class Mosquito extends Enemy{
    public final Vector2 targetPosition = new Vector2();

    public Mosquito(float x, float y) {
        // 💡 TWEAK HITBOX HERE: (x, y, width, height, hp, speed)
        super(x, y, 80f, 20f, 2, 120f);
    }

    @Override
    public boolean isIgnoringGravity() { return true; }

    @Override
    public void updateAI(float delta, Player player, boolean atEdgeOrWall) {
        Vector2 playerPos = player.getPosition();
        if (getEnemyState() == EnemyState.PATROL) {
            getVelocity().set(0, 0);
            setCurrentState(EntityState.IDLE);
            if (getPosition().dst(playerPos) < 250f) {
                targetPosition.set(playerPos);
                setEnemyState(EnemyState.PAUSE);
                aiTimer = 0f;
                setLookingRight(playerPos.x > getPosition().x);
            }
        } else if (getEnemyState() == EnemyState.PAUSE && aiTimer >= 0.4f) {
            setEnemyState(EnemyState.CHARGE);
            Vector2 dashVec = new Vector2(targetPosition).sub(getPosition()).nor().scl(speed * 2f);
            getVelocity().set(dashVec);
            setCurrentState(EntityState.RUNNING);
            aiTimer = 0f;
        } else if (getEnemyState() == EnemyState.CHARGE && (aiTimer >= 1.2f || getPosition().dst(targetPosition) < 15f)) {
            setEnemyState(EnemyState.PATROL);
        }
    }
    @Override
    public AnimationType getAnimationType() {
        // 💡 ADDED: Route directly to death states when processing damage death
        if (isDying()) {
            return this.isOnGround() ? AnimationType.Mosquito_death_land : AnimationType.Mosquito_death_air;
        }
        if (getEnemyState() == com.test.liftoff.Enums.EnemyState.PAUSE) return AnimationType.Mosquito_attack_anticipate;
        if (getEnemyState() == com.test.liftoff.Enums.EnemyState.CHARGE) return AnimationType.Mosquito_attack;
        return AnimationType.Mosquito_idle;
    }

    @Override
    public float getSpriteOffsetY(float frameHeight) {
        return 60;
    }
}
