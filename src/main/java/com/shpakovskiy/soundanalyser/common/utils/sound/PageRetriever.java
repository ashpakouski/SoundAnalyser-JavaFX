package com.shpakovskiy.soundanalyser.common.utils.sound;

import com.shpakovskiy.soundanalyser.model.Sound;

import java.util.Map;

public class PageRetriever {

    //TODO: Think of better name
    public static double[] retrieveSoundRange(Sound sound, int offset, int limit) {
        int signalRangeLength = Math.min(sound.getRawValues().length - offset, limit);
        double[] signalRange = new double[signalRangeLength];

        System.arraycopy(sound.getRawValues(), offset, signalRange, 0, signalRangeLength);

        return signalRange;
    }

    public static double[] getDistributionDensity(Sound sound) {
        double[] distributionDensity = new double[Byte.MAX_VALUE * 2 + 1];

        for (double currentAmplitude : sound.getRawValues()) {
            //System.out.println(">>> AMP = " + currentAmplitude);
            distributionDensity[(int) (Math.abs(currentAmplitude))]++;
        }

        return distributionDensity;
    }
}