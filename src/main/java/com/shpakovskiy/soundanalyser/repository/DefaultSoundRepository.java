package com.shpakovskiy.soundanalyser.repository;

import com.shpakovskiy.soundanalyser.common.constants.SuitableFormat;
import com.shpakovskiy.soundanalyser.common.utils.sound.RawAudioConverter;
import com.shpakovskiy.soundanalyser.model.Sound;
import org.tritonus.sampled.convert.PCM2PCMConversionProvider;

import javax.sound.sampled.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

//TODO: Introduce one more level of abstraction or split the implementations.
//TODO: Add JavaDoc.
public class DefaultSoundRepository implements SoundRepository {
    private boolean isRecordingSound = false; //Honestly, doesn't look like a nice idea

    public Sound loadFromFile1(String filePath) throws IOException, UnsupportedAudioFileException {
        System.out.println("Trying to load: " + filePath);

        String fileExtension = filePath.split("\\.")[1];
        System.out.println("File extension: " + fileExtension);

        if (fileExtension.equalsIgnoreCase("wav") ||
                fileExtension.equalsIgnoreCase("wa1v")) {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filePath));

            int sampleSizeBits = audioInputStream.getFormat().getSampleSizeInBits();

            // System.out.println("Audio files with sample size of " + sampleSizeBits + " bits " + "(what is not a multiple of " + Byte.SIZE + ")");

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
        } else if (fileExtension.equalsIgnoreCase("pcm")) {
            int sampleSizeBits = 16;
            int sampleSizeBytes = sampleSizeBits / Byte.SIZE;
            byte[] rawAudioData = Files.readAllBytes(Paths.get(filePath));

            return new Sound(
                    44000,
                    sampleSizeBytes,
                    RawAudioConverter.retrieveSoundValues(rawAudioData, sampleSizeBytes)
            );
        }

        return null;
    }

    /*
    @Override
    public Sound loadFromFile(String filePath) throws IOException, UnsupportedAudioFileException {
        loadRawValues(filePath, soundValues -> {
            return new Sound(
                    44000,
                    2,
                    RawAudioConverter.retrieveSoundValues(rawAudioData, 2)
            );
        });
        return null;
    }
     */

    //FIXME: Method is big enough. Take measures to split it according to sub-responsibilities.
    //FIXME: STREAMS ARE NOT CLOSED AT THE MOMENT!
    //TODO: Adapt for proper multithreading support.

    /**
     * This method accepts path to the sound file and returns formatted raw values via SoundRetrievingListener callback.
     */
    @Override
    public void loadSound(String soundFilePath, SoundRetrievingListener soundRetrievingListener) {
        try {
            String fileExtension = soundFilePath.split("\\.")[1]; // For now, assume there is only one dot

            // System.out.println("File extension: " + fileExtension);

            AudioInputStream audioFileRawInputStream = null;

            if (fileExtension.equalsIgnoreCase("wav") || fileExtension.equalsIgnoreCase("wa1v")) {
                soundRetrievingListener.onSoundRetrieved(loadFromFile1(soundFilePath));
                // audioFileRawInputStream = AudioSystem.getAudioInputStream(new File(soundFilePath));

                return;
            } else if (fileExtension.equalsIgnoreCase("pcm")) {
                byte[] rawAudioData = Files.readAllBytes(Paths.get(soundFilePath));
                AudioFormat audioFormat = new AudioFormat(44100, 16, 1, true, true);
                audioFileRawInputStream = new AudioInputStream(new ByteArrayInputStream(rawAudioData), audioFormat, rawAudioData.length);
            }

            AudioFormat baseFormat = audioFileRawInputStream.getFormat();

            // System.out.println("BaseFormat: " + baseFormat);

            AudioInputStream cleanAudioInputStream = AudioSystem.getAudioInputStream(
                    baseFormat,
                    audioFileRawInputStream
            );

            //TODO: Pretty old library, doesn't support all formats.
            // Consider finding a replacement or use custom implementation, as was done above.
            PCM2PCMConversionProvider conversionProvider = new PCM2PCMConversionProvider();
            if (!conversionProvider.isConversionSupported(SuitableFormat.getProcessingFormat(), baseFormat)) {
                System.err.println("Audio format conversion is not supported.");
                soundRetrievingListener.onSoundRetrieved(null); //FIXME
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

                soundRetrievingListener.onSoundRetrieved(
                        new Sound(
                                outDinSound.getFormat().getSampleRate(),
                                outDinSound.getFormat().getSampleSizeInBits(),
                                RawAudioConverter.justToDouble(outputStream.toByteArray())
                        )
                );
            } catch (IOException e) {
                e.printStackTrace();
                soundRetrievingListener.onSoundRetrieved(null); //FIXME
            }
            //}).start();
        } catch (IOException | UnsupportedAudioFileException e) {
            e.printStackTrace();
            soundRetrievingListener.onSoundRetrieved(null); //FIXME
        }
    }

    //TODO: Watch comments to the method above.
    /*
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
                for (int i = 0; (i < 256) && isRecordingSound; i++) {
                    int count = targetDataLine.read(buffer, 0, 1024);

                    if (count > 0) {
                        outputStream.write(buffer, 0, count);
                    }

                    int bbc = 0;
                    for (byte b : buffer) {
                        bbc += b;
                    }

                    System.out.println("Still recording: " + i + "; b = " + bbc);
                }

                soundRetrievingListener.onSoundRetrieved(RawAudioConverter.justToDouble(outputStream.toByteArray()));

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
     */

    /*
    @Override
    public void stopRecording() {
        isRecordingSound = false;
    }
     */
}