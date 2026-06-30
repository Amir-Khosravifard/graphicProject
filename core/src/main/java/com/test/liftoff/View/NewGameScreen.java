package com.test.liftoff.View;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.test.liftoff.Controller.GameController;
import com.test.liftoff.Model.Entity.Player;

public class NewGameScreen extends AbstractScreen{
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




        Button enterNewGameButton0 = EnterGameButtonGenerator.generateButton(skin, "gameButton/select_game_HUD_godseeker.png", "Forgotten Crossroads");
        enterNewGameButton0.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UIManager.changeScreen(new GameScreen(new GameController(new Player())));
            }
        });
        menuBlockTable.add(enterNewGameButton0).right().padTop(150).row();


        Button enterNewGameButton1 = EnterGameButtonGenerator.generateButton(skin, "gameButton/select_game_HUD_Steel_Soul.png", "GreenPath");
        enterNewGameButton1.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UIManager.changeScreen(new GameScreen(new GameController(new Player())));
            }
        });
        menuBlockTable.add(enterNewGameButton1).right().row();

        uiTable.add(menuBlockTable).padRight(100).center().row();



        TextButton backButton = createBackButton(new MainMenuScreen());


        uiTable.add(backButton).expand().bottom().left().pad(10);

        stack.add(uiTable);

        rootTable.add(stack).grow().minSize(0);
        Gdx.input.setInputProcessor(stage);
    }
}
