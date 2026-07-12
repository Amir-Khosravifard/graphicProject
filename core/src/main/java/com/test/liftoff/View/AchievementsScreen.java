package com.test.liftoff.View;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Scaling;
import com.test.liftoff.Audio.SoundManager;
import com.test.liftoff.Controller.AchievementManager;
import com.test.liftoff.Enums.AudioType;
import com.test.liftoff.Model.AchievementData;

public class AchievementsScreen extends AbstractScreen {

    private static final String ICON_COMPLETION = "achievments/achievement__0001_all_maps.png";
    private static final String ICON_SPEEDRUN = "achievments/achievement__0006_ending_A.png";
    private static final String ICON_TRUE_HUNTER = "achievments/achievement__0007_stag_quest_complete.png";
    private static final String ICON_FALSE_KNIGHT = "achievments/achievement_false_knight #50302521.png";

    @Override
    public void show() {
        super.show();
        SoundManager.playBackGroundMusic(AudioType.TITLE_THEME);

        AchievementData achievements = AchievementManager.load();

        Stack stack = new Stack();
        stack.setFillParent(true);

        Texture backGroundTexture = new Texture(Gdx.files.internal("BackGround/undefined - Imgur1.png"));
        Image backGroundImage = new Image(backGroundTexture);
        backGroundImage.setScaling(Scaling.fill);
        stack.add(backGroundImage);

        Table uiTable = new Table();
        uiTable.setFillParent(true);

        Label.LabelStyle titleStyle = new Label.LabelStyle(skin.getFont("font"), skin.getColor("white"));
        Label screenTitle = new Label("ACHIEVEMENTS", titleStyle);
        screenTitle.setFontScale(1.2f);
        uiTable.add(screenTitle).padTop(40).padBottom(40).row();

        Table listTable = new Table();
        listTable.top();
        listTable.padTop(20);

        addAchievementRow(listTable, "Completion", "Finish the game by conquering both trial maps.", ICON_COMPLETION, achievements.completion);
        addAchievementRow(listTable, "Speedrun", "Finish the game inside record time (under 5 minutes).", ICON_SPEEDRUN, achievements.speedrun);
        addAchievementRow(listTable, "True Hunter", "Defeat every single unique enemy species variation.", ICON_TRUE_HUNTER, achievements.true_hunter);
        addAchievementRow(listTable, "Defeat False Knight", "Crush the False Knight armor guardian.", ICON_FALSE_KNIGHT, achievements.defeat_false_knight);

        ScrollPane.ScrollPaneStyle customScrollStyle = new ScrollPane.ScrollPaneStyle(skin.get(ScrollPane.ScrollPaneStyle.class));
        customScrollStyle.background = null;

        ScrollPane scrollPane = new ScrollPane(listTable, customScrollStyle);
        scrollPane.setFadeScrollBars(false);
        uiTable.add(scrollPane).grow().pad(10, 80, 20, 80).row();

        TextButton backButton = createBackButton(new MainMenuScreen());
        uiTable.add(backButton).left().pad(10).padBottom(20);

        stack.add(uiTable);
        rootTable.add(stack).grow().minSize(0);
        Gdx.input.setInputProcessor(stage);
    }

    private void addAchievementRow(Table targetTable, String title, String description, String iconPath, boolean isUnlocked) {
        Table rowTable = new Table();
        rowTable.setBackground(skin.getDrawable("window"));
        rowTable.pad(15);

        Texture iconTex = new Texture(Gdx.files.internal(iconPath));
        Image iconImage = new Image(iconTex);

        if (isUnlocked) {
            iconImage.setColor(Color.WHITE);
        } else {
            iconImage.setColor(0.25f, 0.25f, 0.25f, 0.45f);
        }
        rowTable.add(iconImage).size(64, 64).padRight(20);

        Table textStack = new Table();
        textStack.left().top();


        Color darkTitleColor = isUnlocked ? Color.BLACK : new Color(0.25f, 0.25f, 0.25f, 1f);
        Label.LabelStyle titleStyle = new Label.LabelStyle(skin.getFont("font"), darkTitleColor);
        Label titleLabel = new Label(title + (isUnlocked ? " [UNLOCKED]" : " [LOCKED]"), titleStyle);
        titleLabel.setFontScale(0.9f);
        textStack.add(titleLabel).left().row();


        Color darkDescColor = new Color(0.15f, 0.15f, 0.15f, 1f);
        Label.LabelStyle descStyle = new Label.LabelStyle(skin.getFont("font"), darkDescColor);
        Label descLabel = new Label(description, descStyle);
        descLabel.setFontScale(0.7f);
        descLabel.setWrap(true);
        textStack.add(descLabel).left().width(650f).padTop(5);

        rowTable.add(textStack).growX().left();
        targetTable.add(rowTable).width(850f).height(130f).padBottom(20).row();
    }
}
