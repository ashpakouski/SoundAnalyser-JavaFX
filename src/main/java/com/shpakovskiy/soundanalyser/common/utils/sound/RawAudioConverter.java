package com.shpakovskiy.soundanalyser.common.utils.sound;

public class RawAudioConverter {

    //TODO: Add JavaDoc
    public static int[] retrieveSoundValues(byte[] rawAudioData, int sampleSizeBytes) {
        int[] audioData = new int[rawAudioData.length / sampleSizeBytes];

        for (int i = 0; i < rawAudioData.length / sampleSizeBytes; i++) {
            int singleValue = 0;

            for (int byteShift = sampleSizeBytes - 1; byteShift >= 0; byteShift--) {
                singleValue = singleValue | rawAudioData[i * sampleSizeBytes + byteShift];

                if (byteShift > 0) {
                    singleValue = singleValue << Byte.SIZE;
                }
            }

            audioData[i] = singleValue;
        }

        return audioData;
    }
}