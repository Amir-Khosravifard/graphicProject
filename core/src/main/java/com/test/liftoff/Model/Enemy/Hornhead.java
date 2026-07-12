package com.test.liftoff.Model.Enemy;

import com.badlogic.gdx.math.Rectangle;
import com.test.liftoff.Enums.AnimationType;
import com.test.liftoff.Enums.EnemyState;
import com.test.liftoff.Enums.EntityState;
import com.test.liftoff.Model.Entity.Player;

public class Hornhead extends Enemy {
    public Hornhead(float x, float y) {
        super(x, y, 75f, 110f, 4, 80f);
    }

    @Override
    public float getWidth() {
        if (getEnemyState() == EnemyState.CHARGE) {
            return 110f;
        }
        return 60f;
    }

    @Override
    public void updateAI(float delta, Player player, boolean atEdgeOrWall) {
        float dir = isLookingRight() ? 1f : -1f;
        Rectangle playerBox = new Rectangle(player.getPosition().x, player.getPosition().y, player.getWidth(), player.getHeight());

        if (getEnemyState() == EnemyState.PATROL) {
            getVelocity().x = speed * dir;
            setCurrentState(EntityState.RUNNING);
            if (aiTimer >= 2.0f) {
                setEnemyState(EnemyState.PAUSE);
                aiTimer = 0f;
            }
            float sightX = isLookingRight() ? getPosition().x : getPosition().x - 200f;
            Rectangle sightBox = new Rectangle(sightX, getPosition().y, 200f, getHeight());
            if (sightBox.overlaps(playerBox)) {
                setEnemyState(EnemyState.ATTACK);
                aiTimer = 0f;
            }


            if (atEdgeOrWall && flipCooldownTimer <= 0f) {
                setLookingRight(!isLookingRight());
                flipCooldownTimer = 0.40f;
            }
        } else if (getEnemyState() == EnemyState.ATTACK) {
            getVelocity().x = 0;
            setCurrentState(EntityState.ATTACKING);
            if (aiTimer >= 0.4f) {
                setEnemyState(EnemyState.CHARGE);
                aiTimer = 0f;
            }
        } else if (getEnemyState() == EnemyState.PAUSE) {
            getVelocity().x = 0;
            setCurrentState(EntityState.IDLE);
            if (aiTimer >= 1.0f) {
                setEnemyState(EnemyState.PATROL);
                aiTimer = 0f;
            }
        } else if (getEnemyState() == EnemyState.CHARGE) {
            getVelocity().x = speed * 2.5f * dir;
            if (atEdgeOrWall || aiTimer >= 2.5f) {
                setEnemyState(EnemyState.PAUSE);
                aiTimer = 0f;
            }
        }
    }

    @Override
    public AnimationType getAnimationType() {
        if (isDying()) {
            return AnimationType.Hornhead_death_land;
        }
        if (getEnemyState() == EnemyState.ATTACK) return AnimationType.Hornhead_attack_anticipate;
        if (getEnemyState() == EnemyState.PAUSE) return AnimationType.Hornhead_idle;
        if (getEnemyState() == EnemyState.CHARGE) return AnimationType.Hornhead_attack_lunge;
        return AnimationType.Hornhead_walk;
    }

    @Override
    public float getSpriteOffsetX(float frameWidth) {
        if (getEnemyState() == EnemyState.CHARGE)
            return (frameWidth - getWidth()) / 2f - 40;
        return (frameWidth - getWidth()) / 2f;
    }

    @Override
    public float getSpriteOffsetY(float frameHeight) {
        return 0f;
    }
}
