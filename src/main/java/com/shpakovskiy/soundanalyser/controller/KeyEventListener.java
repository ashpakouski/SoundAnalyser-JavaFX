package com.shpakovskiy.soundanalyser.controller;

import javafx.scene.input.KeyEvent;

@FunctionalInterface
public interface KeyEventListener {

    void onKeyEvent(KeyEvent keyEvent);
}