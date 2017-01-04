package de.acepe.onkyoremote.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.acepe.onkyoremote.ControlledScreen;
import de.acepe.onkyoremote.ScreenManager;
import de.acepe.onkyoremote.Screens;
import de.acepe.onkyoremote.backend.Command;
import de.acepe.onkyoremote.backend.Settings;
import de.acepe.onkyoremote.util.ToStringConverter;
import de.csmp.jeiscp.EiscpConnector;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

public class MainViewController implements ControlledScreen {
    private static final Logger LOG = LoggerFactory.getLogger(MainViewController.class);

    private final Settings settings;
    private final Model model;

    private ScreenManager screenManager;

    @FXML
    private Button settingsButton;
    @FXML
    private ComboBox<Command> sourceComboBox;

    public MainViewController() {
        settings = Settings.getInstance();
        model = new Model();
    }

    @FXML
    void initialize() {
        GlyphsDude.setIcon(settingsButton, FontAwesomeIcon.COG, "1.5em");
        settingsButton.setText("");

        sourceComboBox.getItems().addAll(model.getSources());
        sourceComboBox.valueProperty().bindBidirectional(model.selectedSourceProperty());
        sourceComboBox.setConverter(new ToStringConverter<>(Command::getDisplayKey));
    }

    @FXML
    void onSettingsPerformed() {
        screenManager.setScreen(Screens.SETTINGS, ScreenManager.Direction.LEFT);
    }

    @Override
    public void setScreenManager(ScreenManager screenManager) {
        this.screenManager = screenManager;
    }

    public void setConn(EiscpConnector conn) {
        this.model.setConn(conn);
    }

}
