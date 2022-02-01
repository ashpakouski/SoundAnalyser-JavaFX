package com.shpakovskiy.soundanalyser.repository;

import com.shpakovskiy.soundanalyser.common.constants.SuitableFormat;
import com.shpakovskiy.soundanalyser.common.utils.sound.RawAudioConverter;
import com.shpakovskiy.soundanalyser.model.Sound;
import org.tritonus.sampled.convert.PCM2PCMConversionProvider;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

//TODO: Introduce one more level of abstraction or split the implementations.
//TODO: Add JavaDoc.
public class DefaultSoundRepository implements SoundRepository {
    private boolean isRecordingSound = false; //Honestly, doesn't look like a nice idea

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

    //FIXME: Method is big enough. Take measures to split it according to sub-responsibilities.
    //FIXME: STREAMS ARE NOT CLOSED AT THE MOMENT!
    //TODO: Adapt for proper multithreading support.
    @Override
    public void loadRawValues(String soundFilePath, SoundRetrievingListener soundRetrievingListener) {
        try {
            AudioInputStream audioFileRawInputStream = AudioSystem.getAudioInputStream(new File(soundFilePath));
            AudioFormat baseFormat = audioFileRawInputStream.getFormat();
            AudioFormat readFormat = SuitableFormat.getReadFormat(baseFormat);

            AudioInputStream cleanAudioInputStream = AudioSystem.getAudioInputStream(
                    readFormat,
                    audioFileRawInputStream
            );

            //TODO: Pretty old library, doesn't support all formats.
            // Consider finding a replacement or use custom implementation, as was done above.
            PCM2PCMConversionProvider conversionProvider = new PCM2PCMConversionProvider();
            if (!conversionProvider.isConversionSupported(SuitableFormat.getProcessingFormat(), readFormat)) {
                soundRetrievingListener.onSoundRetrieved(null);
                return;
            }

            final AudioInputStream outDinSound = conversionProvider.getAudioInputStream(
                    SuitableFormat.getProcessingFormat(),
                    cleanAudioInputStream
            );

            //new Thread(() -> {
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            try {
                int count;
                do {
                    count = outDinSound.read(buffer, 0, 1024);

                    if (count > 0) {
                        outputStream.write(buffer, 0, count);
                    }
                } while (count > 0);

                soundRetrievingListener.onSoundRetrieved(outputStream.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
                soundRetrievingListener.onSoundRetrieved(null);
            }
            //}).start();
        } catch (IOException | UnsupportedAudioFileException e) {
            e.printStackTrace();
            soundRetrievingListener.onSoundRetrieved(null);
        }
    }

    //TODO: Watch comments to the method above.
    @Override
    public void recordSound(SoundRetrievingListener soundRetrievingListener) {
        try {
            AudioFormat audioFormat = SuitableFormat.getProcessingFormat();
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
            TargetDataLine targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);

            try {
                targetDataLine.open(audioFormat);
                targetDataLine.start();
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            }

            //new Thread(() -> {
            isRecordingSound = true;
            byte[] buffer = new byte[1024]; //TODO: Move constants to the place for constants.

            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                for (int i = 0; (i < 1024) && isRecordingSound; i++) {
                    int count = targetDataLine.read(buffer, 0, 1024);

                    if (count > 0) {
                        outputStream.write(buffer, 0, count);
                    }
                }

                soundRetrievingListener.onSoundRetrieved(outputStream.toByteArray());

                targetDataLine.close();
            } catch (IOException e) {
                e.printStackTrace();
                soundRetrievingListener.onSoundRetrieved(null);
            } finally {
                isRecordingSound = false;
            }
            //}).start();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            soundRetrievingListener.onSoundRetrieved(null);
        }
    }

    @Override
    public void stopRecording() {
        isRecordingSound = false;
    }
}