package com.test.liftoff;

import com.badlogic.gdx.Game;
import com.test.liftoff.Audio.SoundManager;
import com.test.liftoff.View.AssetManager;
import com.test.liftoff.View.MainMenuScreen;
import com.test.liftoff.View.UIManager;


public class Main extends Game {


    @Override
    public void create() {
        UIManager.initMain(this);
        AssetManager.initAsset();
        SoundManager.initSound();

        MainMenuScreen mainMenuScreen = new MainMenuScreen();
        setScreen(mainMenuScreen);
    }
}
