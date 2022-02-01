package com.shpakovskiy.soundanalyser.common.components;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;

//TODO: Enhance dialog with file extension filter / default path / etc.
public class FileChooserDialog {
    public static String selectFile(Window parentWindow) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("D:\\JavaProjects\\SoundAnalyser-JavaFX\\src\\main\\resources\\com\\shpakovskiy\\soundanalyser\\testRecordings"));
        fileChooser.setTitle("Open new file");

        File selectedFile = fileChooser.showOpenDialog(parentWindow);

        if (selectedFile != null) {
            return selectedFile.getAbsolutePath();
        }

        return null;
    }

    public static String selectFile() {
        return selectFile(new Stage());
    }
}