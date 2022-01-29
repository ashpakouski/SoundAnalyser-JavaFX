package com.shpakovskiy.soundanalyser.common.utils.math;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

public class FourierTransform {
    public static double[] getFrequencySpectrum(double[] defaultRange) {
        FastFourierTransformer fastFourierTransformer = new FastFourierTransformer(DftNormalization.STANDARD);
        Complex[] transformResult = fastFourierTransformer.transform(defaultRange, TransformType.FORWARD);

        double[] transformedRange = new double[transformResult.length / 2];

        for (int i = 0; i < transformedRange.length; i++) {
            transformedRange[i] = Math.sqrt(Math.pow(transformResult[i].getReal(), 2));
        }

        return transformedRange;
    }
}