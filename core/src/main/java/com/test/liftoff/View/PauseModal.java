package com.test.liftoff.View;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class PauseModal extends Modal{

    public PauseModal() {
        super();
        TextButton resumeButton = new TextButton("resume", getSkin());
        TextButton exitButton = new TextButton("exit", getSkin());
        add(resumeButton).width(300).pad(15).row();
        add(exitButton).width(300).row();
        this.pad(30);

        resumeButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onResume();
            }
        });

        exitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onExit();
            }
        });

    }
    public void onResume(){

    }
    public void onExit(){

    }
}
