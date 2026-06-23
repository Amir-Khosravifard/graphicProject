package com.test.liftoff.View;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class AssetManager {
    private static Skin skin;
    public static void initAsset(){
        skin = new Skin(Gdx.files.internal("uiSkin/plain-james-ui.json"));
    }

    public static Skin getSkin() {
        return skin;
    }
}
