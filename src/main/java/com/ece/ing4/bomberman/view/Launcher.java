package com.ece.ing4.bomberman.view;

import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launcher extends Application {
    // Just a counter to create some delay while showing preloader.
    private static final int COUNT_LIMIT = 1;
    private static int stepCount = 1;
    

    // Used to demonstrate step couns.
    public static String STEP() {
        return stepCount++ + ". ";
    }

    private Stage applicationStage;

    public static void main(String[] args) {
        LauncherImpl.launchApplication(Launcher.class, Splashscreen.class, args);
    }

    public Launcher() {
    }

    @Override
    public void init() throws Exception {
        // Perform some heavy lifting (i.e. database start, check for application updates, etc. )
        for (int i = 0; i < COUNT_LIMIT; i++) {
            double progress = (100 * i) / COUNT_LIMIT;
            LauncherImpl.notifyPreloader(this, new Preloader.ProgressNotification(progress));
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Launcher.fxml"));
        applicationStage = primaryStage;
        Scene scene = new Scene(root);
        applicationStage.setScene(scene);
        applicationStage.show();
    }
}
