package com.test.liftoff.View;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class StartGameScreen extends AbstractScreen {
    @Override
    public void show() {
        super.show();

        Stack stack = new Stack();
        stack.setFillParent(true);


        Table uiTable = new Table();




        Table menuBlockTable = new Table();
        menuBlockTable.right();




        Button enterNewGameButton = EnterGameButtonGenerator.generateButton(skin, "gameButton/select_game_HUD_0002_health_frame.png", "New Game");
        menuBlockTable.add(enterNewGameButton).right().padTop(150).row();

        Button enterSavedGameButton = EnterGameButtonGenerator.generateButton(skin, "gameButton/select_game_HUD_godseeker.png", "New Game");
        menuBlockTable.add(enterSavedGameButton).right().padTop(10).row();

        Button enterSavedGameButton1 = EnterGameButtonGenerator.generateButton(skin, "gameButton/select_game_HUD_Steel_Soul.png", "New Game");
        menuBlockTable.add(enterSavedGameButton1).right().padTop(10).row();


        uiTable.add(menuBlockTable).padRight(300).center().row();



        Texture backGroundTexture = new Texture(Gdx.files.internal("BackGround/startGameBackGround.png"));
        Image backGroundImage = new Image(backGroundTexture);
        stack.add(backGroundImage);
        TextButton backButton = new TextButton("Back", skin);



        uiTable.add(backButton).expand().bottom().left().pad(10);

//        stage.setDebugAll(true);


        stack.add(uiTable);

        stage.addActor(stack);
        Gdx.input.setInputProcessor(stage);

        backButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UIManager.changeScreen(new MainMenuScreen());
            }
        });
    }
}
