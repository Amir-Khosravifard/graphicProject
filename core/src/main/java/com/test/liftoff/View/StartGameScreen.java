package com.test.liftoff.View;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.test.liftoff.Audio.SoundManager;
import com.test.liftoff.Enums.AudioType;

public class StartGameScreen extends AbstractScreen {
    @Override
    public void show() {
        super.show();

        SoundManager.playBackGroundMusic(AudioType.TITLE_THEME);


        Stack stack = new Stack();
        stack.setFillParent(true);
        Texture backGroundTexture = new Texture(Gdx.files.internal("BackGround/undefined - Imgur2.png"));
        Image backGroundImage = new Image(backGroundTexture);

        backGroundImage.setScaling(com.badlogic.gdx.utils.Scaling.fill);
        stack.add(backGroundImage);



        Table uiTable = new Table();




        Table menuBlockTable = new Table();
        menuBlockTable.right();




        Button enterNewGameButton = EnterGameButtonGenerator.generateButton(skin, "gameButton/select_game_HUD_0002_health_frame.png", "New Game");
        enterNewGameButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UIManager.changeScreen(new NewGameScreen());
            }
        });
        menuBlockTable.add(enterNewGameButton).right().padTop(150).row();

        Button enterSavedGameButton0 = EnterGameButtonGenerator.generateButton(skin, "gameButton/select_game_HUD_0002_health_frame.png", "Saved Game 1");
        menuBlockTable.add(enterSavedGameButton0).right().padTop(10).row();

        Button enterSavedGameButton1 = EnterGameButtonGenerator.generateButton(skin, "gameButton/select_game_HUD_0002_health_frame.png", "Saved Game 2");
        menuBlockTable.add(enterSavedGameButton1).right().padTop(10).row();

        Button enterSavedGameButton2 = EnterGameButtonGenerator.generateButton(skin, "gameButton/select_game_HUD_0002_health_frame.png", "Saved Game 3");
        menuBlockTable.add(enterSavedGameButton2).right().padTop(10).row();

        Button enterSavedGameButton3 = EnterGameButtonGenerator.generateButton(skin, "gameButton/select_game_HUD_0002_health_frame.png", "Saved Game 4");
        menuBlockTable.add(enterSavedGameButton3).right().padTop(10).row();


        uiTable.add(menuBlockTable).padRight(100).center().row();


        TextButton backButton = createBackButton(new MainMenuScreen());


        uiTable.add(backButton).expand().bottom().left().pad(10);

//        stage.setDebugAll(true);


        stack.add(uiTable);

        rootTable.add(stack).grow().minSize(0);
        Gdx.input.setInputProcessor(stage);

    }

}
