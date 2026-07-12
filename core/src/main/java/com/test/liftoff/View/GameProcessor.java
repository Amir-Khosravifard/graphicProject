package com.test.liftoff.View;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.test.liftoff.Controller.GameController;

public class GameProcessor implements InputProcessor {
    private GameController gameController;

    public GameProcessor(GameController gameController) {
        this.gameController = gameController;
    }

    @Override
    public boolean keyDown(int keyCode) {
        boolean ctrlPressed = com.badlogic.gdx.Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) ||
            com.badlogic.gdx.Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT);

        if (ctrlPressed) {
            if (keyCode == Input.Keys.T) {
                gameController.cheatTeleportToBoss();
                return true;
            }
            if (keyCode == Input.Keys.N) {
                gameController.cheatToggleNoclip();
                return true;
            }
            if (keyCode == Input.Keys.H) {
                gameController.cheatEmergencyHeal();
                return true;
            }
            if (keyCode == Input.Keys.S) {
                gameController.cheatRefillSoul();
                return true;
            }
            if (keyCode == Input.Keys.G) {
                gameController.cheatToggleGodMode();
                return true;
            }
            if (keyCode == Input.Keys.K) {
                gameController.cheatKillAllEnemies();
                return true;
            }
        }


        if (gameController.isNearZote()) {
            if (keyCode == Input.Keys.E && !gameController.isDialogueActive()) {
                gameController.setDialogueActive(true);
                GameScreen.resetTypewriterEffect();
                return true;
            }
            if (keyCode == Input.Keys.ENTER && gameController.isDialogueActive()) {
                GameScreen.advanceOrSkipDialogueLine();
                return true;
            }
        }

        if (gameController.isDialogueActive()) return true;

        if (keyCode == Input.Keys.ESCAPE) {
            if (gameController.isPaused()) return false;
            gameController.setPaused(true);
            PauseModal pauseModal = new PauseModal() {
                @Override
                public void onExit() {
                    gameController.saveCurrentGame();
                    UIManager.changeScreen(new StartGameScreen());
                }

                @Override
                public void onResume() {
                    hide();
                }

                @Override
                public void hide() {
                    super.hide();
                    gameController.setPaused(false);
                }
            };
            pauseModal.show();
        } else if (keyCode == Input.Keys.RIGHT) {
            gameController.setMovingRight(true);
        } else if (keyCode == Input.Keys.LEFT) {
            gameController.setMovingLeft(true);
        } else if (keyCode == Input.Keys.DOWN) {
            gameController.setLookingDown(true);
        } else if (keyCode == Input.Keys.UP) {
            if (gameController.getPlayer().isNoclip()) gameController.setMovingUp(true);
            else gameController.jumpPlayer();
        } else if (keyCode == Input.Keys.C) {
            gameController.handlePlayerDash();
        } else if (keyCode == Input.Keys.X) {
            gameController.handlePlayerAttack();
        } else if (keyCode == Input.Keys.A) {
            gameController.setFocusActive(true);
        } else if (keyCode == Input.Keys.O) {
            gameController.damagePlayer(1);
        }
        return false;
    }

    @Override
    public boolean keyUp(int keyCode) {
        if (gameController.isDialogueActive()) return true;

        if (keyCode == Input.Keys.RIGHT) {
            gameController.setMovingRight(false);
        } else if (keyCode == Input.Keys.LEFT) {
            gameController.setMovingLeft(false);
        } else if (keyCode == Input.Keys.DOWN) {
            gameController.setLookingDown(false);
        } else if (keyCode == Input.Keys.UP) {
            gameController.setMovingUp(false);
            gameController.cutPlayerJump();
        } else if (keyCode == Input.Keys.A) {
            gameController.setFocusActive(false);
        }
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
