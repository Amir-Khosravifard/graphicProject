package com.test.liftoff.Controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.test.liftoff.Model.AchievementData;

public class AchievementManager {
    private static final Json json = new Json();
    private static final String FILE_NAME = "achievements.json";
    private static AchievementData cachedData;

    public static AchievementData load() {
        if (cachedData != null) return cachedData;

        if (!Gdx.files.local(FILE_NAME).exists()) {
            cachedData = new AchievementData();
            return cachedData;
        }
        try {
            String text = Gdx.files.local(FILE_NAME).readString();
            cachedData = json.fromJson(AchievementData.class, text);
            if (cachedData == null) cachedData = new AchievementData();
        } catch (Exception e) {
            Gdx.app.error("AchievementManager", "Error reading achievements file: " + e.getMessage());
            cachedData = new AchievementData();
        }
        return cachedData;
    }

    public static void save() {
        try {
            if (cachedData == null) cachedData = load();
            String serializedText = json.toJson(cachedData);
            Gdx.files.local(FILE_NAME).writeString(serializedText, false);
        } catch (Exception e) {
            Gdx.app.error("AchievementManager", "Error writing achievements file: " + e.getMessage());
        }
    }
}
