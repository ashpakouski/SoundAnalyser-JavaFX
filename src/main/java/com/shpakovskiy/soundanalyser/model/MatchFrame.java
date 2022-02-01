package com.shpakovskiy.soundanalyser.model;

public class MatchFrame {
    private final int time;
    private final int songId;

    public MatchFrame(int songId, int time) {
        this.songId = songId;
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public int getSongId() {
        return songId;
    }
}