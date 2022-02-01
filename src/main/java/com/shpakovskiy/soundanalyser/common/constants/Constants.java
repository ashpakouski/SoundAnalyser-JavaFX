package com.shpakovskiy.soundanalyser.common.constants;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

public class Constants {
    public static final int DEFAULT_SIGNAL_VIEW_WIDTH = 1024;

    public static final class KeyCombinations {
        public static final KeyCombination COMMAND_O =
                new KeyCodeCombination(KeyCode.O, KeyCombination.META_DOWN);

        public static final KeyCombination COMMAND_RIGHT =
                new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.META_DOWN);

        public static final KeyCombination COMMAND_LEFT =
                new KeyCodeCombination(KeyCode.LEFT, KeyCombination.META_DOWN);
    }

    public static final class Shift {
        public static final int LEFT = -1;
        public static final int RIGHT = 1;
    }
}