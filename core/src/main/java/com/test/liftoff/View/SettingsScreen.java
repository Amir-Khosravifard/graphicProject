package com.test.liftoff.View;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.test.liftoff.Audio.SoundManager;
import com.test.liftoff.Enums.AudioType;

public class SettingsScreen extends AbstractScreen{
    @Override
    public void show() {
        super.show();

        SoundManager.playBackGroundMusic(AudioType.TITLE_THEME);


        Stack stack = new Stack();
        stack.setFillParent(true);

        Texture backGroundTexture = new Texture(Gdx.files.internal("BackGround/undefined - Imgur3.png"));
        Image backGroundImage = new Image(backGroundTexture);
        backGroundImage.setScaling(com.badlogic.gdx.utils.Scaling.fill);
        stack.add(backGroundImage);

        Table uiTable = new Table();
        uiTable.center();

        Label titleLabel = new Label("SETTINGS", skin);
        uiTable.add(titleLabel).padBottom(40).colspan(2).row();

        Label volumeLabel = new Label("Music Volume: ", skin);
        Slider volumeSlider = new Slider(0f, 1f, 0.05f, false, skin);
        volumeSlider.setValue(SoundManager.getMusicVolume());
        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SoundManager.setMusicVolume(volumeSlider.getValue());
            }
        });
        uiTable.add(volumeLabel).left().pad(10);
        uiTable.add(volumeSlider).width(300).pad(10).row();

        Label musicStatusLabel = new Label("Music Stream: ", skin);
        TextButton musicToggleBtn = new TextButton(SoundManager.isMusicEnabled() ? "ON" : "OFF", skin);
        musicToggleBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean nextState = !SoundManager.isMusicEnabled();
                SoundManager.setMusicEnabled(nextState);
                musicToggleBtn.setText(nextState ? "ON" : "OFF");
            }
        });
        uiTable.add(musicStatusLabel).left().pad(10);
        uiTable.add(musicToggleBtn).width(150).pad(10).row();

        Label sfxStatusLabel = new Label("SFX Audio: ", skin);
        final TextButton sfxToggleBtn = new TextButton(SoundManager.isSfxEnabled() ? "ON" : "OFF", skin);
        sfxToggleBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean nextState = !SoundManager.isSfxEnabled();
                SoundManager.setSfxEnabled(nextState);
                sfxToggleBtn.setText(nextState ? "ON" : "OFF");
            }
        });
        uiTable.add(sfxStatusLabel).left().pad(10);
        uiTable.add(sfxToggleBtn).width(150).pad(10).row();

        TextButton resetButton = new TextButton("Reset to Defaults", skin);
        resetButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SoundManager.resetToDefaults();
                volumeSlider.setValue(SoundManager.getMusicVolume());
                musicToggleBtn.setText(SoundManager.isMusicEnabled() ? "ON" : "OFF");
                sfxToggleBtn.setText(SoundManager.isSfxEnabled() ? "ON" : "OFF");
            }
        });
        uiTable.add(resetButton).colspan(2).center().width(320).padTop(30).row();

        TextButton backButton = createBackButton(new MainMenuScreen());
        uiTable.add(backButton).colspan(2).center().width(150).padTop(20);

        stack.add(uiTable);
        rootTable.add(stack).grow().minSize(0);
        Gdx.input.setInputProcessor(stage);
    }
}
