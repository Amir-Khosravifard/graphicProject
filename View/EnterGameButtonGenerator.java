package com.test.liftoff.View;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import java.awt.*;

public class EnterGameButtonGenerator  {
    public static Button generateButton(Skin skin, String iconImagePath, String buttonText){
        Texture iconTexture = new Texture(Gdx.files.internal(iconImagePath));
        com.badlogic.gdx.scenes.scene2d.ui.Image iconImage = new com.badlogic.gdx.scenes.scene2d.ui.Image(iconTexture);

        Texture plateTexture = new Texture(Gdx.files.internal("gameButton/selector.png"));
        com.badlogic.gdx.scenes.scene2d.ui.Image plateImage = new com.badlogic.gdx.scenes.scene2d.ui.Image(plateTexture);


        com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle customLabelStyle = new com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle();
        customLabelStyle.font = skin.getFont("font"); // Gets your font asset
        customLabelStyle.fontColor = skin.getColor("white");

        com.badlogic.gdx.scenes.scene2d.ui.Label slotText = new com.badlogic.gdx.scenes.scene2d.ui.Label(buttonText, customLabelStyle);
        slotText.setAlignment(Align.center);


        Stack textOverPlateStack = new Stack();
        textOverPlateStack.add(plateImage);
        textOverPlateStack.add(slotText);

        com.badlogic.gdx.scenes.scene2d.ui.Button saveSlotButton = new com.badlogic.gdx.scenes.scene2d.ui.Button(new com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle());


        saveSlotButton.add(iconImage).padRight(12);
        saveSlotButton.add(textOverPlateStack).size(350, 75);

        saveSlotButton.addListener(new ClickListener(){
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                saveSlotButton.getColor().a = 0.5f;
            }
        });
        saveSlotButton.addListener(new ClickListener(){
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                // 🔄 Untouched state: Return back to translucent when mouse leaves
                saveSlotButton.getColor().a = 1.0f;
            }
        });
        return saveSlotButton;

    }

}
