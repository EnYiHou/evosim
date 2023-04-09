module org.totallyspies.evosim {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;
    requires com.google.common;

    opens org.totallyspies.evosim.fxml to javafx.fxml;
    opens org.totallyspies.evosim.ui to javafx.fxml;
    exports org.totallyspies.evosim.fxml;
    exports org.totallyspies.evosim.ui;
}
