package de.acepe.onkyoremote.ui;

import de.acepe.onkyoremote.ControlledScreen;
import de.acepe.onkyoremote.ScreenManager;
import de.acepe.onkyoremote.Screens;
import de.acepe.onkyoremote.backend.Settings;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class SettingsViewController implements ControlledScreen {

    private ScreenManager screenManager;
    private Settings settings;

    @FXML
    private Label settingsLabel;
    @FXML
    private Button backButton;

    public SettingsViewController() {
    }

    @FXML
    private void initialize() {
        settings = Settings.getInstance();
        GlyphsDude.setIcon(backButton, FontAwesomeIcon.CHEVRON_LEFT, "1.5em");
        backButton.setText("");

    }

    @FXML
    void onBackPerformed() {
        settings.saveSettings();
        screenManager.setScreen(Screens.MAIN_VIEW, ScreenManager.Direction.RIGHT);
    }

    @Override
    public void setScreenManager(ScreenManager screenManager) {
        this.screenManager = screenManager;
    }

}
