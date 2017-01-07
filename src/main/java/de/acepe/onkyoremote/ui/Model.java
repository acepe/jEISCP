package de.acepe.onkyoremote.ui;

import static de.csmp.jeiscp.EiscpProtocolHelper.convertToHexString;
import static de.csmp.jeiscp.eiscp.EiscpCommmandsConstants.*;
import static java.lang.Integer.toHexString;
import static java.lang.Math.abs;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.acepe.onkyoremote.backend.Command;
import de.csmp.jeiscp.EiscpConnector;
import de.csmp.jeiscp.EiscpListener;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Model implements EiscpListener {
    private static final Logger LOG = LoggerFactory.getLogger(Model.class);

    private final List<Command> sources = Stream.of(new Command("BD/DVD", INPUT_SELECTOR_DVD_ISCP),
                                                    new Command("CBL/SAT", INPUT_SELECTOR_VIDEO2_ISCP),
                                                    new Command("STB/DVR", INPUT_SELECTOR_VIDEO1_ISCP),
                                                    new Command("GAME", INPUT_SELECTOR_VIDEO3_ISCP),
                                                    new Command("PC", INPUT_SELECTOR_VIDEO6_ISCP),
                                                    new Command("AUX", INPUT_SELECTOR_VIDEO4_ISCP),
                                                    new Command("AM", INPUT_SELECTOR_AM_ISCP),
                                                    new Command("FM", INPUT_SELECTOR_FM_ISCP),
                                                    new Command("TV/CD", INPUT_SELECTOR_CD_ISCP),
                                                    new Command("NET", INPUT_SELECTOR_NETWORK_ISCP),
                                                    new Command("AM", INPUT_SELECTOR_USB_ISCP))
                                                .collect(toList());

    private final ObjectProperty<Command> selectedSource = new SimpleObjectProperty<>();
    private final DoubleProperty volume = new SimpleDoubleProperty(0);
    private final DoubleProperty volumeCenter = new SimpleDoubleProperty(0);
    private final DoubleProperty volumeSub = new SimpleDoubleProperty(0);
    private final BooleanProperty mute = new SimpleBooleanProperty(false);

    private EiscpConnector conn;
    private ObservableList<String> sendCommands = FXCollections.observableArrayList();

    public Model() {
        selectedSource.addListener(observable -> sendIscpCommand(selectedSource.get()));
    }

    public void setConn(EiscpConnector conn) {
        this.conn = conn;
        conn.addListener(this);
        init();
    }

    private void init() {
        sendIscpCommand(SYSTEM_POWER_QUERY_ISCP);
        sendIscpCommand(MASTER_VOLUME_QUERY_ISCP);
        sendIscpCommand(AUDIO_MUTING_QUERY_ISCP);
        sendIscpCommand(CENTER_TEMPORARY_LEVEL_QUERY_ISCP);
        sendIscpCommand(SUBWOOFER_TEMPORARY_LEVEL_QUERY_ISCP);
        sendIscpCommand(VIDEO_INFOMATION_QUERY_ISCP);
        sendIscpCommand(MONITOR_OUT_RESOLUTION_QUERY_ISCP);
        sendIscpCommand(INPUT_SELECTOR_QUERY_ISCP);
        sendCommands.clear();
    }

    public void setVolume(double vol) {
        if (vol == volume.get()) {
            return;
        }
        volume.set(vol);
        sendIscpCommand(MASTER_VOLUME_ISCP + convertToHexString((byte) vol));
    }

    public void setVolumeCenter(double centerVol) {
        setOffsetVolume(centerVol, volumeCenter, "CTL");
    }

    public void setVolumeSub(double subVol) {
        setOffsetVolume(subVol, volumeSub, "SWL");
    }

    private void setOffsetVolume(double vol, DoubleProperty property, String codePrefix) {
        if (vol == property.get()) {
            return;
        }
        property.set(vol);
        if (vol == 0) {
            sendIscpCommand(codePrefix + "00");
        } else {
            sendIscpCommand(codePrefix + (vol < 0 ? "-" : "+") + toHexString(abs((int) vol)).toUpperCase());
        }
    }

    public void setMute() {
        if (sendCommands.stream().anyMatch(s -> s.startsWith(AUDIO_MUTING_ISCP))) {
            return;
        }
        sendIscpCommand(AUDIO_MUTING_TOGGLE_ISCP);
    }

    public ObjectProperty<Command> selectedSourceProperty() {
        return selectedSource;
    }

    private void sendIscpCommand(Command command) {
        sendIscpCommand(command.getCode());
    }

    private void sendIscpCommand(String cmd) {
        try {
            conn.sendIscpCommand(cmd);
            sendCommands.add(cmd);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    public List<Command> getSources() {
        return sources;
    }

    @Override
    public void receivedIscpMessage(String message) {
        if (sendCommands.contains(message)) {
            sendCommands.remove(message);
            return;
        }
        String command = message.substring(0, 3);
        String parameter = message.substring(3);

        Platform.runLater(() -> {
            switch (command) {
                case INPUT_SELECTOR_ISCP:
                    sources.stream()
                           .filter(c -> c.getCode().equals(message))
                           .findFirst()
                           .ifPresent(c -> setinUI(selectedSource, c));
                    break;
                case MASTER_VOLUME_ISCP:
                    int volumeValue = Integer.parseInt(parameter, 16);
                    volume.setValue(volumeValue);
                    break;
                case SUBWOOFER_TEMPORARY_LEVEL_ISCP:
                    setOffsetVolume(parameter, volumeSub);
                    break;
                case CENTER_TEMPORARY_LEVEL_ISCP:
                    setOffsetVolume(parameter, volumeCenter);
                    break;
                case AUDIO_MUTING_ISCP:
                    setinUI(mute, parameter.equals("01"));
                    sendCommands.remove(AUDIO_MUTING_TOGGLE_ISCP);
                    break;
            }
        });
    }

    private void setOffsetVolume(String parameter, DoubleProperty offsetProperty) {
        if (parameter.equals("00")) {
            offsetProperty.setValue(0);
        } else {
            String sign = parameter.substring(0, 1);
            int value = Integer.decode("0x" + parameter.substring(1));
            if (sign.equals("-")) {
                value *= -1;
            }
            offsetProperty.setValue(value);
        }
    }

    private <T> void setinUI(Property<T> property, T value) {
        Platform.runLater(() -> property.setValue(value));
    }

    public DoubleProperty volumeProperty() {
        return volume;
    }

    public DoubleProperty volumeCenterProperty() {
        return volumeCenter;
    }

    public DoubleProperty volumeSubProperty() {
        return volumeSub;
    }

    public BooleanProperty muteProperty() {
        return mute;
    }

}
