package com.shpakovskiy.soundanalyser.common.components;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

//TODO: Enhance dialog with file extension filter / default path / etc.
public class FileChooserDialog {
    public static String selectFile(Window parentWindow) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open new file");
        return fileChooser.showOpenDialog(parentWindow).getAbsolutePath();
    }

    public static String selectFile() {
        return selectFile(new Stage());
    }
}