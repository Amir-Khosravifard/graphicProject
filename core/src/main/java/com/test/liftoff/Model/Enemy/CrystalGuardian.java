package com.test.liftoff.Model.Enemy;

import com.badlogic.gdx.math.Vector2;
import com.test.liftoff.Enums.AnimationType;
import com.test.liftoff.Enums.EnemyState;
import com.test.liftoff.Enums.EntityState;
import com.test.liftoff.Model.Entity.Player;

public class CrystalGuardian extends Enemy {
    public boolean isEnraged = false;

    public CrystalGuardian(float x, float y) {
        super(x, y, 90f, 110f, 10, 100f);
        this.contactDamage = 2;
    }

    @Override
    public void updateAI(float delta, Player player, boolean atEdgeOrWall) {

        float playerCenter = player.getPosition().x + player.getWidth() / 2f;
        float bossCenter = getPosition().x + getWidth() / 2f;
        float horizontalDifference = playerCenter - bossCenter;

        getVelocity().x = 0;


        if (getEnemyState() == EnemyState.PATROL) {
            setCurrentState(EntityState.IDLE);


            if (Math.abs(horizontalDifference) > 40f) {
                setLookingRight(horizontalDifference > 0);
            }

            if (Math.abs(getPosition().y - player.getPosition().y) < 60f && getPosition().dst(player.getPosition()) < 400f) {
                setEnemyState(EnemyState.ATTACK);
                aiTimer = 0f;
            }
        } else if (getEnemyState() == EnemyState.ATTACK) {
            setCurrentState(EntityState.ATTACKING);


            if (Math.abs(horizontalDifference) > 40f) {
                setLookingRight(horizontalDifference > 0);
            }

            if (aiTimer >= 0.5f) {
                if (Math.abs(getPosition().y - player.getPosition().y) < 40f) isEnraged = true;
                setEnemyState(EnemyState.CHARGE);
                aiTimer = 0f;
            }
        } else if (getEnemyState() == EnemyState.CHARGE) {
            setCurrentState(EntityState.RUNNING);


            if (atEdgeOrWall) {
                setEnemyState(EnemyState.PATROL);
                aiTimer = 0f;
            } else {


                float chaseDir = isLookingRight() ? 1f : -1f;
                getVelocity().x = speed * 2.0f * chaseDir;
            }

            if (aiTimer >= 3.0f) {
                setEnemyState(EnemyState.PATROL);
                aiTimer = 0f;
            }
        }
    }

    @Override
    public AnimationType getAnimationType() {
        if (isDying()) return AnimationType.Crystallized_death;
        if (getEnemyState() == EnemyState.ATTACK) return AnimationType.Crystallized_shoot;
        if (getEnemyState() == EnemyState.CHARGE) return AnimationType.Crystallized_run;
        return AnimationType.Crystallized_idle;
    }
}
