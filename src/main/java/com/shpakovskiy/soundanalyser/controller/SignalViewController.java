package com.shpakovskiy.soundanalyser.controller;

import com.shpakovskiy.soundanalyser.common.Constants;
import com.shpakovskiy.soundanalyser.common.components.FileChooserDialog;
import com.shpakovskiy.soundanalyser.common.utils.sound.PageRetriever;
import com.shpakovskiy.soundanalyser.common.utils.ui.ChartHelper;
import com.shpakovskiy.soundanalyser.model.Sound;
import com.shpakovskiy.soundanalyser.repository.DefaultSoundRepository;
import com.shpakovskiy.soundanalyser.repository.SoundRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollBar;

public class SignalViewController {

    private final SoundRepository soundRepository = new DefaultSoundRepository();
    private Sound currentSound;

    @FXML
    public MenuBar appMenu;

    @FXML
    public LineChart<Number, Number> signalView;

    @FXML
    public ScrollBar signalViewScrollBar;

    @FXML
    public void initialize() {
        signalView.setCreateSymbols(false);
        signalView.setLegendVisible(false);

        signalViewScrollBar.setDisable(true);
        signalViewScrollBar.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (currentSound != null) {
                drawSignal(PageRetriever.retrieveSoundRange(
                        currentSound, newValue.intValue(), Constants.DEFAULT_SIGNAL_VIEW_WIDTH));
            }
        });
    }

    @FXML
    public void onButtonOpenAction(ActionEvent actionEvent) {
        String audioFilePath = FileChooserDialog.selectFile(((MenuItem) actionEvent.getTarget())
                .getParentPopup()
                .getOwnerWindow());

        try {
            currentSound = soundRepository.loadFromFile(audioFilePath);
            int[] soundValues = currentSound.getRawValues();

            signalViewScrollBar.setMin(0);
            signalViewScrollBar.setValue(0);

            if (soundValues.length > Constants.DEFAULT_SIGNAL_VIEW_WIDTH) {
                signalViewScrollBar.setMax(soundValues.length - Constants.DEFAULT_SIGNAL_VIEW_WIDTH);
            }

            signalViewScrollBar.setDisable(soundValues.length <= Constants.DEFAULT_SIGNAL_VIEW_WIDTH);

            drawSignal(PageRetriever.retrieveSoundRange(currentSound, 0, Constants.DEFAULT_SIGNAL_VIEW_WIDTH));
        } catch (Exception e) {
            e.printStackTrace(); //TODO: Replace with logger and show message for user
        }
    }

    private void drawSignal(int[] signalValues) {
        signalView.getData().clear();
        signalView.getData().add(ChartHelper.convertToChartSeries(signalValues));
    }
}