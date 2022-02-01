package com.shpakovskiy.soundanalyser.common.utils.math;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

public class FastFourierTransform {

    //TODO: Move width of window to constants or make this value adjustable.
    //I guess "Spectra" is plural of "spectrum"
    private Complex[][] getWindowsSpectra(byte[] soundValues) {
        int soundLengthBytes = soundValues.length;
        int windowsNumber = soundLengthBytes / 4096; //Yes, it doesn't include short "tail"

        Complex[][] spectra = new Complex[windowsNumber][];

        for (int windowNumber = 0; windowNumber < windowsNumber; windowNumber++) {
            Complex[] singleWindowSpectrum = new Complex[4096];

            for (int i = 0; i < 4096; i++) {
                singleWindowSpectrum[i] = new Complex(soundValues[(windowNumber * 4096) + i], 0);
            }

            FastFourierTransformer fastFourierTransformer = new FastFourierTransformer(DftNormalization.STANDARD);
            spectra[windowNumber] = fastFourierTransformer.transform(singleWindowSpectrum, TransformType.FORWARD);
        }

        return spectra;
    }
}
