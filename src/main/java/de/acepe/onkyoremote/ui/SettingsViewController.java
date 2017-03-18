package de.acepe.onkyoremote.ui;

import static de.acepe.onkyoremote.util.Utils.makePartialIPRegex;
import static de.acepe.onkyoremote.util.Utils.validateIP;
import static javafx.beans.binding.Bindings.createBooleanBinding;
import static javafx.beans.binding.Bindings.createStringBinding;

import java.util.function.UnaryOperator;

import de.acepe.onkyoremote.ControlledScreen;
import de.acepe.onkyoremote.ScreenManager;
import de.acepe.onkyoremote.Screens;
import de.acepe.onkyoremote.backend.Settings;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

public class SettingsViewController implements ControlledScreen {

    private final ReceiverConnector receiverConnector;

    private ScreenManager screenManager;
    private Settings settings;

    @FXML
    private Button backButton;
    @FXML
    private TextField receiverIpTextField;
    @FXML
    private Button connectButton;
    @FXML
    private Label statusLabel;

    public SettingsViewController() {
        receiverConnector = ReceiverConnector.getInstance();
    }

    @FXML
    private void initialize() {
        settings = Settings.getInstance();
        GlyphsDude.setIcon(backButton, FontAwesomeIcon.CHEVRON_LEFT, "1.5em");

        String regex = makePartialIPRegex();
        final UnaryOperator<TextFormatter.Change> ipAddressFilter = c -> {
            String text = c.getControlNewText();
            if (text.matches(regex)) {
                return c;
            } else {
                return null;
            }
        };
        receiverIpTextField.setTextFormatter(new TextFormatter<>(ipAddressFilter));
        receiverIpTextField.textProperty().bindBidirectional(settings.receiverIpProperty());
        connectButton.disableProperty().bind(createBooleanBinding(() -> validateIP(receiverIpTextField.getText()),
                                                                  receiverIpTextField.textProperty()).not());
        statusLabel.textProperty().bind(createStringBinding(this::getConnectionStatusText,
                                                            receiverConnector.selectedSourceProperty()));
        setConnectionStatusIcon();
        receiverConnector.connectedProperty().addListener(observable -> setConnectionStatusIcon());
    }

    private void setConnectionStatusIcon() {
        GlyphsDude.setIcon(statusLabel,
                           receiverConnector.isConnected() ? FontAwesomeIcon.CHECK : FontAwesomeIcon.TIMES,
                           "1.5em");
        statusLabel.setId(receiverConnector.isConnected() ? "connection-status-ok" : "connection-status-nok");
    }

    private String getConnectionStatusText() {
        return receiverConnector.selectedSourceProperty().get() == null ? "not connected" : "connected";
    }

    @FXML
    void onConnectPerformed() {
        receiverConnector.connectToReceiver();
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
