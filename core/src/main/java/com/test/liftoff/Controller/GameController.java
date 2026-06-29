package com.test.liftoff.Controller;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.test.liftoff.Enums.AnimationType;
import com.test.liftoff.Enums.EntityState;
import com.test.liftoff.Model.Entity.Entity;
import com.test.liftoff.Model.Entity.Player;
import com.test.liftoff.View.AssetManager;

import java.util.ArrayList;

public class GameController {
    private static int gravity = 1000;
    private Player player;
    private AnimationType playerCurrentAnimation = AnimationType.KnightIdle;
    private float playerStateTime = 0;

    private ArrayList<Entity> entities = new ArrayList<>();
    private ArrayList<Rectangle> platforms = new ArrayList<>();

    public void setPlatforms(ArrayList<Rectangle> platforms) {
        this.platforms = platforms;
    }


    public ArrayList<Entity> getEntities() {
        return entities;
    }

    public Player getPlayer() {
        return player;
    }

    public TextureRegion getPlayerCurrentFrame(){
        TextureRegion frame = AssetManager.getAnimation(playerCurrentAnimation).getKeyFrame(playerStateTime);
        if(player.isLookingRight() && !frame.isFlipX())
            frame.flip(true, false);
        else if(!player.isLookingRight() && frame.isFlipX())
            frame.flip(true, false);
        return frame;
    }

    public GameController(Player player) {
        this.player = player;
        this.entities.add(player);
    }

    public Vector2 getPlayerPosition(){
        return player.getPosition();
    }
    public void setMovingRight(boolean movingRight){
        player.setMovingRight(movingRight);
    }
    public void setMovingLeft(boolean movingLeft){
        player.setMovingLeft(movingLeft);
    }
    public void jumpPlayer(){
        player.jump();
    }
    public ArrayList<Rectangle> getPlatforms() {
        return platforms;
    }

    private void applyPhysics(Entity entity, float delta) {
        if(!entity.isOnGround()) {
            entity.setVelocityY(entity.getVelocity().y - gravity * delta); // Gravity
        }

        float maxStepTime = 0.003f;
        float accumulator = delta;

        entity.setOnGround(false);

        while (accumulator > 0) {
            float stepDelta = Math.min(accumulator, maxStepTime);
            accumulator -= stepDelta;

            // X-AXIS (Walls)
            entity.setPositionX(entity.getPosition().x + entity.getVelocity().x * stepDelta);
            if (platforms != null) {
                Rectangle bounds = new Rectangle(entity.getPosition().x, entity.getPosition().y, entity.getWidth(), entity.getHeight());
                for (Rectangle platform : platforms) {
                    if (bounds.overlaps(platform)) {
                        if (entity.getVelocity().x > 0) entity.setPositionX(platform.x - entity.getWidth());
                        else if (entity.getVelocity().x < 0) entity.setPositionX(platform.x + platform.width);
                        entity.setVelocityX(0);
                        break;
                    }
                }
            }

            // Y-AXIS (Floors/Ceilings)
            entity.setPositionY(entity.getPosition().y + entity.getVelocity().y * stepDelta);
            if (platforms != null) {
                Rectangle bounds = new Rectangle(entity.getPosition().x, entity.getPosition().y, entity.getWidth(), entity.getHeight());
                for (Rectangle platform : platforms) {
                    if (bounds.overlaps(platform)) {
                        if (entity.getVelocity().y <= 0) {
                            entity.setPositionY(platform.y + platform.height);
                            entity.setVelocityY(0);
                            entity.setOnGround(true);
                        } else if (entity.getVelocity().y > 0) {
                            entity.setPositionY(platform.y - entity.getHeight());
                            entity.setVelocityY(0);
                        }
                        break;
                    }
                }
            }
        }

        // 1-pixel Ground Sensor
        entity.setOnGround(false);
        if (platforms != null) {
            Rectangle groundCheck = new Rectangle(entity.getPosition().x, entity.getPosition().y - 1f, entity.getWidth(), 1f);
            for (Rectangle platform : platforms) {
                if (groundCheck.overlaps(platform)) {
                    entity.setOnGround(true);
                    entity.setVelocityY(0);
                    break;
                }
            }
        }
    }


    private void updateEntityAnimation(Entity entity, float delta) {
        EntityState nextState;

        if (entity.getVelocity().x != 0 && entity.isOnGround()) {
            nextState = EntityState.RUNNING;
        } else if (!entity.isOnGround()) {
            nextState = entity.getVelocity().y > 0 ? EntityState.JUMPING : EntityState.FALLING;
        } else {
            nextState = EntityState.IDLE;
        }

        // Just change the state data. No timing logic here anymore!
        entity.setCurrentState(nextState);
    }



    public void update(float delta){


        if(player.isMovingLeft()){
            player.setVelocityX(-500);
            player.setLookingRight(false);
        }
        else if(player.isMovingRight()){
            player.setVelocityX(500);
            player.setLookingRight(true);
        }
        else
            player.setVelocityX(0);

        for(Entity entity : entities) {
            applyPhysics(entity, delta);
            updateEntityAnimation(entity, delta);
        }
    }


}
