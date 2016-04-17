package com.ece.ing4.bomberman.view;


import javafx.application.Platform;

import javafx.application.Preloader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

// Affiche le splashscreen du jeux

public class Splashscreen extends Preloader {

    private Stage preloaderStage;
    private Scene scene;
    private Label progress;  
    private static final double WIDTH = 423.0;
    private static final double HEIGHT = 156.0;

    public Splashscreen() {
    }

    @Override
    public void init() throws Exception {
        Platform.runLater(() -> {
        	Label title = new Label("BOMBERMAN");
        	Label subTitle = new Label("MULTIPLAYER");
        	Font fontTitle = new Font("Heiti SC Light", 33);
        	Font fontSubTitle = new Font("Heiti SC Light", 24);
       
            title.setFont(fontTitle);
            subTitle.setFont(fontSubTitle);
            
            progress = new Label("0%");

            VBox root = new VBox(title,subTitle, progress);
            root.setAlignment(Pos.CENTER);
            scene = new Scene(root, WIDTH, HEIGHT);
        });
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.preloaderStage = primaryStage;
        preloaderStage.setScene(scene);
        preloaderStage.show();
    }

    @Override
    public void handleApplicationNotification(PreloaderNotification info) {
        if (info instanceof ProgressNotification) {
            progress.setText(((ProgressNotification) info).getProgress() + "%");
        }
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification info) {
        StateChangeNotification.Type type = info.getType();
        switch (type) {
            case BEFORE_START:
                preloaderStage.hide();
                break;
            default:
            	break;
        }
    }
}
