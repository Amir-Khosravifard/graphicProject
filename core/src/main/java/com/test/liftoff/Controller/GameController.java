package com.test.liftoff.Controller;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.test.liftoff.Enums.AnimationType;
import com.test.liftoff.Model.Player;
import com.test.liftoff.View.AssetManager;

public class GameController {
    private Player player;
    private AnimationType playerCurrentAnimation = AnimationType.KnightIdle;
    private float playerStateTime = 0;

    public void setPlayerCurrentAnimation(AnimationType playerCurrentAnimation) {
        this.playerCurrentAnimation = playerCurrentAnimation;
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
    }

    public Vector2 getPlayerPosition(){
        return player.getPosition();
    }
    public void setMovingRight(boolean movingRight){
        player.setMovingRight(movingRight);
    }
    public void jumpPlayer(){
        player.jump();
    }


    public void setMovingLeft(boolean movingLeft){
        player.setMovingLeft(movingLeft);
    }
    public void update(float delta){
        if(player.getPosition().y < 0.001)
            player.setOnGround(true);
        else
            player.setOnGround(false);

        if(!player.isOnGround())
            player.setVelocityY(player.getVelocity().y-1000*delta);
        else if(player.getVelocity().y < 0.01){
            player.setPositionY(0);
            player.setVelocityY(0);
        }

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



        AnimationType nextAnimation = null;
        if(player.getVelocity().x != 0 && player.isOnGround())
            nextAnimation = AnimationType.KnightRun;
        else if(!player.isOnGround())
            nextAnimation = AnimationType.KnightRegularJump;
        else
            nextAnimation = AnimationType.KnightIdle;


        if(nextAnimation != playerCurrentAnimation){
            playerCurrentAnimation = nextAnimation;
            playerStateTime = 0f;
        }
        else
            playerStateTime += delta;

        player.getPosition().add(player.getVelocity().cpy().scl(delta));
    }


}
