package com.test.liftoff.View;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class AbstractScreen implements Screen {
    protected Stage stage;
    protected Skin skin;

    protected Table rootTable;
    private Stack mainStack;
    private Stack modalStack;
    private Stack toastStack;

    @Override
    public void show() {

        stage = new Stage(new ScreenViewport());
        skin = AssetManager.getSkin();

        mainStack = new Stack();
        mainStack.setFillParent(true);
        modalStack = new Stack();
        toastStack = new Stack();
        rootTable = new Table();

        mainStack.add(rootTable);
        mainStack.add(modalStack);
        mainStack.add(toastStack);

        stage.addActor(mainStack);

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1f);
        Gdx.gl.glClear(com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);

        renderWorld(delta);

        stage.draw();
    }

    public void renderWorld(float delta) {
    }

    @Override
    public void resize(int width, int height) {
        float baseWidth = 1280f;
        float baseHeight = 720f;

        float scaleX = width / baseWidth;
        float scaleY = height / baseHeight;
        float scale = Math.min(scaleX, scaleY);

        float maxScale = 1.25f;
        float minScale = 0.75f;
        if (scale > maxScale) scale = maxScale;
        if (scale < minScale) scale = minScale;

        if (stage.getViewport() instanceof ScreenViewport) {
            ((ScreenViewport) stage.getViewport()).setUnitsPerPixel(1f / scale);
        }

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
        if (stage != null) stage.dispose();
    }

    public void addToModalStack(Table table) {
        modalStack.add(table);
    }


    public TextButton createBackButton(Screen backScreen) {
        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UIManager.changeScreen(backScreen);
            }
        });
        return backButton;
    }
}
