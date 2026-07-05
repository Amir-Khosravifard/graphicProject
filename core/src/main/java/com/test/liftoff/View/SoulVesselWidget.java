package com.test.liftoff.View;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.test.liftoff.Model.Entity.Player;

public class SoulVesselWidget extends Widget {
    private final Player player;
    private final Texture vesselFrame;
    private final Texture[] liquidTextures;

    private static final float PAINT_CENTER_X = 73.5f;
    private static final float PAINT_CENTER_Y = 100f;
    private static final float LIQUID_FIT_FACTOR = 3f;
    private static final float HUD_SCALE = 0.7f;

    public SoulVesselWidget(Player player, Texture vesselFrame, Texture[] liquidTextures) {
        this.player = player;
        this.vesselFrame = vesselFrame;
        this.liquidTextures = liquidTextures;
    }

    // 💡 FIXED: Return the scaled width to the Scene2D Table layout engine
    @Override
    public float getPrefWidth() {
        return vesselFrame.getWidth() * HUD_SCALE;
    }

    // 💡 FIXED: Return the scaled height to shrink the layout box down completely
    @Override
    public float getPrefHeight() {
        return vesselFrame.getHeight() * HUD_SCALE;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float x = getX();
        float y = getY();

        int frameIndex = player.getSoul() / 11;
        frameIndex = Math.max(0, Math.min(frameIndex, 9));
        Texture activeLiquid = liquidTextures[frameIndex];

        float scaledLiquidW = activeLiquid.getWidth() * LIQUID_FIT_FACTOR * HUD_SCALE;
        float scaledLiquidH = activeLiquid.getHeight() * LIQUID_FIT_FACTOR * HUD_SCALE;

        float scaledFrameW = vesselFrame.getWidth() * HUD_SCALE;
        float scaledFrameH = vesselFrame.getHeight() * HUD_SCALE;

        float absoluteCenterX = x + (PAINT_CENTER_X * HUD_SCALE);
        float absoluteCenterY = y + ((vesselFrame.getHeight() - PAINT_CENTER_Y) * HUD_SCALE);

        float liquidDrawX = absoluteCenterX - (scaledLiquidW / 2f);
        float liquidDrawY = absoluteCenterY - (scaledLiquidH / 2f);

        batch.draw(vesselFrame, x, y, scaledFrameW, scaledFrameH);
        batch.draw(activeLiquid, liquidDrawX, liquidDrawY, scaledLiquidW, scaledLiquidH);
    }
}
