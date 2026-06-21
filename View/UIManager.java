package com.test.liftoff.View;

import com.badlogic.gdx.Screen;
import com.test.liftoff.Main;

public class UIManager {
    private static Main main;
    public static void initMain(Main instance){
        main = instance;
    }
    public static void changeScreen(Screen screen){
        main.setScreen(screen);
    }
}
