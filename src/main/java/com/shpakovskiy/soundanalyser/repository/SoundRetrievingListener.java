package com.shpakovskiy.soundanalyser.repository;

@FunctionalInterface
public interface SoundRetrievingListener {

    void onSoundRetrieved(byte[] soundValues);
}