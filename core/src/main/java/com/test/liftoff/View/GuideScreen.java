package com.test.liftoff.View;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.test.liftoff.Audio.SoundManager;
import com.test.liftoff.Enums.AudioType;

public class GuideScreen extends AbstractScreen{
    @Override
    public void show() {
        super.show();

        SoundManager.playBackGroundMusic(AudioType.TITLE_THEME);


        Stack stack = new Stack();
        stack.setFillParent(true);
        Texture backGroundTexture = new Texture(Gdx.files.internal("BackGround/undefined - Imgur1.png"));
        Image backGroundImage = new Image(backGroundTexture);

        backGroundImage.setScaling(com.badlogic.gdx.utils.Scaling.fill);
        stack.add(backGroundImage);


        Table uiTable = new Table();
        TextButton backButton = createBackButton(new MainMenuScreen());

        uiTable.add(backButton).expand().bottom().left().pad(10);

        stack.add(uiTable);

        rootTable.add(stack).grow().minSize(0);
        Gdx.input.setInputProcessor(stage);
    }
}
