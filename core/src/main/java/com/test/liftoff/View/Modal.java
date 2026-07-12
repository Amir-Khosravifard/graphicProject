package com.test.liftoff.View;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class Modal extends Table {
    private Skin skin;
    private Table wrapperTable;

    public Modal() {
        this.skin = AssetManager.getSkin();
        wrapperTable = new Table();
        wrapperTable.add(this);

        setBackground(skin.getDrawable("window"));

        wrapperTable.setTouchable(Touchable.enabled);
        this.setTouchable(Touchable.enabled);

        wrapperTable.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (event.getTarget() == wrapperTable)
                    hide();
            }
        });


        wrapperTable.addListener(new InputListener() {
            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                return true;
            }

            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                return true;
            }
        });


    }

    public void show() {
        UIManager.getCurrentScreen().addToModalStack(wrapperTable);
        wrapperTable.getStage().setKeyboardFocus(wrapperTable);
    }

    public void hide() {
        wrapperTable.remove();
    }

    @Override
    public Skin getSkin() {
        return skin;
    }
}
