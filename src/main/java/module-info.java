module com.shpakovskiy.soundanalyser {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens com.shpakovskiy.soundanalyser to javafx.fxml;
    exports com.shpakovskiy.soundanalyser;
    exports com.shpakovskiy.soundanalyser.controller;
    opens com.shpakovskiy.soundanalyser.controller to javafx.fxml;
}