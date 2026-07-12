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
import com.test.liftoff.Enums.AudioType;

public class GuideScreen extends AbstractScreen {

    @Override
    public void show() {
        super.show();
        SoundManager.playBackGroundMusic(AudioType.TITLE_THEME);

        Stack stack = new Stack();
        stack.setFillParent(true);

        Texture backGroundTexture = new Texture(Gdx.files.internal("BackGround/undefined - Imgur1.png"));
        Image backGroundImage = new Image(backGroundTexture);
        backGroundImage.setScaling(Scaling.fill);
        stack.add(backGroundImage);

        Table uiTable = new Table();
        uiTable.setFillParent(true);

        Label.LabelStyle titleStyle = new Label.LabelStyle(skin.getFont("font"), skin.getColor("white"));
        Label screenTitle = new Label("GAMEPLAY GUIDE", titleStyle);
        screenTitle.setFontScale(1.2f);
        uiTable.add(screenTitle).padTop(30).padBottom(20).row();


        Table contentTable = new Table();
        contentTable.top().center();
        contentTable.pad(10);

        Color darkTitleColor = Color.BLACK;
        Color darkDescColor = new Color(0.15f, 0.15f, 0.15f, 1f);
        Color cheatKeyColor = new Color(0.6f, 0.1f, 0.1f, 1f);

        Label.LabelStyle sectionHeaderStyle = new Label.LabelStyle(skin.getFont("font"), darkTitleColor);
        Label.LabelStyle textStyle = new Label.LabelStyle(skin.getFont("font"), darkDescColor);
        Label.LabelStyle codeStyle = new Label.LabelStyle(skin.getFont("font"), cheatKeyColor);


        Table section1 = new Table();
        section1.setBackground(skin.getDrawable("window"));


        section1.pad(35, 25, 20, 25);

        Label controlTitle = new Label("1. MOVEMENT & COMBAT CONTROLS", sectionHeaderStyle);
        controlTitle.setFontScale(0.9f);
        section1.add(controlTitle).left().padBottom(10).row();

        String controlsText = "* Move Left/Right: Left / Right Arrow Keys\n" +
            "* Jump / Wall Jump: Up Arrow Key\n" +
            "* Look Down (Airborne Pogo Slam): Down Arrow Key\n" +
            "* Attack (Nail Slash): X Key\n" +
            "* Dash Ability: C Key\n" +
            "* Focus Healing: Hold A Key";
        Label controlsLabel = new Label(controlsText, textStyle);
        controlsLabel.setFontScale(0.7f);
        section1.add(controlsLabel).left().row();


        contentTable.add(section1).width(900f).height(220f).center().padBottom(25).row();


        Table section2 = new Table();
        section2.setBackground(skin.getDrawable("window"));
        section2.pad(35, 25, 20, 25);


        Label mechanicsTitle = new Label("2. KNIGHT MECHANICS", sectionHeaderStyle);
        mechanicsTitle.setFontScale(0.9f);
        section2.add(mechanicsTitle).left().padBottom(10).row();

        String mechanicsText = "* Health Bar: Your vitality is represented by the white Masks in the top left corner.\n" +
            "* Gathering Soul: Striking active enemies with your Nail extracts raw Soul energy.\n" +
            "* Soul Vessel: Absorbed energy dynamically fills the circular Vessel overlay.\n" +
            "* Focus Channeling: When on safe ground, hold the [A] key to consume 33 Soul points\n" +
            "  and stitch together one damaged Health Mask completely.";
        Label mechanicsLabel = new Label(mechanicsText, textStyle);
        mechanicsLabel.setFontScale(0.7f);
        mechanicsLabel.setWrap(true);
        section2.add(mechanicsLabel).left().width(840f).row();


        contentTable.add(section2).width(900f).height(200f).center().padBottom(25).row();


        Table section3 = new Table();
        section3.setBackground(skin.getDrawable("window"));
        section3.pad(35, 25, 20, 25);


        Label cheatsTitle = new Label("3. CHEAT CODES", sectionHeaderStyle);
        cheatsTitle.setFontScale(0.9f);
        section3.add(cheatsTitle).left().padBottom(15).colspan(2).row();

        addCheatRow(section3, "Ctrl + T", "Instant Teleportation directly to active Boss Room Arena gates.", codeStyle, textStyle);
        addCheatRow(section3, "Ctrl + N", "Toggle Noclip Spectator Flight mode (Bypasses terrain boundaries).", codeStyle, textStyle);
        addCheatRow(section3, "Ctrl + H", "Emergency Heal triage patch (Instantly restores 1 Health Mask cell).", codeStyle, textStyle);
        addCheatRow(section3, "Ctrl + S", "Refill core Soul Vessel completely to maximum payload capacity (99 Points).", codeStyle, textStyle);
        addCheatRow(section3, "Ctrl + G", "Toggle Invulnerable God Mode stance (Ignores spikes, environmental hazard damage).", codeStyle, textStyle);
        addCheatRow(section3, "Ctrl + K", "Sovereign Obliteration pulse (Instantly kills all active level monsters).", codeStyle, textStyle);


        contentTable.add(section3).width(900f).height(300f).center().padBottom(25).row();

        ScrollPane.ScrollPaneStyle customScrollStyle = new ScrollPane.ScrollPaneStyle(skin.get(ScrollPane.ScrollPaneStyle.class));
        customScrollStyle.background = null;

        ScrollPane scrollPane = new ScrollPane(contentTable, customScrollStyle);
        scrollPane.setFadeScrollBars(false);
        uiTable.add(scrollPane).grow().pad(10, 60, 10, 60).row();

        TextButton backButton = createBackButton(new MainMenuScreen());
        uiTable.add(backButton).left().pad(15).padBottom(20);

        stack.add(uiTable);
        rootTable.add(stack).grow().minSize(0);
        Gdx.input.setInputProcessor(stage);
    }

    private void addCheatRow(Table target, String shortcut, String effect, Label.LabelStyle codeStyle, Label.LabelStyle textStyle) {
        Label shortcutLabel = new Label(shortcut, codeStyle);
        shortcutLabel.setFontScale(0.7f);
        target.add(shortcutLabel).width(150f).left().padBottom(8);

        Label effectLabel = new Label(effect, textStyle);
        effectLabel.setFontScale(0.7f);
        effectLabel.setWrap(true);
        target.add(effectLabel).width(680f).left().padBottom(8).row();
    }
}
