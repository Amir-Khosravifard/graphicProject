package com.test.liftoff.Model.Enemy;

import com.test.liftoff.Enums.AnimationType;
import com.test.liftoff.Enums.EntityState;
import com.test.liftoff.Model.Entity.Player;

public class Crawlid extends Enemy{
    public Crawlid(float x, float y) {
        // 💡 TWEAK HITBOX HERE: (x, y, width, height, hp, speed)
        super(x, y, 70f, 55f, 5, 60f);
    }

    @Override
    public void updateAI(float delta, Player player, boolean atEdgeOrWall) {
        float dir = isLookingRight() ? 1f : -1f;
        getVelocity().x = speed * dir;
        setCurrentState(EntityState.RUNNING);
        if (atEdgeOrWall) {
            setLookingRight(!isLookingRight());
        }
    }
    @Override
    public AnimationType getAnimationType() {
        // 💡 FIXED: Instantly switches asset sheets if the crawler is dying
        if (isDying()) {
            return this.isOnGround() ? com.test.liftoff.Enums.AnimationType.Crawlid_death_land
                : com.test.liftoff.Enums.AnimationType.Crawlid_death_air;
        }
        return com.test.liftoff.Enums.AnimationType.Crawlid_walk;
    }
}
