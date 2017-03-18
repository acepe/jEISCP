package de.acepe.onkyoremote;

import de.acepe.onkyoremote.ui.ReceiverConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class OnkyoRemote extends Application {
    private static final Logger LOG = LoggerFactory.getLogger(OnkyoRemote.class);

    private ScreenManager screenManager;

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.setProperty("prism.lcdtext", "false");
        Platform.setImplicitExit(false);

        screenManager = new ScreenManager(this);
        screenManager.loadScreen(Screens.MAIN_VIEW);
        screenManager.loadScreen(Screens.SETTINGS);

        BorderPane root = new BorderPane();
        root.setCenter(screenManager);

        Screens startScreen = Screens.MAIN_VIEW;
        Scene scene = new Scene(root, startScreen.getWidth(), startScreen.getHeight());
        scene.getStylesheets().add(ScreenManager.APP_STYLE);

        primaryStage.setScene(scene);
        primaryStage.setX(600);

        screenManager.setScreen(startScreen);
        primaryStage.show();
        primaryStage.toFront();
        primaryStage.requestFocus();

        primaryStage.setOnCloseRequest((event -> {
            screenManager.closeStages();
            Platform.exit();
        }));

        ReceiverConnector.getInstance().connectToReceiver();
    }

    @Override
    public void stop() throws Exception {
        System.exit(0);
    }

    public static void main(String... args) {
        launch(args);
    }
}