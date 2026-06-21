package com.test.liftoff.View;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class SettingsScreen extends AbstractScreen{
    @Override
    public void show() {
        super.show();
        Stack stack = new Stack();
        stack.setFillParent(true);
        Texture backGroundTexture = new Texture(Gdx.files.internal("BackGround/startGameBackGround.png"));
        Image backGroundImage = new Image(backGroundTexture);
        stack.add(backGroundImage);


        Table uiTable = new Table();
        TextButton backButton = createBackButton(new MainMenuScreen());

        uiTable.add(backButton).expand().bottom().left().pad(10);

        stack.add(uiTable);

        stage.addActor(stack);
        Gdx.input.setInputProcessor(stage);
    }
}
