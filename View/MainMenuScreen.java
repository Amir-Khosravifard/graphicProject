package com.test.liftoff.View;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen implements Screen {
    private Stage stage;
    private Skin skin;
    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiSkin/plain-james-ui.json"));

        Stack stack = new Stack();
        stack.setFillParent(true);


        Texture backGroundTexture = new Texture(Gdx.files.internal("BackGround/Voidheart_menu_BG.png"));
        Image backGroundImage = new Image(backGroundTexture);
        stack.add(backGroundImage);

//        stack.add(logoImage);

        Table uiTable = new Table();

        Texture logoTexture = new Texture(Gdx.files.internal("BackGround/vheart_title.png"));
        Image logoImage = new Image(logoTexture);
        uiTable.add(logoImage).size(logoTexture.getWidth(), logoTexture.getHeight()).center().padBottom(-60).row();

        TextButton startGameButton = new TextButton("Start Game", skin);
        uiTable.add(startGameButton).center().pad(10).row();

        TextButton settingButton = new TextButton("Settings", skin);
        uiTable.add(settingButton).center().pad(10).row();

        TextButton guideButton = new TextButton("Guide", skin);
        uiTable.add(guideButton).center().pad(10).row();

        TextButton achievementsButton = new TextButton("Achievements", skin);
        uiTable.add(achievementsButton).center().pad(10).row();

        TextButton exitButton = new TextButton("Exit", skin);
        uiTable.add(exitButton).center().pad(10).padBottom(25).row();
//        uiTable.setDebug(true);

        stack.add(uiTable);

        stage.addActor(stack);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1f);
        Gdx.gl.glClear(com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();

    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
