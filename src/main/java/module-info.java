module com.shpakovskiy.soundanalyser {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.shpakovskiy.soundanalyser to javafx.fxml;
    exports com.shpakovskiy.soundanalyser;
}