package com.test.liftoff;

import com.badlogic.gdx.Game;
import com.test.liftoff.View.MainMenuScreen;
import com.test.liftoff.View.UIManager;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {


    @Override
    public void create() {
        UIManager.initMain(this);
        MainMenuScreen mainMenuScreen = new MainMenuScreen();
        setScreen(mainMenuScreen);
    }
}
