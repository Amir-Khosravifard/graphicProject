package com.test.liftoff.View;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class Modal extends Table {
    private Skin skin;
    private Table wrapperTable;

    public Modal() {
        this.skin = AssetManager.getSkin();
        wrapperTable = new Table();
        wrapperTable.add(this);
    }
    public void show(){
        UIManager.getCurrentScreen().addToModalStack(wrapperTable);
    }
    public void hide(){
        wrapperTable.remove();
    }
}
