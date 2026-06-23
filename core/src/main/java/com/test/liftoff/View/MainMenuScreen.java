package com.test.liftoff.View;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen extends AbstractScreen {
    @Override
    public void show() {
        super.show();

        Stack stack = new Stack();


        Texture backGroundTexture = new Texture(Gdx.files.internal("BackGround/Voidheart_menu_BG.png"));
        Image backGroundImage = new Image(backGroundTexture);
        stack.add(backGroundImage);

//        stack.add(logoImage);

        Table uiTable = new Table();

        Texture logoTexture = new Texture(Gdx.files.internal("BackGround/vheart_title.png"));
        Image logoImage = new Image(logoTexture);
        uiTable.add(logoImage).size(logoTexture.getWidth(), logoTexture.getHeight()).center().padBottom(-60).row();

        TextButton startGameButton = new TextButton("Start Game", skin);
        startGameButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UIManager.changeScreen(new StartGameScreen());
            }
        });
        uiTable.add(startGameButton).center().pad(10).row();

        TextButton settingButton = new TextButton("Settings", skin);
        settingButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UIManager.changeScreen(new SettingsScreen());
            }
        });
        uiTable.add(settingButton).center().pad(10).row();

        TextButton guideButton = new TextButton("Guide", skin);
        guideButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UIManager.changeScreen(new GuideScreen());
            }
        });
        uiTable.add(guideButton).center().pad(10).row();

        TextButton achievementsButton = new TextButton("Achievements", skin);
        achievementsButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UIManager.changeScreen(new AchievementsScreen());
            }
        });
        uiTable.add(achievementsButton).center().pad(10).row();

        TextButton exitButton = new TextButton("Exit", skin);
        exitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        uiTable.add(exitButton).center().pad(10).padBottom(25).row();
//        uiTable.setDebug(true);

        stack.add(uiTable);

        rootTable.add(stack).grow().minSize(0);

        Gdx.input.setInputProcessor(stage);




    }

}
