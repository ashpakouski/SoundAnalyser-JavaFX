package com.shpakovskiy.soundanalyser.repository;

import com.shpakovskiy.soundanalyser.common.utils.RawAudioConverter;
import com.shpakovskiy.soundanalyser.model.Sound;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public class DefaultSoundRepository implements SoundRepository {

    @Override
    public Sound loadFromFile(String filePath) throws IOException, UnsupportedAudioFileException {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filePath));

        int sampleSizeBits = audioInputStream.getFormat().getSampleSizeInBits();

        if (sampleSizeBits % Byte.SIZE != 0) {
            throw new UnsupportedAudioFileException(
                    "Audio files with sample size of " + sampleSizeBits + " bits " +
                            "(what is not a multiple of " + Byte.SIZE + ") are not supported");
        }

        int sampleSizeBytes = sampleSizeBits / Byte.SIZE;
        byte[] rawAudioData = audioInputStream.readAllBytes();

        return new Sound(
                audioInputStream.getFormat().getSampleRate(),
                sampleSizeBytes,
                RawAudioConverter.retrieveSoundValues(rawAudioData, sampleSizeBytes)
        );
    }
}