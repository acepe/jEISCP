package de.acepe.onkyoremote.ui;

import static de.csmp.jeiscp.eiscp.EiscpCommmandsConstants.*;
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
import de.acepe.onkyoremote.util.ToStringConverter;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;

public class MainViewController implements ControlledScreen {
    private static final Logger LOG = LoggerFactory.getLogger(MainViewController.class);

    private final ReceiverConnector receiverConnector;

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
    private Label listeningModeLabel;
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
        receiverConnector = ReceiverConnector.getInstance();
    }

    @FXML
    void initialize() {
        setIcon(settingsButton, FontAwesomeIcon.COG, "1.5em");
        setIcon(muteButton, FontAwesomeIcon.VOLUME_UP, "1.5em");
        settingsButton.setText("");
        muteButton.setText("");

        sourceComboBox.getItems().addAll(receiverConnector.getSources());
        sourceComboBox.valueProperty().bindBidirectional(receiverConnector.selectedSourceProperty());
        sourceComboBox.setConverter(new ToStringConverter<>(Command::getDisplayKey));

        volumeLabel.textProperty().bind(format("%.0f%%", receiverConnector.volumeProperty()));
        volumeLabelCenter.textProperty().bind(format("%.0f", receiverConnector.volumeCenterProperty()));
        volumeLabelSub.textProperty().bind(format("%.0f", receiverConnector.volumeSubProperty()));

        installScrollListener(volumeSlider, volumeSliderCenter, volumeSliderSub);
        installEnabledOnConnectedListener(sourceComboBox, volumeSlider, volumeSliderCenter, volumeSliderSub);

        DoubleProperty volumeProperty = volumeSlider.valueProperty();
        volumeProperty.addListener((observable, oldValue, newValue) -> receiverConnector.setVolume((Double) newValue));
        receiverConnector.volumeProperty().addListener((observable, oldValue, newValue) -> {
            volumeProperty.setValue(newValue);
            sliderProgress.setProgress(newValue.doubleValue() / 100);
        });

        DoubleProperty volumeCenterProperty = volumeSliderCenter.valueProperty();
        volumeCenterProperty.addListener((observable,
                                          oldValue,
                                          newValue) -> receiverConnector.setVolumeCenter((Double) newValue));
        receiverConnector.volumeCenterProperty()
                         .addListener((observable, oldValue, newValue) -> volumeCenterProperty.setValue(newValue));

        DoubleProperty volumeSubProperty = volumeSliderSub.valueProperty();
        volumeSubProperty.addListener((observable,
                                       oldValue,
                                       newValue) -> receiverConnector.setVolumeSub((Double) newValue));
        receiverConnector.volumeSubProperty()
                         .addListener((observable, oldValue, newValue) -> volumeSubProperty.setValue(newValue));

        receiverConnector.muteProperty()
                         .addListener(observable -> setIcon(muteButton, receiverConnector.muteProperty().get()
                                 ? FontAwesomeIcon.VOLUME_OFF
                                 : FontAwesomeIcon.VOLUME_UP, "1.5em"));

        listeningModeLabel.textProperty().bindBidirectional(receiverConnector.listeningModeProperty());
    }

    private void installEnabledOnConnectedListener(Node... nodes) {
        Stream.of(nodes).forEach(n -> n.disableProperty().bind(receiverConnector.connectedProperty().not()));
    }

    private void installScrollListener(Slider... sliders) {
        Stream.of(sliders)
              .forEach(s -> s.setOnScroll(event -> s.setValue(s.getValue() + max(-1, min(1, event.getDeltaY())))));
    }

    @FXML
    void onMutePerformed() {
        receiverConnector.setMute();
    }

    @FXML
    void onSettingsPerformed() {
        screenManager.setScreen(Screens.SETTINGS, ScreenManager.Direction.LEFT);
    }

    @FXML
    void onStereoPerformed() {
        receiverConnector.sendIscpCommand(LISTENING_MODE_STEREO_ISCP);
    }

    @FXML
    void onDirectPerformed() {
        receiverConnector.sendIscpCommand(LISTENING_MODE_DIRECT_ISCP);
    }

    @FXML
    void onPureAudioPerformed() {
        receiverConnector.sendIscpCommand(LISTENING_MODE_PURE_AUDIO_ISCP);
    }

    @FXML
    void onAllChannelStereoPerformed() {
        receiverConnector.sendIscpCommand(LISTENING_MODE_ALL_CH_STEREO_ISCP);
    }

    @FXML
    void onFullMonoPerformed() {
        receiverConnector.sendIscpCommand(LISTENING_MODE_FULL_MONO_ISCP);
    }

    @FXML
    void onMultiChannelPerformed() {
        receiverConnector.sendIscpCommand(LISTENING_MODE_STRAIGHT_DECODE_ISCP);
    }

    @FXML
    void onThxCinemaPerformed() {
        receiverConnector.sendIscpCommand(LISTENING_MODE_THX_CINEMA_ISCP);
    }

    @FXML
    void onThxGamesPerformed() {
        receiverConnector.sendIscpCommand(LISTENING_MODE_THX_GAMES_ISCP);
    }

    @FXML
    void onThxMusicPerformed() {
        receiverConnector.sendIscpCommand(LISTENING_MODE_THX_MUSIC_ISCP);
    }

    @FXML
    void onGameRockPerformed() {
        receiverConnector.sendIscpCommand(LISTENING_MODE_THX_MUSIC_ISCP);
    }

    @FXML
    void onGameSportsPerformed() {
        receiverConnector.sendIscpCommand(LISTENING_MODE_MUSICAL_ISCP);
    }

    @FXML
    void onTheaterDimensionalPerformed() {
        receiverConnector.sendIscpCommand(LISTENING_MODE_THEATER_DIMENSIONAL_ISCP);
    }
    @FXML
    void onTvLogicPerformed() {
        receiverConnector.sendIscpCommand(LISTENING_MODE_TV_LOGIC_ISCP);
    }


    @Override
    public void setScreenManager(ScreenManager screenManager) {
        this.screenManager = screenManager;
    }

}
