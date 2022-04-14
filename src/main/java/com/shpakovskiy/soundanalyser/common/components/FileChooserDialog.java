package com.shpakovskiy.soundanalyser.common.components;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

//TODO: Enhance dialog with file extension filter / default path / etc.
public class FileChooserDialog {
    public static String selectFile() {
        return selectFile(new Stage());
    }

    public static String selectFile(Window parentWindow) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open new file");
        return fileChooser.showOpenDialog(parentWindow).getAbsolutePath();
    }

    public static List<String> selectFolderFiles() {
        return selectFolderFiles(new Stage());
    }

    public static List<String> selectFolderFiles(Window parentWindow) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select folder");

        File folder = directoryChooser.showDialog(parentWindow);
        String folderPath = folder.getAbsolutePath();
        String[] folderFileNames = folder.list();

        if (folderFileNames != null && (folderFileNames.length > 0)) {
            List<String> folderFiles = new ArrayList<>();

            for (String folderFileName : folderFileNames) {
                folderFiles.add(folderPath + File.separator + folderFileName);
            }

            return folderFiles;
        }

        return new ArrayList<>();
    }
}