package com.shpakovskiy.soundanalyser.repository;

import com.shpakovskiy.soundanalyser.model.Sound;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

public interface SoundRepository {

    Sound loadFromFile(String filePath) throws IOException, UnsupportedAudioFileException;

    void loadRawValues(String soundFilePath, SoundRetrievingListener soundRetrievingListener);

    void recordSound(SoundRetrievingListener soundRetrievingListener);

    void stopRecording();
}
