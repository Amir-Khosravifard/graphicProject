package com.test.liftoff.View;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.test.liftoff.Audio.SoundManager;
import com.test.liftoff.Controller.GameController;
import com.test.liftoff.Enums.*;
import com.test.liftoff.Model.Enemy.*;
import com.test.liftoff.Model.Entity.Entity;
import com.test.liftoff.Model.Entity.Player;
import com.test.liftoff.Model.Entity.Spike;
import com.test.liftoff.Model.Entity.Zote;
import com.test.liftoff.Tiled.TiledMapHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class GameScreen extends AbstractScreen {
    private SpriteBatch batch;
    private Camera camera;
    private Viewport viewport;
    private ShapeRenderer shapeRenderer;
    private GameProcessor gameProcessor;
    private static GameController staticController;
    private GameController gameController;
    private LevelType levelType;
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer mapRenderer;
    private HashMap<Entity, RenderClock> visualClocks = new HashMap<>();
    private Texture fullMaskTexture;
    private Texture emptyMaskTexture;
    private Texture vesselTexture;
    private Texture[] liquidTextures;
    private Texture bgTexture;
    private int[] backgroundLayers;
    private int[] foregroundLayers;
    private float mapPixelWidth;
    private float mapPixelHeight;
    private float shakeDuration = 0f;
    private final float shakeIntensity = 8f;
    private boolean winModalOpened = false;

    private static float typewriterTimer = 0f;
    private static int visibleCharsCount = 0;
    private Table dialogueOverlayBox;
    private Label dialogueTextLabel;

    public GameScreen(GameController gameController, LevelType levelType) {
        this.gameController = gameController;
        staticController = gameController;
        this.levelType = levelType;
        this.gameController.setLevelType(levelType);
    }

    public static void resetTypewriterEffect() {
        visibleCharsCount = 0;
        typewriterTimer = 0f;
        playRandomZoteVocalGrunt();
    }

    public static void advanceOrSkipDialogueLine() {
        String fullLineText = staticController.getCurrentDialogueText();
        if (visibleCharsCount < fullLineText.length()) {
            visibleCharsCount = fullLineText.length();
        } else {
            staticController.advanceDialogue();
            visibleCharsCount = 0;
            typewriterTimer = 0f;
            if (staticController.isDialogueActive()) {
                playRandomZoteVocalGrunt();
            }
        }
    }

    private static void playRandomZoteVocalGrunt() {
        int choice = MathUtils.random(1, 3);
        if (choice == 1) SoundManager.playSound(AudioType.ZOTE_01);
        else if (choice == 2) SoundManager.playSound(AudioType.ZOTE_02);
        else SoundManager.playSound(AudioType.ZOTE_03);
    }

    private String findLayerNameIgnoreCase(String targetedName) {
        for (MapLayer layer : tiledMap.getLayers()) {
            if (layer.getName().equalsIgnoreCase(targetedName)) {
                return layer.getName();
            }
        }
        return targetedName;
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
            liquidTextures[i] = new Texture(Gdx.files.internal("animations/animation/HUD/liquidTexture/" + (i) + ".png"));
        }
        if (levelType.getBgPath() != null) {
            bgTexture = new Texture(Gdx.files.internal(levelType.getBgPath()));
        }
        TiledMapHelper mapHelper = new TiledMapHelper();
        tiledMap = mapHelper.loadMap(levelType.getMapPath());
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        MapProperties prop = tiledMap.getProperties();
        int mapWidth = prop.get("width", Integer.class);
        int tilePixelWidth = prop.get("tilewidth", Integer.class);
        int mapHeight = prop.get("height", Integer.class);
        int tilePixelHeight = prop.get("tileheight", Integer.class);
        mapPixelWidth = mapWidth * tilePixelWidth;
        mapPixelHeight = mapHeight * tilePixelHeight;
        ArrayList<Integer> bgIndices = new ArrayList<>();
        ArrayList<Integer> fgIndices = new ArrayList<>();
        for (int i = 0; i < tiledMap.getLayers().getCount(); i++) {
            String name = tiledMap.getLayers().get(i).getName();
            if (name.equals("BackLayer") || name.equals("main")) {
                bgIndices.add(i);
            } else if (name.equals("FrontLayer")) {
                fgIndices.add(i);
            }
        }
        backgroundLayers = new int[bgIndices.size()];
        for (int i = 0; i < bgIndices.size(); i++) backgroundLayers[i] = bgIndices.get(i);
        foregroundLayers = new int[fgIndices.size()];
        for (int i = 0; i < fgIndices.size(); i++) foregroundLayers[i] = fgIndices.get(i);
        String actualObstaclesLayer = findLayerNameIgnoreCase("Obstacles");
        String actualSpikesLayer = findLayerNameIgnoreCase("Spikes");
        String actualPlayerSpawnLayer = findLayerNameIgnoreCase("playerSpawnPoints");
        String actualEnemySpawnLayer = findLayerNameIgnoreCase("enemySpawnPoints");
        String actualBossRoomLayer = findLayerNameIgnoreCase("bossRoom");
        String actualCameraLayer = findLayerNameIgnoreCase("camera");
        String actualZoteLayer = findLayerNameIgnoreCase("zote");

        ArrayList<Rectangle> platforms = mapHelper.getCollisionRectangles(actualObstaclesLayer);
        gameController.setPlatforms(platforms);
        ArrayList<Rectangle> bossRoomTriggers = mapHelper.getCollisionRectangles(actualBossRoomLayer);
        gameController.setBossRoomTriggers(bossRoomTriggers);
        if (tiledMap.getLayers().get(actualCameraLayer) != null) {
            Vector2 staticCamTarget = mapHelper.getObjectPosition(actualCameraLayer, "camera");
            gameController.setBossCameraPosition(staticCamTarget.x, staticCamTarget.y);
        }

        if (tiledMap.getLayers().get(actualZoteLayer) != null) {
            try {
                Vector2 zoteSpawn = mapHelper.getObjectPosition(actualZoteLayer, "zote");
                if (zoteSpawn != null && zoteSpawn.x > 100) {
                    Zote zoteObj = new Zote(zoteSpawn.x, zoteSpawn.y);
                    gameController.setZoteNPC(zoteObj);
                    gameController.getEntities().add(zoteObj);
                }
            } catch (Exception ignored) {
            }
        }

        ArrayList<Spike> spikeModels = new ArrayList<>();
        if (tiledMap.getLayers().get(actualSpikesLayer) != null) {
            for (MapObject object : tiledMap.getLayers().get(actualSpikesLayer).getObjects()) {
                if (object instanceof RectangleMapObject) {
                    Rectangle rect = ((RectangleMapObject) object).getRectangle();
                    spikeModels.add(new Spike(rect.x, rect.y, rect.width, rect.height));
                }
            }
        }
        gameController.setSpikes(spikeModels);
        ArrayList<Vector2> checkpointPositions = new ArrayList<>();
        if (tiledMap.getLayers().get(actualPlayerSpawnLayer) != null) {
            for (MapObject object : tiledMap.getLayers().get(actualPlayerSpawnLayer).getObjects()) {
                Float xProp = object.getProperties().get("x", Float.class);
                Float yProp = object.getProperties().get("y", Float.class);
                if (xProp != null && yProp != null) {
                    checkpointPositions.add(new Vector2(xProp, yProp));
                }
            }
        }
        gameController.setCheckpointPositions(checkpointPositions);
        if (tiledMap.getLayers().get(actualPlayerSpawnLayer) != null) {
            Vector2 startSpawn = mapHelper.getObjectPosition(actualPlayerSpawnLayer, levelType.getSpawnPointName());
            gameController.setSpawnPoint(startSpawn);
            if (gameController.getPlayer().getPosition().x == 0 && gameController.getPlayer().getPosition().y == 0) {
                gameController.getPlayer().getPosition().set(startSpawn);
                gameController.updateCheckpoint(startSpawn);
            } else {
                Vector2 loadedPos = gameController.getPlayer().getPosition();
                gameController.updateCheckpoint(loadedPos);
            }
            camera.position.set(gameController.getPlayer().getPosition().x, gameController.getPlayer().getPosition().y, 0);
            camera.update();
        }
        if (tiledMap.getLayers().get(actualEnemySpawnLayer) != null) {
            for (MapObject object : tiledMap.getLayers().get(actualEnemySpawnLayer).getObjects()) {
                Float xProp = object.getProperties().get("x", Float.class);
                Float yProp = object.getProperties().get("y", Float.class);
                if (xProp == null || yProp == null) continue;
                float x = xProp;
                float y = yProp;
                String name = object.getName();
                if (name != null) {
                    name = name.trim();
                    gameController.recordEnemySpawn(name, x, y);
                    if ("boss".equalsIgnoreCase(name)) {
                        gameController.spawnEnemy(new FalseKnight(x, y));
                    } else if ("crawlid".equalsIgnoreCase(name)) {
                        gameController.spawnEnemy(new Crawlid(x, y));
                    } else if ("hornhead".equalsIgnoreCase(name)) {
                        gameController.spawnEnemy(new Hornhead(x, y));
                    } else if ("mosquito".equalsIgnoreCase(name)) {
                        gameController.spawnEnemy(new Mosquito(x, y));
                    } else if ("crystalGuardian".equalsIgnoreCase(name)) {
                        gameController.spawnEnemy(new CrystalGuardian(x, y));
                    }
                }
            }
        }
        Player player = gameController.getPlayer();
        SoulVesselWidget soulVessel = new SoulVesselWidget(player, vesselTexture, liquidTextures);
        float maskScale = 0.5f;
        float scaledMaskW = fullMaskTexture.getWidth() * maskScale;
        float scaledMaskH = fullMaskTexture.getHeight() * maskScale;
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


        rootTable.top().left();

        Table hudTable = new Table();
        hudTable.top().left();
        hudTable.add(soulVessel).top().padLeft(20f).padTop(10f);
        hudTable.add(maskRowTable).top().padLeft(10f).padTop(18f);


        rootTable.add(hudTable).left().top().expandX().fillX().row();

        dialogueOverlayBox = new Table();
        dialogueOverlayBox.setBackground(skin.getDrawable("window"));
        dialogueOverlayBox.pad(20);

        Label.LabelStyle nameStyle = new Label.LabelStyle(skin.getFont("font"), new Color(0.55f, 0.05f, 0.05f, 1f));
        Label nameLabel = new Label("ZOTE THE MIGHTY", nameStyle);
        nameLabel.setFontScale(0.9f);
        dialogueOverlayBox.add(nameLabel).left().row();

        Label.LabelStyle textStyle = new Label.LabelStyle(skin.getFont("font"), Color.BLACK);
        dialogueTextLabel = new Label("", textStyle);
        dialogueTextLabel.setFontScale(0.75f);
        dialogueTextLabel.setWrap(true);
        dialogueOverlayBox.add(dialogueTextLabel).left().width(800f).padTop(10).row();

        Label.LabelStyle hintStyle = new Label.LabelStyle(skin.getFont("font"), new Color(0.2f, 0.2f, 0.2f, 1f));
        Label nextHint = new Label("[Press ENTER to Advance]", hintStyle);
        nextHint.setFontScale(0.55f);
        dialogueOverlayBox.add(nextHint).right().padTop(10);

        dialogueOverlayBox.setVisible(false);


        rootTable.row().expandY().bottom().padBottom(30);
        rootTable.add(dialogueOverlayBox).width(920f).height(170f).center();

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

    private TextureRegion getFrameFromAsset(AnimationType animationType, float visualTime, Entity entity) {
        TextureRegion frame = AssetManager.getAnimation(animationType).getKeyFrame(visualTime);
        if (entity.isLookingRight() && !frame.isFlipX()) frame.flip(true, false);
        else if (!entity.isLookingRight() && frame.isFlipX()) frame.flip(true, false);
        return frame;
    }

    private TextureRegion getEffectFrame(AnimationType effectType, float visualTime, boolean assetNaturallyFacesRight) {
        TextureRegion frame = AssetManager.getAnimation(effectType).getKeyFrame(visualTime);
        if (frame == null) return null;
        boolean playerFacingRight = gameController.getPlayer().isLookingRight();
        if (assetNaturallyFacesRight) {
            if (!playerFacingRight && !frame.isFlipX()) frame.flip(true, false);
            else if (playerFacingRight && frame.isFlipX()) frame.flip(true, false);
        } else {
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
        if (gameController.checkAndPopCameraShake()) {
            shakeDuration = 0.2f;
        }
        if (gameController.isBossCameraLocked()) {
            if (camera instanceof OrthographicCamera) {
                ((OrthographicCamera) camera).zoom = MathUtils.lerp(((OrthographicCamera) camera).zoom, 1.35f, 4f * effectiveDelta);
            }
            Vector2 staticTarget = gameController.getBossCameraPosition();
            camera.position.x = MathUtils.lerp(camera.position.x, staticTarget.x, 4f * effectiveDelta);
            camera.position.y = MathUtils.lerp(camera.position.y, staticTarget.y, 4f * effectiveDelta);
        } else {
            if (camera instanceof OrthographicCamera) {
                ((OrthographicCamera) camera).zoom = MathUtils.lerp(((OrthographicCamera) camera).zoom, 1.0f, 4f * effectiveDelta);
            }
            float targetX = player.getPosition().x + player.getWidth() / 2f;
            float targetY = player.getPosition().y + player.getHeight() / 2f;
            camera.position.x = MathUtils.lerp(camera.position.x, targetX, 5f * effectiveDelta);
            camera.position.y = MathUtils.lerp(camera.position.y, targetY, 5f * effectiveDelta);
            float currentZoom = (camera instanceof OrthographicCamera) ? ((OrthographicCamera) camera).zoom : 1.0f;
            float camHalfW = (camera.viewportWidth * currentZoom) / 2f;
            float camHalfH = (camera.viewportHeight * currentZoom) / 2f;
            camera.position.x = MathUtils.clamp(camera.position.x, camHalfW, mapPixelWidth - camHalfW);
            camera.position.y = MathUtils.clamp(camera.position.y, camHalfH, mapPixelHeight - camHalfH);
        }
        if (shakeDuration > 0f) {
            shakeDuration -= effectiveDelta;
            camera.position.x += MathUtils.random(-shakeIntensity, shakeIntensity);
            camera.position.y += MathUtils.random(-shakeIntensity, shakeIntensity);
        }
        camera.update();
        if (bgTexture != null) {
            batch.setProjectionMatrix(camera.combined);
            batch.begin();
            float currentZoom = (camera instanceof OrthographicCamera) ? ((OrthographicCamera) camera).zoom : 1.0f;
            batch.draw(
                bgTexture,
                camera.position.x - (camera.viewportWidth * currentZoom) / 2f,
                camera.position.y - (camera.viewportHeight * currentZoom) / 2f,
                camera.viewportWidth * currentZoom,
                camera.viewportHeight * currentZoom
            );
            batch.end();
        }
        mapRenderer.setView((OrthographicCamera) camera);
        mapRenderer.render(backgroundLayers);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (Entity entity : gameController.getEntities()) {
            if (!visualClocks.containsKey(entity)) {
                visualClocks.put(entity, new RenderClock());
            }
            RenderClock clock = visualClocks.get(entity);
            AnimationType targetAnim = entity.getAnimationType();
            if (entity instanceof Enemy && ((Enemy) entity).isDead() &&
                AssetManager.getAnimation(targetAnim).isAnimationFinished(clock.getAnimTime())) {
            } else {
                clock.update(targetAnim, effectiveDelta);
            }
            boolean skipPlayerBodyDraw = false;
            if (entity == player && gameController.getInvincibilityTimer() > 0f) {
                if ((int) (gameController.getInvincibilityTimer() * 25) % 2 == 0) {
                    skipPlayerBodyDraw = true;
                }
            }
            TextureRegion frame = getFrameFromAsset(targetAnim, clock.getAnimTime(), entity);
            if (frame != null) {
                float spriteOffsetX = entity.getSpriteOffsetX(frame.getRegionWidth());
                float spriteOffsetY = entity.getSpriteOffsetY(frame.getRegionHeight());
                if (!skipPlayerBodyDraw) {
                    batch.draw(
                        frame,
                        entity.getPosition().x - spriteOffsetX,
                        entity.getPosition().y - spriteOffsetY
                    );
                }
                if (entity == player) {
                    TextureRegion effectFrame = null;
                    float effectX = player.getPosition().x;
                    float effectY = player.getPosition().y;
                    if (player.getCurrentState() == EntityState.ATTACKING) {
                        effectFrame = getEffectFrame(AnimationType.SideSlashEffect, clock.getAnimTime(), false);
                        float effectWidth = effectFrame.getRegionWidth();
                        if (player.isLookingRight()) effectX += 15f;
                        else effectX -= (effectWidth - 25f);
                        effectY += 15f;
                    } else if (player.getCurrentState() == EntityState.POGO_ATTACKING) {
                        effectFrame = getEffectFrame(AnimationType.DownSlashEffect, clock.getAnimTime(), false);
                        effectX += (player.getWidth() - effectFrame.getRegionWidth()) / 2f;
                        effectY -= (effectFrame.getRegionHeight() - 15f);
                    } else if (player.getCurrentState() == EntityState.DASHING) {
                        effectFrame = getEffectFrame(AnimationType.DashEffect, clock.getAnimTime(), true);
                        if (player.isLookingRight()) effectX = player.getPosition().x - 400f;
                        else effectX = player.getPosition().x + player.getWidth() + 15f;
                        effectY = player.getPosition().y + (player.getHeight() - effectFrame.getRegionHeight()) / 2f;
                    }
                    if (effectFrame != null) batch.draw(effectFrame, effectX, effectY);
                }
                if (entity instanceof CrystalGuardian) {
                    CrystalGuardian guardian = (CrystalGuardian) entity;
                    if (guardian.getEnemyState() == EnemyState.ATTACK) {
                        TextureRegion laserFrame = AssetManager.getAnimation(AnimationType.Crystallized_laser).getKeyFrame(clock.getAnimTime());
                        if (laserFrame != null) {
                            float beamLength = 2500f;
                            float beamHeight = 95f;
                            float segmentWidth = 190f;
                            float laserY = guardian.getPosition().y + (guardian.getHeight() / 2f) - (beamHeight / 2f);
                            float currentX = guardian.getPosition().x;
                            boolean toggleMirror = false;
                            if (guardian.isLookingRight()) {
                                currentX += guardian.getWidth() - 15f;
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
                                currentX += 15f;
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

        if (gameController.isNearZote() && !gameController.isDialogueActive() && gameController.getZoteNPC() != null) {
            Zote z = gameController.getZoteNPC();
            BitmapFont font = skin.getFont("font");
            font.setColor(Color.YELLOW);
            font.draw(batch, "Press E to Listen", z.getPosition().x - 20f, z.getPosition().y + z.getHeight() + 75f);
        }
        batch.end();

        if (gameController.isDialogueActive()) {
            dialogueOverlayBox.setVisible(true);
            String fullTargetText = gameController.getCurrentDialogueText();
            typewriterTimer += effectiveDelta;
            if (typewriterTimer >= 0.035f) {
                typewriterTimer = 0f;
                if (visibleCharsCount < fullTargetText.length()) {
                    visibleCharsCount++;
                }
            }
            dialogueTextLabel.setText(fullTargetText.substring(0, visibleCharsCount));
        } else {
            dialogueOverlayBox.setVisible(false);
        }

        mapRenderer.render(foregroundLayers);
        if (gameController.isGameWon() && !winModalOpened) {
            winModalOpened = true;
            WinModal winModal = new WinModal(
                gameController.getDeathCount(),
                gameController.getKillCount(),
                gameController.getTotalPlayTime(),
                new Runnable() {
                    @Override
                    public void run() {
                        winModalOpened = false;
                        Player freshPlayer = new Player();
                        GameController freshController = new GameController(freshPlayer);
                        freshController.setActiveSlot(gameController.getActiveSlot());
                        UIManager.changeScreen(new GameScreen(freshController, levelType));
                    }
                }
            );
            winModal.show();
        }
//        shapeRenderer.setProjectionMatrix(camera.combined);
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//        shapeRenderer.setColor(com.badlogic.gdx.graphics.Color.RED);
//        for (Entity entity : gameController.getEntities()) {
//            shapeRenderer.rect(entity.getPosition().x, entity.getPosition().y, entity.getWidth(), entity.getHeight());
//        }
//        Rectangle activeAttack = gameController.getAttackBox();
//        if (activeAttack != null) {
//            shapeRenderer.setColor(com.badlogic.gdx.graphics.Color.CYAN);
//            shapeRenderer.rect(activeAttack.x, activeAttack.y, activeAttack.width, activeAttack.height);
//        }
//        for (Entity entity : gameController.getEntities()) {
//            if (entity instanceof FalseKnight) {
//                com.badlogic.gdx.math.Rectangle hammerBox = gameController.getFalseKnightHammerBox((FalseKnight) entity);
//                if (hammerBox != null) {
//                    shapeRenderer.rect(hammerBox.x, hammerBox.y, hammerBox.width, hammerBox.height);
//                }
//            }
//        }
//        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (bgTexture != null) bgTexture.dispose();
        if (fullMaskTexture != null) fullMaskTexture.dispose();
        if (emptyMaskTexture != null) emptyMaskTexture.dispose();
        if (vesselTexture != null) vesselTexture.dispose();
        if (liquidTextures != null) {
            for (Texture t : liquidTextures) if (t != null) t.dispose();
        }
        if (mapRenderer != null) mapRenderer.dispose();
        if (tiledMap != null) tiledMap.dispose();
        if (batch != null) batch.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
    }
}
