package com.test.liftoff.Audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.test.liftoff.Enums.AudioType;

import java.util.HashMap;

public class SoundManager {
    private static final HashMap<AudioType, Sound> sfxMap = new HashMap<>();
    private static Music currentBackGroundMusic = null;

    private static float musicVolume = 0.5f;
    private static boolean musicEnabled = true;
    private static boolean sfxEnabled = true;
    private static AudioType currentBackGroundMusicType = null;

    public static void initSound() {
        for (AudioType type : AudioType.values()) {
            if (!type.isMusic()) {
                sfxMap.put(type, Gdx.audio.newSound(Gdx.files.internal(type.getPath())));
            }
        }
    }

    public static void playBackGroundMusic(AudioType audioType) {
        if (!audioType.isMusic()) return;

        if (currentBackGroundMusic != null && currentBackGroundMusicType == audioType) {
            updateBackGroundMusicState();
            return;
        }

        if (currentBackGroundMusic != null) {
            currentBackGroundMusic.stop();
            currentBackGroundMusic.dispose();
        }

        currentBackGroundMusicType = audioType;
        currentBackGroundMusic = Gdx.audio.newMusic(Gdx.files.internal(audioType.getPath()));
        currentBackGroundMusic.setLooping(true);
        updateBackGroundMusicState();
    }

    public static void setMusicVolume(float volume) {
        musicVolume = Math.max(0f, Math.min(1f, volume));
        updateBackGroundMusicState();
    }

    public static void setMusicEnabled(boolean enabled) {
        musicEnabled = enabled;
        updateBackGroundMusicState();
    }

    public static void setSfxEnabled(boolean enabled) {
        sfxEnabled = enabled;
    }

    private static void updateBackGroundMusicState() {
        if (currentBackGroundMusic != null) {
            if (musicEnabled) {
                currentBackGroundMusic.setVolume(musicVolume);
                if (!currentBackGroundMusic.isPlaying()) currentBackGroundMusic.play();
            } else {
                currentBackGroundMusic.setVolume(0f);
                currentBackGroundMusic.pause();
            }
        }
    }

    public static void resetToDefaults() {
        musicVolume = 0.5f;
        musicEnabled = true;
        sfxEnabled = true;
        updateBackGroundMusicState();
    }

    public static float getMusicVolume() { return musicVolume; }
    public static boolean isMusicEnabled() { return musicEnabled; }
    public static boolean isSfxEnabled() { return sfxEnabled; }
}
