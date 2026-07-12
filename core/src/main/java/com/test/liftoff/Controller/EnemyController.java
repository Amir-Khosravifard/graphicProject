package com.test.liftoff.Controller;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.test.liftoff.Enums.EnemyState;
import com.test.liftoff.Enums.EntityState;
import com.test.liftoff.Model.Enemy.Enemy;
import com.test.liftoff.Model.Entity.Player;
import com.test.liftoff.Model.Entity.Spike;

import java.util.ArrayList;

public class EnemyController {
    private final ArrayList<Enemy> activeEnemies = new ArrayList<>();

    public void addEnemy(Enemy enemy) {
        activeEnemies.add(enemy);
    }

    public ArrayList<Enemy> getActiveEnemies() {
        return activeEnemies;
    }

    public void updateEnemies(float delta, Player player, ArrayList<Rectangle> platforms, ArrayList<Spike> spikes) {
        for (int i = activeEnemies.size() - 1; i >= 0; i--) {
            Enemy enemy = activeEnemies.get(i);
            if (enemy.isDead()) {
                activeEnemies.remove(i);
                continue;
            }
            if (enemy.isDying()) {
                enemy.deathTimer -= delta;
                if (enemy.deathTimer <= 0) {
                    enemy.setDead(true);
                }
                continue;
            }


            if (enemy.flipCooldownTimer > 0f) {
                enemy.flipCooldownTimer -= delta;
            }

            enemy.aiTimer += delta;
            boolean atEdgeOrWall = checkObstacleOrCliff(enemy, platforms, spikes);
            enemy.updateAI(delta, player, atEdgeOrWall);
        }
    }

    private boolean checkObstacleOrCliff(Enemy enemy, ArrayList<Rectangle> platforms, ArrayList<Spike> spikes) {
        if (enemy.isIgnoringGravity()) return false;

        float aheadWallX = enemy.isLookingRight() ? enemy.getPosition().x + enemy.getWidth() : enemy.getPosition().x - 6f;
        Rectangle wallCheck = new Rectangle(aheadWallX, enemy.getPosition().y + 4f, 6f, enemy.getHeight() - 8f);

        float aheadFootX = enemy.isLookingRight() ? enemy.getPosition().x + enemy.getWidth() : enemy.getPosition().x - 12f;
        Rectangle footCheck = new Rectangle(aheadFootX, enemy.getPosition().y - 6f, 12f, 6f);

        boolean groundAhead = false;
        boolean hitWallOrSpike = false;

        if (platforms != null) {
            for (Rectangle p : platforms) {
                if (wallCheck.overlaps(p)) {
                    hitWallOrSpike = true;
                }
                if (footCheck.overlaps(p)) {
                    groundAhead = true;
                }
            }
        }

        if (spikes != null) {
            for (Spike s : spikes) {
                if (wallCheck.overlaps(s.getBounds()) || footCheck.overlaps(s.getBounds())) {
                    hitWallOrSpike = true;
                }
            }
        }

        return !groundAhead || hitWallOrSpike;
    }
}
