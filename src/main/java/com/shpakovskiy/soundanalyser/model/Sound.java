package com.shpakovskiy.soundanalyser.model;

public class Sound {
    private final float sampleRate;
    private final int sampleSizeBytes; //In some sources "Sample size" is called "Bit depth"
    private final double[] rawValues;

    public Sound(float sampleRate, int sampleSizeBytes, double[] rawValues) {
        this.sampleRate = sampleRate;
        this.sampleSizeBytes = sampleSizeBytes;
        this.rawValues = rawValues;
    }

    public float getSampleRate() {
        return sampleRate;
    }

    public int getSampleSizeBytes() {
        return sampleSizeBytes;
    }

    public double[] getRawValues() {
        return rawValues;
    }
}