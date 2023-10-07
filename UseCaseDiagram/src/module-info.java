module UseCaseDiagram {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires java.desktop;

    opens sample to javafx.fxml;
    exports sample;
}
