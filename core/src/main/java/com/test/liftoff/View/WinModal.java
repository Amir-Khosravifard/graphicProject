package com.test.liftoff.View;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class WinModal extends Modal {
    public WinModal(int deaths, int kills, float timeElapsed, final Runnable onRestart) {
        super();

        Label titleLabel = new Label("VICTORY!", getSkin());
        add(titleLabel).colspan(2).padBottom(30).row();


        add(new Label("Total Deaths: ", getSkin())).left().pad(5);
        add(new Label(String.valueOf(deaths), getSkin())).right().pad(5).row();

        add(new Label("Enemies Defeated: ", getSkin())).left().pad(5);
        add(new Label(String.valueOf(kills), getSkin())).right().pad(5).row();

        add(new Label("Completion Time: ", getSkin())).left().pad(5);
        add(new Label(String.format("%.2fs", timeElapsed), getSkin())).right().pad(5).row();


        TextButton restartButton = new TextButton("Restart", getSkin());
        TextButton mainMenuButton = new TextButton("Main Menu", getSkin());

        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
                onRestart.run();
            }
        });

        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
                UIManager.changeScreen(new MainMenuScreen());
            }
        });

        add(restartButton).width(150).padTop(25).padRight(10);
        add(mainMenuButton).width(150).padTop(25).row();
        this.pad(40);
    }
}
