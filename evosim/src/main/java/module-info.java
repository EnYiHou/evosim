module org.totallyspies.evosim {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;

    opens org.totallyspies.evosim to javafx.fxml;
    opens org.totallyspies.evosim.fxml to javafx.fxml;
    exports org.totallyspies.evosim;
    exports org.totallyspies.evosim.fxml;
}
