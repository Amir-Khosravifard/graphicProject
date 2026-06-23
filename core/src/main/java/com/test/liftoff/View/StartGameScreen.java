package com.test.liftoff.View;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class StartGameScreen extends AbstractScreen {
    @Override
    public void show() {
        super.show();
        Stack stack = new Stack();
        stack.setFillParent(true);
        Texture backGroundTexture = new Texture(Gdx.files.internal("BackGround/startGameBackGround.png"));
        Image backGroundImage = new Image(backGroundTexture);
        stack.add(backGroundImage);



        Table uiTable = new Table();




        Table menuBlockTable = new Table();
        menuBlockTable.right();




        Button enterNewGameButton = EnterGameButtonGenerator.generateButton(skin, "gameButton/select_game_HUD_0002_health_frame.png", "New Game");
        enterNewGameButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UIManager.changeScreen(new GameScreen());
            }
        });
        menuBlockTable.add(enterNewGameButton).right().padTop(150).row();

        Button enterSavedGameButton = EnterGameButtonGenerator.generateButton(skin, "gameButton/select_game_HUD_godseeker.png", "New Game");
        menuBlockTable.add(enterSavedGameButton).right().padTop(10).row();

        Button enterSavedGameButton1 = EnterGameButtonGenerator.generateButton(skin, "gameButton/select_game_HUD_Steel_Soul.png", "New Game");
        menuBlockTable.add(enterSavedGameButton1).right().padTop(10).row();


        uiTable.add(menuBlockTable).padRight(300).center().row();


        TextButton backButton = createBackButton(new MainMenuScreen());


        uiTable.add(backButton).expand().bottom().left().pad(10);

//        stage.setDebugAll(true);


        stack.add(uiTable);

        rootTable.add(stack).grow().minSize(0);
        Gdx.input.setInputProcessor(stage);

    }

}
