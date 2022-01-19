package com.shpakovskiy.soundanalyser.controller;

import com.shpakovskiy.soundanalyser.common.components.FileChooserDialog;
import com.shpakovskiy.soundanalyser.common.utils.ui.ChartHelper;
import com.shpakovskiy.soundanalyser.repository.DefaultSoundRepository;
import com.shpakovskiy.soundanalyser.repository.SoundRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class MainController {

    private final SoundRepository soundRepository = new DefaultSoundRepository();

    @FXML
    public MenuBar appMenu;

    @FXML
    public LineChart<Number, Number> signalView;

    @FXML
    public void initialize() {
        signalView.setCreateSymbols(false);
        signalView.setLegendVisible(false);
    }

    public void onButtonOpenAction(ActionEvent actionEvent) {
        String audioFilePath = FileChooserDialog.selectFile(((MenuItem) actionEvent.getTarget())
                .getParentPopup()
                .getOwnerWindow());

        try {
            signalView.getData().add(
                    ChartHelper.convertToChartSeries(
                            soundRepository.loadFromFile(audioFilePath).getRawValues()
                    )
            );
        } catch (Exception e) {
            e.printStackTrace(); //TODO: Replace with logger and show message for user
        }
    }
}