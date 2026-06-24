package com.test.liftoff.View;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.test.liftoff.Controller.GameController;

public class GameScreen extends AbstractScreen{
    private SpriteBatch batch;
    private Camera camera;
    private Viewport viewport;
    private ShapeRenderer shapeRenderer;
    private GameProcessor gameProcessor;

    private GameController gameController;

    public GameScreen(GameController gameController) {
        this.gameController = gameController;
    }

    @Override
    public void show() {
        super.show();

        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        shapeRenderer = new ShapeRenderer();
        gameProcessor = new GameProcessor(gameController);

        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(gameProcessor);
        Gdx.input.setInputProcessor(inputMultiplexer);


    }


    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        viewport.update(width, height);
    }

    @Override
    public void renderWorld(float delta) {
        gameController.update(delta);

        camera.position.set(gameController.getPlayerPosition().x, 0, 0);
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(gameController.getPlayerCurrentFrame(), gameController.getPlayerPosition().x, gameController.getPlayerPosition().y);
        batch.end();

        TextureRegion frame = gameController.getPlayerCurrentFrame();


        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.line(0, 0, 200, 0);
        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.line(0, 0, 0, 200);
//        shapeRenderer.rect(gameController.getPlayerPosition().x, gameController.getPlayerPosition().y, 100, 100);
        shapeRenderer.rect(
            gameController.getPlayerPosition().x,
            gameController.getPlayerPosition().y,
            frame.getRegionWidth(),
            frame.getRegionHeight()
        );
        shapeRenderer.end();
    }
}
