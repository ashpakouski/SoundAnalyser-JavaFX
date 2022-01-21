package com.shpakovskiy.soundanalyser;

import com.shpakovskiy.soundanalyser.controller.KeyEventListener;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SoundAnalyserApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SoundAnalyserApplication.class.getResource("views/MainView.fxml"));
        Parent root = fxmlLoader.load();

        KeyEventListener signalViewController = fxmlLoader.getController();

        Scene scene = new Scene(root, 1100, 600);

        scene.setOnKeyPressed(signalViewController::onKeyEvent);

        stage.setTitle("Sound analyzer");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}