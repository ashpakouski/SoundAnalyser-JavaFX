package com.shpakovskiy.soundanalyser.repository;

import com.shpakovskiy.soundanalyser.model.Sound;

@FunctionalInterface
public interface SoundRetrievingListener {

    void onSoundRetrieved(Sound sound);
}