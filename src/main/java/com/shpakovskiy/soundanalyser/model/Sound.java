package com.shpakovskiy.soundanalyser.model;

import java.util.Arrays;

public class Sound {
    private final float sampleRate;
    private final int sampleSizeBits; //In some sources "Sample size" is called "Bit depth"
    private final double[] rawValues;

    public Sound(float sampleRate, int sampleSizeBits, double[] rawValues) {
        this.sampleRate = sampleRate;
        this.sampleSizeBits = sampleSizeBits;
        this.rawValues = rawValues;
    }

    public float getSampleRate() {
        return sampleRate;
    }

    public int getSampleSizeBits() {
        return sampleSizeBits;
    }

    public double[] getRawValues() {
        return rawValues;
    }

    @Override
    public String toString() {
        return "Sound{" +
                "sampleRate=" + sampleRate +
                ", sampleSizeBits=" + sampleSizeBits +
                ", rawValues=" + rawValues.length + " values" +
                '}';
    }
}