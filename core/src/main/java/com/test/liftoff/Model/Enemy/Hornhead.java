package com.test.liftoff.Model.Enemy;

import com.badlogic.gdx.math.Rectangle;
import com.test.liftoff.Enums.AnimationType;
import com.test.liftoff.Enums.EnemyState;
import com.test.liftoff.Enums.EntityState;
import com.test.liftoff.Model.Entity.Player;

public class Hornhead extends Enemy {
    public Hornhead(float x, float y) {
        // Base setup: (x, y, width, height, hp, speed)
        super(x, y, 75f, 110f, 4, 80f);
    }

    // =========================================================================
    // 💡 Dynamic State-Based Hitbox
    // =========================================================================
    @Override
    public float getWidth() {
        // When actively lunging, extend the physical reach to match his horns
        if (getEnemyState() == EnemyState.CHARGE) {
            return 110f;
        }
        return 60f; // Fallback to normal patrol width
    }

    @Override
    public void updateAI(float delta, Player player, boolean atEdgeOrWall) {
        float dir = isLookingRight() ? 1f : -1f;
        Rectangle playerBox = new Rectangle(player.getPosition().x, player.getPosition().y, player.getWidth(), player.getHeight());

        // 1. Patrol Phase
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
                setEnemyState(EnemyState.ATTACK); // Enter wind-up anticipation
                aiTimer = 0f;
            }
            if (atEdgeOrWall) setLookingRight(!isLookingRight());
        }

        // 2. Attack Anticipation Phase (Freeze and scream)
        else if (getEnemyState() == EnemyState.ATTACK) {
            getVelocity().x = 0;
            setCurrentState(EntityState.ATTACKING);

            if (aiTimer >= 0.4f) {
                setEnemyState(EnemyState.CHARGE); // Hitbox expands the exact frame this triggers!
                aiTimer = 0f;
            }
        }

        // 3. Post-Action Breather
        else if (getEnemyState() == EnemyState.PAUSE) {
            getVelocity().x = 0;
            setCurrentState(EntityState.IDLE);
            if (aiTimer >= 1.0f) {
                setEnemyState(EnemyState.PATROL);
                aiTimer = 0f;
            }
        }

        // 4. Heavy Lunge Charge Phase
        else if (getEnemyState() == EnemyState.CHARGE) {
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

    // =========================================================================
    // 🛠️ DYNAMIC SPRITE HOOK ALIGNMENTS
    // =========================================================================
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
