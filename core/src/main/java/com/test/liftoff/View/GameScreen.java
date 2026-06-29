package com.test.liftoff.View;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.test.liftoff.Controller.GameController;
import com.test.liftoff.Enums.AnimationType;
import com.test.liftoff.Enums.EntityState;
import com.test.liftoff.Model.Entity.Entity;
import com.test.liftoff.Model.Entity.Player;
import com.test.liftoff.Tiled.TiledMapHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class GameScreen extends AbstractScreen{
    private SpriteBatch batch;
    private Camera camera;
    private Viewport viewport;
    private ShapeRenderer shapeRenderer;
    private GameProcessor gameProcessor;
    private GameController gameController;

    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer mapRenderer;

    private HashMap<Entity, RenderClock> visualClocks = new HashMap<>();


    private int[] backgroundLayers;
    private int[] foregroundLayers;

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


        TiledMapHelper mapHelper = new TiledMapHelper();
        tiledMap = mapHelper.loadMap("tiledMaps/firstMap.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);


        int backLayerIdx = tiledMap.getLayers().getIndex("BackLayer");
        int mainLayerIdx = tiledMap.getLayers().getIndex("main");
        int frontLayerIdx = tiledMap.getLayers().getIndex("FrontLayer");


        backgroundLayers = new int[]{backLayerIdx, mainLayerIdx};
        foregroundLayers = new int[]{frontLayerIdx};

        Vector2 spawnPoint = mapHelper.getObjectPosition("Object Layer 1", "spawnPlayer");
        gameController.getPlayerPosition().set(spawnPoint);

        ArrayList<Rectangle> platforms = mapHelper.getCollisionRectangles("Object Layer 1");
        gameController.setPlatforms(platforms);

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

    private AnimationType getAnimationTypeForState(EntityState state) {
        // 💡 FIXED: Clean, direct stateless mapping
        switch (state) {
            case RUNNING:
                return AnimationType.KnightRun;
            case JUMPING:
            case FALLING:
                return AnimationType.KnightRegularJump;
            case LANDING:
                return AnimationType.KnightRegularLanding; // Returns Landing.png directly
            case IDLE:
            default:
                return AnimationType.KnightIdle;
        }
    }
    private TextureRegion getFrameFromAsset(AnimationType animationType, float visualTime, Entity entity) {
        TextureRegion frame = AssetManager.getAnimation(animationType).getKeyFrame(visualTime);

        // Handle image mirroring/flipping based on model orientation
        if (entity.isLookingRight() && !frame.isFlipX()) frame.flip(true, false);
        else if (!entity.isLookingRight() && frame.isFlipX()) frame.flip(true, false);

        return frame;
    }

    @Override
    public void renderWorld(float delta) {
        gameController.update(delta);

        Player player = gameController.getPlayer();
        camera.position.set(player.getPosition().x, player.getPosition().y, 0);
        camera.update();


        mapRenderer.setView((OrthographicCamera) camera);
        mapRenderer.render(backgroundLayers);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (Entity entity : gameController.getEntities()) {
            if (!visualClocks.containsKey(entity)) {
                visualClocks.put(entity, new RenderClock());
            }
            RenderClock clock = visualClocks.get(entity);

            AnimationType targetAnim = getAnimationTypeForState(entity.getCurrentState());

            clock.update(targetAnim, delta);

            TextureRegion frame = getFrameFromAsset(targetAnim, clock.getAnimTime(), entity);


            if (frame != null) {
                float spriteOffsetX = ((frame.getRegionWidth() - entity.getWidth()) / 2f) - 5f;
                float spriteOffsetY = 0f;

                batch.draw(
                    frame,
                    entity.getPosition().x - spriteOffsetX,
                    entity.getPosition().y - spriteOffsetY
                );
            }
        }
        batch.end();















//        mapRenderer.render(foregroundLayers);
        mapRenderer.render(foregroundLayers);


        shapeRenderer.setProjectionMatrix(camera.combined);

//
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//        shapeRenderer.setColor(Color.RED);
//        shapeRenderer.line(0, 0, 200, 0);
//        shapeRenderer.setColor(Color.BLUE);
//        shapeRenderer.line(0, 0, 0, 200);
//
//        shapeRenderer.setColor(Color.BLUE);
//        shapeRenderer.rect(
//            gameController.getPlayerPosition().x,
//            gameController.getPlayerPosition().y,
//            frame.getRegionWidth(),
//            frame.getRegionHeight()
//        );
//
//        shapeRenderer.setColor(Color.RED);
//        shapeRenderer.rect(
//            gameController.getPlayerPosition().x,
//            gameController.getPlayerPosition().y,
//            physicsWidth,
//            physicsHeight
//        );
//
//        shapeRenderer.setColor(Color.GREEN);
//        if (gameController.getPlatforms() != null) {
//            for (Rectangle platform : gameController.getPlatforms()) {
//                shapeRenderer.rect(platform.x, platform.y, platform.width, platform.height);
//            }
//        }
        shapeRenderer.end();
    }
}
