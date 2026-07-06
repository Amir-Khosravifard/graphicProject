package com.test.liftoff.Controller;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.test.liftoff.Enums.EnemyState;
import com.test.liftoff.Enums.EntityState;
import com.test.liftoff.Model.Enemy.Enemy;
import com.test.liftoff.Model.Entity.Player;

import java.util.ArrayList;

public class EnemyController {
    private final ArrayList<Enemy> activeEnemies = new ArrayList<>();

    public void addEnemy(Enemy enemy) { activeEnemies.add(enemy); }
    public ArrayList<Enemy> getActiveEnemies() { return activeEnemies; }

    public void updateEnemies(float delta, Player player, ArrayList<Rectangle> platforms) {
        for (int i = activeEnemies.size() - 1; i >= 0; i--) {
            Enemy enemy = activeEnemies.get(i);

            // 💡 Only prune tracking once the death timer hits 0
            if (enemy.isDead()) {
                activeEnemies.remove(i);
                continue;
            }

            // 💡 If dying, tick structural timers down and bypass movement entirely
            if (enemy.isDying()) {
                enemy.deathTimer -= delta;
                if (enemy.deathTimer <= 0) {
                    enemy.setDead(true); // Flag asset removal across all master arrays
                }
                continue;
            }

            enemy.aiTimer += delta;
            boolean atEdgeOrWall = checkObstacleOrCliff(enemy, platforms);
            enemy.updateAI(delta, player, atEdgeOrWall);
        }
    }

    private boolean checkObstacleOrCliff(Enemy enemy, ArrayList<Rectangle> platforms) {
        // Stationary or flying entities don't need boundary checking
        if (enemy.isIgnoringGravity() || enemy.getVelocity().x == 0) return false;

        boolean hitWall = enemy.getVelocity().x == 0 && enemy.getCurrentState() == EntityState.RUNNING;
        float aheadX = enemy.getPosition().x + (enemy.isLookingRight() ? enemy.getWidth() : -10f);
        Rectangle footCheck = new Rectangle(aheadX, enemy.getPosition().y - 4f, 10f, 4f);

        boolean groundAhead = false;
        if (platforms != null) {
            for (Rectangle p : platforms) {
                if (footCheck.overlaps(p)) {
                    groundAhead = true;
                    break;
                }
            }
        }
        return !groundAhead || hitWall;
    }
}
