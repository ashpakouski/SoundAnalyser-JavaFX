package com.shpakovskiy.soundanalyser.model;

//Basically, it's "Frequency of amplitude" or frequency of harmonic with the corresponding amplitude.
public class AmplitudeFrequency {
    private final double amplitude;
    private final long frequency;

    public AmplitudeFrequency(double amplitude, long frequency) {
        this.amplitude = amplitude;
        this.frequency = frequency;
    }

    public double getAmplitude() {
        return amplitude;
    }

    public long getFrequency() {
        return frequency;
    }
}