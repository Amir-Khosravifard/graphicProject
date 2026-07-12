package com.test.liftoff.View;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class PauseModal extends Modal {
    public PauseModal() {
        super();
        pad(30);

        Table mainGrid = new Table();
        mainGrid.top().center();

        Table leftMenu = new Table();
        leftMenu.top().center();

        TextButton resumeButton = new TextButton("resume", getSkin());
        TextButton settingsButton = new TextButton("settings", getSkin());
        TextButton exitButton = new TextButton("Save & Exit", getSkin());

        leftMenu.add(resumeButton).width(260).pad(10).row();
        leftMenu.add(settingsButton).width(260).pad(10).row();
        leftMenu.add(exitButton).width(260).pad(10).row();

        Table rightCheats = new Table();
        rightCheats.top().left();
        rightCheats.padLeft(35);

        Color darkTitleColor = Color.BLACK;
        Color darkDescColor = new Color(0.15f, 0.15f, 0.15f, 1f);
        Color cheatKeyColor = new Color(0.6f, 0.1f, 0.1f, 1f);

        Label.LabelStyle sectionHeaderStyle = new Label.LabelStyle(getSkin().getFont("font"), darkTitleColor);
        Label.LabelStyle textStyle = new Label.LabelStyle(getSkin().getFont("font"), darkDescColor);
        Label.LabelStyle codeStyle = new Label.LabelStyle(getSkin().getFont("font"), cheatKeyColor);

        Label cheatsTitle = new Label("CHEAT CODES", sectionHeaderStyle);
        cheatsTitle.setFontScale(0.8f);
        rightCheats.add(cheatsTitle).left().padBottom(15).colspan(2).row();

        addCheatRow(rightCheats, "Ctrl + T", "Teleport to Boss Layer Box", codeStyle, textStyle);
        addCheatRow(rightCheats, "Ctrl + N", "Toggle Noclip Flight Mode", codeStyle, textStyle);
        addCheatRow(rightCheats, "Ctrl + H", "Emergency Heal (+1 Mask)", codeStyle, textStyle);
        addCheatRow(rightCheats, "Ctrl + S", "Refill Soul Vessel (99 Pts)", codeStyle, textStyle);
        addCheatRow(rightCheats, "Ctrl + G", "Toggle Invulnerable God Mode", codeStyle, textStyle);
        addCheatRow(rightCheats, "Ctrl + K", "Instantly Kill All Enemies", codeStyle, textStyle);

        mainGrid.add(leftMenu).top().center();
        mainGrid.add(rightCheats).top().left();
        add(mainGrid);

        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onResume();
            }
        });

        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SettingsModal settingsModal = new SettingsModal();
                settingsModal.show();
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onExit();
            }
        });
    }

    private void addCheatRow(Table target, String shortcut, String effect, Label.LabelStyle codeStyle, Label.LabelStyle textStyle) {
        Label shortcutLabel = new Label(shortcut, codeStyle);
        shortcutLabel.setFontScale(0.65f);
        target.add(shortcutLabel).width(120f).left().padBottom(6);

        Label effectLabel = new Label(effect, textStyle);
        effectLabel.setFontScale(0.65f);
        target.add(effectLabel).width(300f).left().padBottom(6).row();
    }

    public void onResume() {
    }

    public void onExit() {
    }
}
