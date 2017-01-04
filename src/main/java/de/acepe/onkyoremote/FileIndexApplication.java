package de.acepe.onkyoremote;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.acepe.onkyoremote.ui.MainViewController;
import de.csmp.jeiscp.EiscpConnector;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class FileIndexApplication extends Application {
    private static final Logger LOG = LoggerFactory.getLogger(FileIndexApplication.class);

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

        try {
            EiscpConnector conn = new EiscpConnector("192.168.1.118");
            ((MainViewController) screenManager.getController(Screens.MAIN_VIEW)).setConn(conn);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception {
        System.exit(0);
    }

    public static void main(String... args) {
        launch(args);
    }
}