package com.shpakovskiy.soundanalyser.common.constants;

import javax.sound.sampled.AudioFormat;

public class SuitableFormat {

    public static AudioFormat getReadFormat(AudioFormat baseFormat) {
        return new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                baseFormat.getSampleRate(), 16,
                baseFormat.getChannels(),
                baseFormat.getChannels() * 2,
                baseFormat.getSampleRate(),
                false
        );
    }

    public static AudioFormat getProcessingFormat() {
        return new AudioFormat(
                44100, //Sample rate
                8,     //Sample rate in bits
                1,     //Channels (mono)
                true,  //Signed
                true   //Big endian
        );
    }
}