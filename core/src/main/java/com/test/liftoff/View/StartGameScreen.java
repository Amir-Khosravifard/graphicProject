package com.test.liftoff.View;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.test.liftoff.Audio.SoundManager;
import com.test.liftoff.Controller.GameController;
import com.test.liftoff.Controller.SaveManager;
import com.test.liftoff.Enums.AudioType;
import com.test.liftoff.Enums.LevelType;
import com.test.liftoff.Model.Entity.Player;
import com.test.liftoff.Model.SaveData;

import java.util.ArrayList;

public class StartGameScreen extends AbstractScreen {
    @Override
    public void show() {
        super.show();
        SoundManager.playBackGroundMusic(AudioType.TITLE_THEME);
        Stack stack = new Stack();
        stack.setFillParent(true);
        Texture backGroundTexture = new Texture(Gdx.files.internal("BackGround/undefined - Imgur2.png"));
        Image backGroundImage = new Image(backGroundTexture);
        backGroundImage.setScaling(Scaling.fill);
        stack.add(backGroundImage);
        Table uiTable = new Table();
        Table menuBlockTable = new Table();
        menuBlockTable.right();

        Button enterNewGameButton = EnterGameButtonGenerator.generateButton(skin, "gameButton/select_game_HUD_0002_health_frame.png", "New Game");
        enterNewGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UIManager.changeScreen(new NewGameScreen());
            }
        });
        menuBlockTable.add(enterNewGameButton).right().padTop(150).row();


        ArrayList<SaveManager.SaveSlotInfo> sortedSlots = SaveManager.getAllSlotsSorted();

        for (SaveManager.SaveSlotInfo slotInfo : sortedSlots) {
            final int slotId = slotInfo.slotId;
            String textLabel = slotInfo.exists ? "Load Saved Game " + slotId : "Empty Slot " + slotId;
            Button enterSavedGameButton = EnterGameButtonGenerator.generateButton(skin, "gameButton/select_game_HUD_0002_health_frame.png", textLabel);

            enterSavedGameButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Player player = new Player();
                    GameController controller = new GameController(player);
                    controller.setActiveSlot(slotId);

                    if (SaveManager.hasSave(slotId)) {
                        SaveData savedData = SaveManager.load(slotId);
                        LevelType targetLevel = LevelType.CROSSROADS;
                        try {
                            targetLevel = LevelType.valueOf(savedData.levelName);
                        } catch (Exception ex) {
                            targetLevel = LevelType.CROSSROADS;
                        }

                        player.setHealth(savedData.health);
                        player.setSoul(savedData.soul);
                        player.getPosition().set(savedData.checkpointX, savedData.checkpointY);
                        UIManager.changeScreen(new GameScreen(controller, targetLevel));
                    } else {
                        UIManager.changeScreen(new GameScreen(controller, LevelType.CROSSROADS));
                    }
                }
            });
            menuBlockTable.add(enterSavedGameButton).right().padTop(10).row();
        }

        uiTable.add(menuBlockTable).padRight(100).center().row();
        TextButton backButton = createBackButton(new MainMenuScreen());
        uiTable.add(backButton).expand().bottom().left().pad(10);
        stack.add(uiTable);
        rootTable.add(stack).grow().minSize(0);
        Gdx.input.setInputProcessor(stage);
    }
}
