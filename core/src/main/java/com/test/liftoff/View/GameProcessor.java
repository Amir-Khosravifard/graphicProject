package com.test.liftoff.View;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.test.liftoff.Controller.GameController;
import com.test.liftoff.Enums.AnimationType;

public class GameProcessor implements InputProcessor {
    private GameController gameController;

    public GameProcessor(GameController gameController) {
        this.gameController = gameController;
    }

    @Override
    public boolean keyDown(int keyCode) {
        if(keyCode == Input.Keys.ESCAPE){
            PauseModal pauseModal = new PauseModal(){
                @Override
                public void onExit() {
                    UIManager.changeScreen(new StartGameScreen());
                }

                @Override
                public void onResume() {
                    hide();
                }
            };
            pauseModal.show();
        }
        else if(keyCode == Input.Keys.RIGHT){
            gameController.setMovingRight(true);
        }
        else if(keyCode == Input.Keys.LEFT){
            gameController.setMovingLeft(true);
        }

        else if (keyCode == Input.Keys.DOWN) {
            gameController.setLookingDown(true);
        }
        else if(keyCode == Input.Keys.UP){
            gameController.jumpPlayer();
        }
        else if(keyCode == Input.Keys.C) {
            gameController.handlePlayerDash();
        }
        else if(keyCode == Input.Keys.X){
            gameController.handlePlayerAttack();
        }
        else if(keyCode == Input.Keys.A){
            gameController.setFocusActive(true);
        }
        return false;
    }

    @Override
    public boolean keyUp(int keyCode) {

        if(keyCode == Input.Keys.RIGHT){
            gameController.setMovingRight(false);
        }
        else if(keyCode == Input.Keys.LEFT){
            gameController.setMovingLeft(false);
        }
        else if (keyCode == Input.Keys.DOWN) {
            gameController.setLookingDown(false);
        }
        else if(keyCode == Input.Keys.A){ gameController.setFocusActive(false); } // Releasing S stops focusing
        return false;
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    @Override
    public boolean touchDown(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchCancelled(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchDragged(int i, int i1, int i2) {
        return false;
    }

    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;
    }

    @Override
    public boolean scrolled(float v, float v1) {
        return false;
    }
}
