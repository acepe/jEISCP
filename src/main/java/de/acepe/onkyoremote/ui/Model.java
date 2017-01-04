package de.acepe.onkyoremote.ui;

import static de.csmp.jeiscp.eiscp.EiscpCommmandsConstants.*;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.acepe.onkyoremote.backend.Command;
import de.csmp.jeiscp.EiscpConnector;
import de.csmp.jeiscp.EiscpListener;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

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

    private EiscpConnector conn;

    public Model() {
        selectedSourceProperty().addListener(observable -> sendIscpCommand(selectedSource.get()));
    }

    public void setConn(EiscpConnector conn) {
        this.conn = conn;
        conn.addListener(this);
        init();
    }

    private void init() {
        sendIscpCommand(SYSTEM_POWER_QUERY_ISCP);
        sendIscpCommand(MASTER_VOLUME_QUERY_ISCP);
        sendIscpCommand(VIDEO_INFOMATION_QUERY_ISCP);
        sendIscpCommand(MONITOR_OUT_RESOLUTION_QUERY_ISCP);
        sendIscpCommand(INPUT_SELECTOR_QUERY_ISCP);
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
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    public List<Command> getSources() {
        return sources;
    }

    @Override
    public void receivedIscpMessage(String message) {
        LOG.info("Received: " + message);

        String command = message.substring(0, 3);
        String parameter = message.substring(3);

        if (command.equals(INPUT_SELECTOR_ISCP)) {
            sources.stream().filter(c -> c.getCode().equals(message)).findFirst().ifPresent(this::setSource);

            // String currentInput = lastReceivedValues.get(INPUT_SELECTOR_ISCP);
            // if (INPUT_SELECTOR_NETWORK_ISCP.equals(INPUT_SELECTOR_ISCP + currentInput) ||
            // "2E".equals(currentInput)) {
            // try {
            // if (! lastReceivedValues.containsKey(NET_USB_ARTIST_NAME_INFO_ISCP)) {
            // conn.sendIscpCommand(NET_USB_ARTIST_NAME_INFO_QUERY_ISCP);
            // }
            //
            // if (! lastReceivedValues.containsKey(NET_USB_TITLE_NAME_ISCP)) {
            // conn.sendIscpCommand(NET_USB_TITLE_NAME_QUERY_ISCP);
            // }
            // } catch (Exception ex) {
            // log.error(ex.getMessage(), ex);
            // }
            // }

        }
    }

    private void setSource(Command command) {
        Platform.runLater(() -> selectedSourceProperty().setValue(command));
    }
}
