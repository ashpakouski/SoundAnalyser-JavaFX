package com.shpakovskiy.soundanalyser.model;

public class Sound {
    private final float sampleRate;
    private final int sampleSizeBytes;
    private final int[] rawValues;

    public Sound(float sampleRate, int sampleSizeBytes, int[] rawValues) {
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

    public int[] getRawValues() {
        return rawValues;
    }
}
