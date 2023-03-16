module org.totallyspies.evosim {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.totallyspies.evosim to javafx.fxml;
    exports org.totallyspies.evosim;
    exports org.totallyspies.evosim.engine;
}
