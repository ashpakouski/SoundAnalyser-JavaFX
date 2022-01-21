package com.shpakovskiy.soundanalyser.controller;

import com.shpakovskiy.soundanalyser.common.Constants;
import com.shpakovskiy.soundanalyser.common.components.FileChooserDialog;
import com.shpakovskiy.soundanalyser.common.utils.sound.PageRetriever;
import com.shpakovskiy.soundanalyser.common.utils.ui.ChartHelper;
import com.shpakovskiy.soundanalyser.model.Sound;
import com.shpakovskiy.soundanalyser.repository.DefaultSoundRepository;
import com.shpakovskiy.soundanalyser.repository.SoundRepository;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.*;

public class SignalViewController implements KeyEventListener {

    private final SoundRepository soundRepository = new DefaultSoundRepository();
    private Sound currentSound;
    private int currentOffset = 0;

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
                currentOffset = newValue.intValue();
                drawSignal(PageRetriever.retrieveSoundRange(
                        currentSound, currentOffset, Constants.DEFAULT_SIGNAL_VIEW_WIDTH));
            }
        });
    }

    @FXML
    public void onButtonOpenAction() {
        openNewFile();
    }

    @Override
    public void onKeyEvent(KeyEvent keyEvent) {
        if (Constants.KeyCombinations.COMMAND_O.match(keyEvent)) {
            openNewFile();
        } else {
            if (keyEvent.getCode() == KeyCode.RIGHT) {
                shiftSignalView(Constants.Shift.RIGHT);
            } else if (keyEvent.getCode() == KeyCode.LEFT) {
                shiftSignalView(Constants.Shift.LEFT);
            }
        }
    }

    private void openNewFile() {
        String audioFilePath = FileChooserDialog.selectFile(appMenu.getScene().getWindow());

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

    private void shiftSignalView(int shiftLength) {
        if (currentSound != null && (currentOffset + shiftLength) > 0 &&
                (currentOffset + shiftLength) < (currentSound.getRawValues().length - Constants.DEFAULT_SIGNAL_VIEW_WIDTH)) {

            currentOffset += shiftLength;
            drawSignal(PageRetriever.retrieveSoundRange(
                    currentSound, currentOffset, Constants.DEFAULT_SIGNAL_VIEW_WIDTH));
            signalViewScrollBar.setValue(currentOffset);
        }
    }
}