package de.acepe.onkyoremote.ui;

import static de.jensd.fx.glyphs.GlyphsDude.setIcon;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static javafx.beans.binding.Bindings.format;

import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.acepe.onkyoremote.ControlledScreen;
import de.acepe.onkyoremote.ScreenManager;
import de.acepe.onkyoremote.Screens;
import de.acepe.onkyoremote.backend.Command;
import de.acepe.onkyoremote.backend.Settings;
import de.acepe.onkyoremote.util.ToStringConverter;
import de.csmp.jeiscp.EiscpConnector;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class MainViewController implements ControlledScreen {
    private static final Logger LOG = LoggerFactory.getLogger(MainViewController.class);

    private final Settings settings;
    private final Model model;

    private ScreenManager screenManager;

    @FXML
    private Button settingsButton;
    @FXML
    private ComboBox<Command> sourceComboBox;
    @FXML
    private Slider volumeSlider;
    @FXML
    private Button muteButton;
    @FXML
    private Label volumeLabel;
    @FXML
    private ProgressBar sliderProgress;
    @FXML
    private Slider volumeSliderCenter;
    @FXML
    private Label volumeLabelCenter;
    @FXML
    private Slider volumeSliderSub;
    @FXML
    private Label volumeLabelSub;

    public MainViewController() {
        settings = Settings.getInstance();
        model = new Model();
    }

    @FXML
    void initialize() {
        setIcon(settingsButton, FontAwesomeIcon.COG, "1.5em");
        setIcon(muteButton, FontAwesomeIcon.VOLUME_UP, "1.5em");
        settingsButton.setText("");
        muteButton.setText("");

        sourceComboBox.getItems().addAll(model.getSources());
        sourceComboBox.valueProperty().bindBidirectional(model.selectedSourceProperty());
        sourceComboBox.setConverter(new ToStringConverter<>(Command::getDisplayKey));

        volumeLabel.textProperty().bind(format("%.0f%%", model.volumeProperty()));
        volumeLabelCenter.textProperty().bind(format("%.0f", model.volumeCenterProperty()));
        volumeLabelSub.textProperty().bind(format("%.0f", model.volumeSubProperty()));

        installScrollListener(volumeSlider, volumeSliderCenter, volumeSliderSub);

        DoubleProperty volumeProperty = volumeSlider.valueProperty();
        volumeProperty.addListener((observable, oldValue, newValue) -> model.setVolume((Double) newValue));
        model.volumeProperty().addListener((observable, oldValue, newValue) -> {
            volumeProperty.setValue(newValue);
            sliderProgress.setProgress(newValue.doubleValue() / 100);
        });

        DoubleProperty volumeCenterProperty = volumeSliderCenter.valueProperty();
        volumeCenterProperty.addListener((observable, oldValue, newValue) -> model.setVolumeCenter((Double) newValue));
        model.volumeCenterProperty()
             .addListener((observable, oldValue, newValue) -> volumeCenterProperty.setValue(newValue));

        DoubleProperty volumeSubProperty = volumeSliderSub.valueProperty();
        volumeSubProperty.addListener((observable, oldValue, newValue) -> model.setVolumeSub((Double) newValue));
        model.volumeSubProperty().addListener((observable, oldValue, newValue) -> volumeSubProperty.setValue(newValue));

        model.muteProperty().addListener(observable -> setIcon(muteButton, model.muteProperty().get()
                ? FontAwesomeIcon.VOLUME_OFF
                : FontAwesomeIcon.VOLUME_UP, "1.5em"));
    }

    private void installScrollListener(Slider... sliders) {
        Stream.of(sliders)
              .forEach(s -> s.setOnScroll(event -> s.setValue(s.getValue() + max(-1, min(1, event.getDeltaY())))));
    }

    @FXML
    void onMutePerformed() {
        model.setMute();
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
