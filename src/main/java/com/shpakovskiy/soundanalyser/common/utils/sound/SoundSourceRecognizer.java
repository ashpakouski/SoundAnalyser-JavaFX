package com.shpakovskiy.soundanalyser.common.utils.sound;

import java.util.List;

public interface SoundSourceRecognizer {

    void loadBaseRecordings(List<String> audioSourceFiles);

    void getBestMatch(String fileForMatch);
}
