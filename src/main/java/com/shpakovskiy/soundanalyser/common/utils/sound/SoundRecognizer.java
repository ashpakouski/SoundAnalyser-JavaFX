package com.shpakovskiy.soundanalyser.common.utils.sound;

import com.shpakovskiy.soundanalyser.model.AmplitudeFrequency;
import com.shpakovskiy.soundanalyser.model.MatchFrame;
import com.shpakovskiy.soundanalyser.repository.DefaultSoundRepository;
import com.shpakovskiy.soundanalyser.repository.SoundRepository;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import java.util.*;

import static java.util.stream.Collectors.toMap;

public class SoundRecognizer implements SoundSourceRecognizer {
    private final SoundRepository soundRepository = new DefaultSoundRepository();
    private final List<String> audioSourceFiles = new ArrayList<>();
    private final Map<String, List<MatchFrame>> windowMatches = new HashMap<>();

    public static final int FOURIER_WINDOW_SIZE = 4096;

    private final int[] FREQUENCY_RANGE = new int[]{
            10, 30, 50, 70, 90, 110
    };

    @Override
    public void loadBaseRecordings(List<String> audioSourceFiles) {
        this.audioSourceFiles.clear();
        this.audioSourceFiles.addAll(audioSourceFiles); // FIXME: I don't like this approach

        for (int i = 0; i < audioSourceFiles.size(); i++) {
            final int constId = i;
            //System.out.println("Trying to load: " + audioSourceFiles.get(constId));
            soundRepository.loadSound(audioSourceFiles.get(constId), sound -> {
                if (sound != null) {
                    //System.out.println("Loaded [" + constId + "]: " + audioSourceFiles.get(constId) + " -> " + sound.getRawValues().length);

                    // 1. Retrieve spectrum of every window in soundValues
                    Complex[][] windowSpectra = getSignalSpectra(sound.getRawValues());

                    // 2. Analyze it and add save necessary information TODO
                    addSoundInfo(windowSpectra, constId);
                    //System.out.println("[" + constId + "] " + audioSourceFiles.get(constId) + " -> Done");
                } else {
                    System.out.println("Failed to load [" + constId + "]");
                }
            });
        }
    }

    private void addSoundInfo(Complex[][] windowSpectra, long songId) {
        AmplitudeFrequency[][] amplitudeFrequencies = new AmplitudeFrequency[windowSpectra.length][FREQUENCY_RANGE.length];

        for (int windowId = 0; windowId < windowSpectra.length; windowId++) { //Enumerating windows
            String windowHash = getWindowHash(windowSpectra[windowId], amplitudeFrequencies[windowId]);
            addHash(windowHash, (int) songId, windowId);
        }
    }

    /**
     * @param windowSpectrum      Spectrum of currently analysed window
     * @param harmonicFrequencies "Out parameter". Used to save the highest amplitude of every frequency range in
     *                            currently analysed window (with corresponding exact frequency)
     */
    private String getWindowHash(Complex[] windowSpectrum, AmplitudeFrequency[] harmonicFrequencies) {
        for (int harmonicFrequency = FREQUENCY_RANGE[0];
             harmonicFrequency < FREQUENCY_RANGE[FREQUENCY_RANGE.length - 1] - 1;
             harmonicFrequency++) { // Enumerating each frequency

            // Get the magnitude of current frequency
            double harmonicAmplitude = Math.log(windowSpectrum[harmonicFrequency].abs() + 1);

            // Get the index of harmonic's frequency range
            int frequencyIndex = getFrequencyIndex(harmonicFrequency);

            // Save the highest magnitude and corresponding frequency (per frequency range)
            if (harmonicAmplitude > (harmonicFrequencies[frequencyIndex] != null
                    ? harmonicFrequencies[frequencyIndex].getAmplitude()
                    : 0)) {

                harmonicFrequencies[frequencyIndex] = new AmplitudeFrequency(harmonicAmplitude, harmonicFrequency);
            }
        }

        String frequenciesHash = getFrequenciesHash(harmonicFrequencies);
        //System.out.println("Hash: " + frequenciesHash);
        return frequenciesHash;
    }

    // Find out in which range
    private int getFrequencyIndex(int harmonicFrequency) {
        int frequencyIndex = 0;

        while (FREQUENCY_RANGE[frequencyIndex] < harmonicFrequency) {
            frequencyIndex++;
        }

        return frequencyIndex;
    }

    private String getFrequenciesHash(AmplitudeFrequency[] amplitudeFrequencies) {
        List<String> allFrequencies = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            allFrequencies.add(
                    String.valueOf(
                            amplitudeFrequencies[i] != null
                                    ? amplitudeFrequencies[i].getFrequency()
                                    : 0)
            );
        }

        return String.join(",", allFrequencies);
    }

    private void addHash(String windowHash, int songId, int windowId) {
        List<MatchFrame> matchedFrames = windowMatches.get(windowHash);

        if (matchedFrames == null) {
            matchedFrames = new ArrayList<>();
            MatchFrame matchFrame = new MatchFrame(songId, windowId);
            matchedFrames.add(matchFrame);
            windowMatches.put(windowHash, matchedFrames);
        } else {
            matchedFrames.add(new MatchFrame(songId, windowId));
        }
    }

    /**
     * @param windowSpectra Spectrum of every window of sound, that's being recognized
     */
    private Map<Integer, Map<Integer, Integer>> getMatchMap(Complex[][] windowSpectra) {
        // <songId, <offsetBetweenWindows, hitsNumber>>
        Map<Integer, Map<Integer, Integer>> matchMap = new HashMap<>();

        // Perform per-window analysis for sound, that's being recognized
        AmplitudeFrequency[][] amplitudeFrequencies = new AmplitudeFrequency[windowSpectra.length][FREQUENCY_RANGE.length];

        // Enumerating windows
        for (int windowId = 0; windowId < windowSpectra.length; windowId++) {

            // Retrieve hash of currently analyzed window
            String analyzedWindowHash = getWindowHash(windowSpectra[windowId], amplitudeFrequencies[windowId]);

            // Search recordings with the same hash
            List<MatchFrame> matchFrames = windowMatches.get(analyzedWindowHash);

            if (matchFrames != null) {
                // Enumerating every frame, which matches currently analysed one
                for (MatchFrame matchFrame : matchFrames) {

                    // Find "delta" between existing sound window and analyzed one
                    int offset = Math.abs(matchFrame.getWindowId()/* - windowId*/);
                    Map<Integer, Integer> tmpMap = matchMap.get(matchFrame.getSongId());

                    if (tmpMap == null) {
                        tmpMap = new HashMap<>();
                        tmpMap.put(offset, 1);
                        matchMap.put(matchFrame.getSongId(), tmpMap);
                    } else {
                        tmpMap.merge(offset, 1, Integer::sum);
                    }
                }
            }
        }

        return matchMap;
    }

    // TODO: Move execution to separate thread and implement callback for value returning
    @Override
    public void getBestMatch(String fileForMatch) {
        soundRepository.loadSound(fileForMatch, sound -> getBestMatch(sound.getRawValues()));
    }

    public void getBestMatch(double[] soundValues) {
        int bestCount = 0;
        int bestSong = -1;

        Map<String, Integer> songMatches = new HashMap<>();
        Map<Integer, Map<Integer, Integer>> matchMap = getMatchMap(getSignalSpectra(soundValues));

        for (int id = 0; id < audioSourceFiles.size(); id++) {
            Map<Integer, Integer> tmpMap = matchMap.get(id);

            if (tmpMap == null) {
                tmpMap = new HashMap<>();
            }

            int bestCountForSong = 0;

            for (Map.Entry<Integer, Integer> entry : tmpMap.entrySet()) {
                if (entry.getValue() > bestCountForSong) {
                    bestCountForSong += entry.getValue();
                    //bestCountForSong = entry.getValue();
                }
            }

            songMatches.put(audioSourceFiles.get(id), bestCountForSong);

            if (bestCountForSong > bestCount) {
                bestCount = bestCountForSong;
                bestSong = id;
            }
        }

        if (bestSong != -1) {
            Map<String, Integer> sortedResults = songMatches
                    .entrySet()
                    .stream()
                    .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                    .collect(
                            toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                    LinkedHashMap::new));

            for (String songName : sortedResults.keySet()) {
                Integer value = songMatches.get(songName);
                System.out.println(songName + " -> " + value);
            }

            System.out.println("Best match: " + audioSourceFiles.get(bestSong));
        } else {
            System.err.println("Best song couldn't be found");
        }
    }

    /**
     * "Spectra" is plural of "spectrum"
     * <p>
     * This method accepts raw sound values, splits it to the windows of equal size (fourierWindowSize) and
     * retrieves spectrum for each of them.
     */
    private Complex[][] getSignalSpectra(double[] soundValues) {
        if (soundValues != null) {
            int soundLengthBytes = soundValues.length;
            int windowsNumber = soundLengthBytes / FOURIER_WINDOW_SIZE; //Yes, it doesn't include short "tail"

            Complex[][] spectra = new Complex[windowsNumber][];

            for (int windowNumber = 0; windowNumber < windowsNumber; windowNumber++) {
                Complex[] singleWindowSpectrum = new Complex[FOURIER_WINDOW_SIZE];

                for (int i = 0; i < FOURIER_WINDOW_SIZE; i++) {
                    singleWindowSpectrum[i] = new Complex(soundValues[(windowNumber * FOURIER_WINDOW_SIZE) + i], 0);
                }

                FastFourierTransformer fastFourierTransformer = new FastFourierTransformer(DftNormalization.STANDARD);
                spectra[windowNumber] = fastFourierTransformer.transform(singleWindowSpectrum, TransformType.FORWARD);
            }

            return spectra;
        } else {
            return new Complex[0][0]; //FIXME
        }
    }
}