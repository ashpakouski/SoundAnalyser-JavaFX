package com.shpakovskiy.soundanalyser.controller;

import com.shpakovskiy.soundanalyser.common.components.FileChooserDialog;
import com.shpakovskiy.soundanalyser.common.constants.Constants;
import com.shpakovskiy.soundanalyser.common.utils.math.FourierTransform;
import com.shpakovskiy.soundanalyser.common.utils.sound.PageRetriever;
import com.shpakovskiy.soundanalyser.common.utils.sound.SoundRecognizer;
import com.shpakovskiy.soundanalyser.common.utils.ui.ChartHelper;
import com.shpakovskiy.soundanalyser.model.Sound;
import com.shpakovskiy.soundanalyser.repository.DefaultSoundRepository;
import com.shpakovskiy.soundanalyser.repository.SoundRepository;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class SignalViewController implements KeyEventListener {

    private final SoundRepository soundRepository = new DefaultSoundRepository();
    private Sound currentSound;
    private String currentSoundPath = null;
    private int currentOffset = 0;

    @FXML
    private MenuBar appMenu;

    @FXML
    private BarChart<String, Number> signalView;

    @FXML
    private BarChart<String, Number> spectrumView;

    @FXML
    private BarChart<String, Number> distributionDensityView;

    @FXML
    private ScrollBar signalViewScrollBar;

    @FXML
    private void initialize() {
        signalView.setLegendVisible(false);
        spectrumView.setLegendVisible(false);
        distributionDensityView.setLegendVisible(false);

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
    private void onOpenFileAction() {
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

        currentSoundPath = audioFilePath;

        if (audioFilePath != null) {
            try {
                currentSound = soundRepository.loadFromFile(audioFilePath);
                double[] soundValues = currentSound.getRawValues();

                signalViewScrollBar.setMin(0);
                signalViewScrollBar.setValue(0);

                if (soundValues.length > Constants.DEFAULT_SIGNAL_VIEW_WIDTH) {
                    signalViewScrollBar.setMax(soundValues.length - Constants.DEFAULT_SIGNAL_VIEW_WIDTH);
                }

                signalViewScrollBar.setDisable(soundValues.length <= Constants.DEFAULT_SIGNAL_VIEW_WIDTH);

                drawSignal(PageRetriever.retrieveSoundRange(currentSound, 0, Constants.DEFAULT_SIGNAL_VIEW_WIDTH));

                drawDistributionDensitySignal(PageRetriever.getDistributionDensity(currentSound));
            } catch (Exception e) {
                e.printStackTrace(); //TODO: Replace with logger and show message for user
            }
        }
    }

    private void drawDistributionDensitySignal(double[] signalValues) {
        distributionDensityView.getData().clear();
        distributionDensityView.getData().add(ChartHelper.convertToBarChartSeries(signalValues));
    }

    private void drawSignal(double[] signalValues) {
        drawTimeDomainSignal(signalValues);
        drawFrequencyDomainSignal(signalValues);
    }

    private void drawTimeDomainSignal(double[] signalValues) {
        signalView.getData().clear();
        signalView.getData().add(ChartHelper.convertToBarChartSeries(signalValues));
    }

    private void drawFrequencyDomainSignal(double[] signalValues) {
        spectrumView.getData().clear();
        spectrumView.getData().add(ChartHelper.convertToBarChartSeries(
                FourierTransform.getFrequencySpectrum(signalValues)
        ));
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

    @FXML
    public void onRecognizeMelodicSourceAction() {
        SoundRecognizer soundRecognizer = new SoundRecognizer(FileChooserDialog.selectFolderFiles());
        soundRecognizer.loadSoundRecordings();

        //soundRepository.recordSound(soundRecognizer::getBestMatch);
        soundRecognizer.getBestMatch(currentSoundPath);
    }
}