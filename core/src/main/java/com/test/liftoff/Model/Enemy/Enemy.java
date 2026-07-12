package com.test.liftoff.Model.Enemy;

import com.test.liftoff.Enums.EnemyState;
import com.test.liftoff.Model.Entity.Entity;
import com.test.liftoff.Model.Entity.Player;

public abstract class Enemy extends Entity {
    protected EnemyState enemyState = EnemyState.PATROL;
    public float aiTimer = 0f;
    public final float speed;
    protected int contactDamage = 1;

    protected boolean isDying = false;
    public float deathTimer = 0f;


    public float flipCooldownTimer = 0f;

    public Enemy(float x, float y, float width, float height, int maxHealth, float speed) {
        super(width, height, maxHealth);
        this.speed = speed;
        this.position.set(x, y);
        this.isLookingRight = false;
    }

    public EnemyState getEnemyState() {
        return enemyState;
    }

    public void setEnemyState(EnemyState state) {
        this.enemyState = state;
    }

    public int getContactDamage() {
        return contactDamage;
    }

    public boolean isDying() {
        return isDying;
    }

    @Override
    public void takeDamage(int amount) {
        if (isDead || isDying) return;
        this.health -= amount;
        if (this.health <= 0) {
            this.health = 0;
            this.isDying = true;
            this.deathTimer = 0.5f;
            this.getVelocity().set(0, 0);
        }
    }

    public abstract void updateAI(float delta, Player player, boolean atEdgeOrWall);
}
