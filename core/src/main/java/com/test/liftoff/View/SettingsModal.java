package com.test.liftoff.View;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.test.liftoff.Audio.SoundManager;

public class SettingsModal extends Modal {
    public SettingsModal() {
        super();


        Label titleLabel = new Label("SETTINGS", getSkin());
        add(titleLabel).padBottom(40).colspan(2).row();


        Label volumeLabel = new Label("Music Volume: ", getSkin());
        final Slider volumeSlider = new Slider(0f, 1f, 0.05f, false, getSkin());
        volumeSlider.setValue(SoundManager.getMusicVolume());
        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SoundManager.setMusicVolume(volumeSlider.getValue());
            }
        });
        add(volumeLabel).left().pad(10);
        add(volumeSlider).width(300).pad(10).row();


        Label musicStatusLabel = new Label("Music Stream: ", getSkin());
        final TextButton musicToggleBtn = new TextButton(SoundManager.isMusicEnabled() ? "ON" : "OFF", getSkin());
        musicToggleBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean nextState = !SoundManager.isMusicEnabled();
                SoundManager.setMusicEnabled(nextState);
                musicToggleBtn.setText(nextState ? "ON" : "OFF");
            }
        });
        add(musicStatusLabel).left().pad(10);
        add(musicToggleBtn).width(150).pad(10).row();


        Label sfxStatusLabel = new Label("SFX Audio: ", getSkin());
        final TextButton sfxToggleBtn = new TextButton(SoundManager.isSfxEnabled() ? "ON" : "OFF", getSkin());
        sfxToggleBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean nextState = !SoundManager.isSfxEnabled();
                SoundManager.setSfxEnabled(nextState);
                sfxToggleBtn.setText(nextState ? "ON" : "OFF");
            }
        });
        add(sfxStatusLabel).left().pad(10);
        add(sfxToggleBtn).width(150).pad(10).row();


        TextButton resetButton = new TextButton("Reset to Defaults", getSkin());
        resetButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SoundManager.resetToDefaults();
                volumeSlider.setValue(SoundManager.getMusicVolume());
                musicToggleBtn.setText(SoundManager.isMusicEnabled() ? "ON" : "OFF");
                sfxToggleBtn.setText(SoundManager.isSfxEnabled() ? "ON" : "OFF");
            }
        });
        add(resetButton).colspan(2).center().width(320).padTop(30).row();


        TextButton backButton = new TextButton("Back", getSkin());
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                hide();
            }
        });
        add(backButton).colspan(2).center().width(150).padTop(20);
        this.pad(30);
    }
}
