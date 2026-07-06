package com.test.liftoff.View;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.test.liftoff.Audio.SoundManager;
import com.test.liftoff.Controller.GameController;
import com.test.liftoff.Enums.AnimationType;
import com.test.liftoff.Enums.EnemyState;
import com.test.liftoff.Enums.EntityState;
import com.test.liftoff.Enums.LevelType;
import com.test.liftoff.Model.Enemy.Crawlid;
import com.test.liftoff.Model.Enemy.CrystalGuardian;
import com.test.liftoff.Model.Enemy.Hornhead;
import com.test.liftoff.Model.Enemy.Mosquito;
import com.test.liftoff.Model.Entity.Entity;
import com.test.liftoff.Model.Entity.Player;
import com.test.liftoff.Model.Entity.Spike;
import com.test.liftoff.Tiled.TiledMapHelper;
import com.test.liftoff.View.AnimationResolver.AnimationResolver;
import com.test.liftoff.View.AnimationResolver.PlayerAnimationResolver;

import java.util.ArrayList;
import java.util.HashMap;

public class GameScreen extends AbstractScreen{
    private SpriteBatch batch;
    private Camera camera;
    private Viewport viewport;
    private ShapeRenderer shapeRenderer;
    private GameProcessor gameProcessor;
    private GameController gameController;

    private LevelType levelType;
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer mapRenderer;


    private HashMap<Entity, RenderClock> visualClocks = new HashMap<>();


    private Texture fullMaskTexture;
    private Texture emptyMaskTexture;
    private Texture vesselTexture;
    private Texture[] liquidTextures;

    private Matrix4 uiMatrix;

    private int[] backgroundLayers;
    private int[] foregroundLayers;

    public GameScreen(GameController gameController, LevelType levelType) {
        this.gameController = gameController;
        this.levelType = levelType;
    }


    @Override
    public void show() {
        super.show();

        SoundManager.playBackGroundMusic(levelType.getBackGroundMusicType());
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        shapeRenderer = new ShapeRenderer();
        gameProcessor = new GameProcessor(gameController);


        fullMaskTexture = new Texture(Gdx.files.internal("animations/animation/HUD/FilledHealth.png"));
        emptyMaskTexture = new Texture(Gdx.files.internal("animations/animation/HUD/EmptyHealth.png"));
        vesselTexture = new Texture(Gdx.files.internal("animations/animation/HUD/HealthBar_005.png"));
        liquidTextures = new Texture[10];
        for (int i = 0; i < 10; i++) {
            liquidTextures[i] = new Texture(Gdx.files.internal("animations/animation/HUD/liquidTexture/"+(i)+".png"));
        }

        TiledMapHelper mapHelper = new TiledMapHelper();
        tiledMap = mapHelper.loadMap("tiledMaps/firstMap.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);


        int backLayerIdx = tiledMap.getLayers().getIndex("BackLayer");
        int mainLayerIdx = tiledMap.getLayers().getIndex("main");
        int frontLayerIdx = tiledMap.getLayers().getIndex("FrontLayer");


        backgroundLayers = new int[]{backLayerIdx, mainLayerIdx};
        foregroundLayers = new int[]{frontLayerIdx};

        Vector2 spawnPoint = mapHelper.getObjectPosition(levelType.getObjectLayerName(), levelType.getSpawnPointName());        gameController.getPlayerPosition().set(spawnPoint);
        gameController.getPlayerPosition().set(spawnPoint);
        gameController.setSpawnPoint(spawnPoint);

        ArrayList<Rectangle> platforms = mapHelper.getCollisionRectangles("Object Layer 1");
        gameController.setPlatforms(platforms);

        ArrayList<Spike> spikeModels = new ArrayList<>();
        if (tiledMap.getLayers().get("Spikes") != null) {
            for (MapObject object : tiledMap.getLayers().get("Spikes").getObjects()) {
                if (object instanceof RectangleMapObject) {
                    Rectangle rect = ((RectangleMapObject) object).getRectangle();
                    spikeModels.add(new Spike(rect.x, rect.y, rect.width, rect.height));
                }
            }
        }
        gameController.setSpikes(spikeModels);




        if (tiledMap.getLayers().get(levelType.getObjectLayerName()).getObjects().get("enemySpawn") != null) {
            Vector2 enemySpawn = mapHelper.getObjectPosition(levelType.getObjectLayerName(), "enemySpawn");
            gameController.spawnEnemy(new CrystalGuardian(enemySpawn.x, enemySpawn.y));
        }





        Player player = gameController.getPlayer();

        // Instantiate the scaling component widget
        SoulVesselWidget soulVessel = new SoulVesselWidget(player, vesselTexture, liquidTextures);

        // MASK SCALING: Scale factor matching the 75% size reduction
        float maskScale = 0.5f;
        float scaledMaskW = fullMaskTexture.getWidth() * maskScale;
        float scaledMaskH = fullMaskTexture.getHeight() * maskScale;

        // Construct the row of 5 layout health masks
        Table maskRowTable = new Table();
        for (int i = 0; i < 5; i++) {
            final int maskIndex = i;
            Image maskImage = new Image(fullMaskTexture) {
                @Override
                public void draw(Batch batch, float parentAlpha) {
                    if (maskIndex < player.getHealth()) {
                        setDrawable(new Image(fullMaskTexture).getDrawable());
                    } else {
                        setDrawable(new Image(emptyMaskTexture).getDrawable());
                    }
                    super.draw(batch, parentAlpha);
                }
            };
            maskRowTable.add(maskImage).size(scaledMaskW, scaledMaskH).padRight(4f * maskScale);
        }

        // =========================================================================
        // 💡 ARCHITECTURE FIX: Mount directly to the inherited rootTable container
        // =========================================================================
        // Tell the inherited base canvas to align its rows strictly to the top-left edge
        rootTable.top().left();

        // Drop the elements straight into the managed layout stream
        rootTable.add(soulVessel).top().padLeft(20f).padTop(10f);
        rootTable.add(maskRowTable).top().padLeft(10f).padTop(18f);

        // Remove the old stage.addActor(hudMasterTable) line!
        // AbstractScreen handles drawing the rootTable automatically.

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

//    private AnimationType getAnimationTypeForState(EntityState state) {
//        switch (state) {
//            case RUNNING:
//                return AnimationType.KnightRun;
//            case JUMPING:
//                return AnimationType.KnightRegularJump;
//            case DOUBLE_JUMPING:
//                return AnimationType.KnightDoubleJump;
//            case FALLING:
//                return AnimationType.KnightFall;
//            case LANDING:
//                return AnimationType.KnightRegularLanding;
//            case DASHING:
//                return AnimationType.KnightDash;
//            case ATTACKING:
//                return AnimationType.KnightNailSlash;
//            case POGO_ATTACKING:
//                return AnimationType.KnightPogo;
//            case WALL_SLIDING:
//                return AnimationType.KnightWallSlide;
//            case WALL_JUMPING:
//                return AnimationType.KnightWallJump;
//            case FOCUS_START:
//                return AnimationType.KnightFocusStart;
//            case FOCUS_LOOPING:
//                return AnimationType.KnightFocusLoop;
//            case FOCUS_GET:
//                return AnimationType.KnightFocusGet;
//            case FOCUS_END:
//                return AnimationType.KnightFocusEnd;
//            case IDLE:
//            default:
//                return AnimationType.KnightIdle;
//        }
//    }
    private TextureRegion getFrameFromAsset(AnimationType animationType, float visualTime, Entity entity) {
        TextureRegion frame = AssetManager.getAnimation(animationType).getKeyFrame(visualTime);

        if (entity.isLookingRight() && !frame.isFlipX()) frame.flip(true, false);
        else if (!entity.isLookingRight() && frame.isFlipX()) frame.flip(true, false);

        return frame;
    }

    // 💡 ADDED: Guarantees that visual effect sheets match the player's direction without turning backwards
    private TextureRegion getEffectFrame(AnimationType effectType, float visualTime, boolean assetNaturallyFacesRight) {
        TextureRegion frame = AssetManager.getAnimation(effectType).getKeyFrame(visualTime);
        if (frame == null) return null;

        boolean playerFacingRight = gameController.getPlayer().isLookingRight();

        if (assetNaturallyFacesRight) {
            // Right-facing assets (Dash, SideSlash) need to flip ONLY when looking left
            if (!playerFacingRight && !frame.isFlipX()) frame.flip(true, false);
            else if (playerFacingRight && frame.isFlipX()) frame.flip(true, false);
        } else {
            // Left-facing or symmetrical assets (DownSlash) follow standard character flipping
            if (playerFacingRight && !frame.isFlipX()) frame.flip(true, false);
            else if (!playerFacingRight && frame.isFlipX()) frame.flip(true, false);
        }

        return frame;
    }

    @Override
    public void renderWorld(float delta) {
        gameController.update(delta);

        float effectiveDelta = gameController.isPaused() ? 0f : delta;

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

            // =========================================================================
            // 💡 PURE OOP POLYMORPHISM: No map lookups, no weird interfaces.
            // =========================================================================
            AnimationType targetAnim = entity.getAnimationType();
            clock.update(targetAnim, effectiveDelta);
            if (entity == player && gameController.getInvincibilityTimer() > 0f) {
                if ((int)(gameController.getInvincibilityTimer() * 25) % 2 == 0) {
                    continue;
                }
            }

            TextureRegion frame = getFrameFromAsset(targetAnim, clock.getAnimTime(), entity);


            if (frame != null) {
                float spriteOffsetX = entity.getSpriteOffsetX(frame.getRegionWidth());
                float spriteOffsetY = entity.getSpriteOffsetY(frame.getRegionHeight());

                batch.draw(
                    frame,
                    entity.getPosition().x - spriteOffsetX,
                    entity.getPosition().y - spriteOffsetY
                );
                if (entity == player) {
                    TextureRegion effectFrame = null;
                    float effectX = player.getPosition().x;
                    float effectY = player.getPosition().y;

                    // 1. Normal Forward Side Slash Overlay
                    // 1. Normal Forward Side Slash Overlay
                    if (player.getCurrentState() == EntityState.ATTACKING) {
                        // 💡 FLIP FIX: Changed naturallyFacesRight from true to false to invert the slash arc orientation
                        effectFrame = getEffectFrame(AnimationType.SideSlashEffect, clock.getAnimTime(), false);

                        float effectWidth = effectFrame.getRegionWidth();

                        if (player.isLookingRight()) {
                            // 💡 Pushed slightly forward from player left-edge anchor
                            effectX += 15f;
                        } else {
                            // 💡 THE FIX: Subtract the width of the texture so it expands OUTWARD to the left symmetrically!
                            effectX -= (effectWidth - 25f);
                        }

                        effectY += 15f; // Vertical centering stabilizer
                    }

                    // 2. Downward Pogo Attack Slash Overlay
                    else if (player.getCurrentState() == EntityState.POGO_ATTACKING) {
                        effectFrame = getEffectFrame(AnimationType.DownSlashEffect, clock.getAnimTime(), false);

                        // Center the downward scoop right under the player's collision bounds
                        effectX += (player.getWidth() - effectFrame.getRegionWidth()) / 2f;
                        effectY -= (effectFrame.getRegionHeight() - 15f);
                    }

                    // 3. Directional Ghost Dash Trail Overlay
                    // 3. Directional Ghost Dash Trail Overlay
                    else if (player.getCurrentState() == EntityState.DASHING) {
                        effectFrame = getEffectFrame(AnimationType.DashEffect, clock.getAnimTime(), true);

                        if (player.isLookingRight()) {
                            // 💡 DASH RIGHT: Anchor the trail behind the player's left side
                            effectX = player.getPosition().x - 400f;
                        } else {
                            // 💡 DASH LEFT: Anchor the trail starting at the player's right edge (width = 40)
                            // and let it naturally expand backward to the right!
                            effectX = player.getPosition().x + player.getWidth() + 15f;
                        }

                        // Vertical alignment: Centers the dash wind relative to the player's height (90px)
                        effectY = player.getPosition().y + (player.getHeight() - effectFrame.getRegionHeight()) / 2f;
                    }

                    // Draw the visual effect if active
                    if (effectFrame != null) {
                        batch.draw(effectFrame, effectX, effectY);
                    }
                }

                // =========================================================================
                // 💡 BOSS ATTACK OVERLAY: Loops and mirrors the laser segment horizontally
                // =========================================================================
                if (entity instanceof CrystalGuardian) {
                    CrystalGuardian guardian = (CrystalGuardian) entity;

                    if (guardian.getEnemyState() == EnemyState.ATTACK) {
                        // Dynamically pull the active swirling animation frame
                        TextureRegion laserFrame = AssetManager.getAnimation(AnimationType.Crystallized_laser).getKeyFrame(clock.getAnimTime());

                        if (laserFrame != null) {
                            // 💡 THE FIXES: Infinite length, thicker beam, and proportional texture segments
                            float beamLength = 2500f;  // Shoots completely across the entire map screen
                            float beamHeight = 95f;    // Thicker, high-damage beam core
                            float segmentWidth = 190f; // Scaled up to keep the laser from looking squished

                            // Recalculate vertical alignment so the thicker beam stays centered on his chest
                            float laserY = guardian.getPosition().y + (guardian.getHeight() / 2f) - (beamHeight / 2f);
                            float currentX = guardian.getPosition().x;

                            boolean toggleMirror = false; // Tracks alternating flips for seamless seams

                            if (guardian.isLookingRight()) {
                                currentX += guardian.getWidth() - 15f; // Fire from right-hand side
                                float endX = currentX + beamLength;

                                while (currentX < endX) {
                                    if (laserFrame.isFlipX()) laserFrame.flip(true, false);
                                    if (toggleMirror) laserFrame.flip(true, false);

                                    batch.draw(laserFrame, currentX, laserY, segmentWidth, beamHeight);

                                    if (toggleMirror) laserFrame.flip(true, false);

                                    currentX += segmentWidth;
                                    toggleMirror = !toggleMirror;
                                }
                            } else {
                                currentX += 15f; // Fire from left-hand side
                                float endX = currentX - beamLength;

                                while (currentX > endX) {
                                    if (!laserFrame.isFlipX()) laserFrame.flip(true, false);
                                    if (toggleMirror) laserFrame.flip(true, false);

                                    batch.draw(laserFrame, currentX - segmentWidth, laserY, segmentWidth, beamHeight);

                                    if (toggleMirror) laserFrame.flip(true, false);

                                    currentX -= segmentWidth;
                                    toggleMirror = !toggleMirror;
                                }
                            }
                        }
                    }
                }
            }

        }
//        stage.setDebugAll(true);
        batch.end();















//        mapRenderer.render(foregroundLayers);
        mapRenderer.render(foregroundLayers);




        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(com.badlogic.gdx.graphics.Color.RED);

        for (Entity entity : gameController.getEntities()) {
            shapeRenderer.rect(
                entity.getPosition().x,
                entity.getPosition().y,
                entity.getWidth(),
                entity.getHeight()
            );
        }

        Rectangle activeAttack = gameController.getAttackBox();
        if (activeAttack != null) {
            shapeRenderer.setColor(com.badlogic.gdx.graphics.Color.CYAN);
            shapeRenderer.rect(
                activeAttack.x,
                activeAttack.y,
                activeAttack.width,
                activeAttack.height
            );
        }

        shapeRenderer.end();



//        shapeRenderer.setProjectionMatrix(camera.combined);

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
