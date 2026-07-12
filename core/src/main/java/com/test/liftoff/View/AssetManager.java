package com.test.liftoff.View;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.test.liftoff.Enums.AnimationType;

import java.util.HashMap;

public class AssetManager {
    private static Skin skin;
    private static HashMap<AnimationType, Animation<TextureRegion>> animationMap = new HashMap<>();


    public static void loadAnimation(AnimationType animationType, Animation.PlayMode playMode) {
        Texture texture = new Texture(animationType.getPath());
        int frameWidth = texture.getWidth() / animationType.getFrameCount();
        int frameHeight = texture.getHeight();
        TextureRegion[][] split = TextureRegion.split(texture, frameWidth, frameHeight);
        TextureRegion[] frames = new TextureRegion[animationType.getFrameCount()];
        for (int i = 0; i < animationType.getFrameCount(); i++)
            frames[i] = split[0][i];
        Animation<TextureRegion> animation = new Animation<>(1 / 30f, frames);
        animation.setPlayMode(playMode);
        animationMap.put(animationType, animation);
    }

    public static void initAsset() {
        skin = new Skin(Gdx.files.internal("uiSkin/james/plain-james-ui.json"));
        for (AnimationType type : AnimationType.values())
            loadAnimation(type, type.getPlayMode());
    }

    public static Skin getSkin() {
        return skin;
    }

    public static Animation<TextureRegion> getAnimation(AnimationType animationType) {
        return animationMap.get(animationType);
    }
}
