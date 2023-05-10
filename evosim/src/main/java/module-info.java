module org.totallyspies.evosim {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;
    requires com.google.common;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires static lombok;

    opens org.totallyspies.evosim.fxml to javafx.fxml;
    opens org.totallyspies.evosim.ui to javafx.fxml;
    opens org.totallyspies.evosim.neuralnetwork to com.fasterxml.jackson.databind;
    opens org.totallyspies.evosim.utils to com.fasterxml.jackson.databind;
    opens org.totallyspies.evosim.entities to com.fasterxml.jackson.databind;
    opens org.totallyspies.evosim.geometry to com.fasterxml.jackson.databind;
    exports org.totallyspies.evosim.fxml;
    exports org.totallyspies.evosim.ui;
    exports org.totallyspies.evosim.geometry;
    exports org.totallyspies.evosim.entities;
    exports org.totallyspies.evosim.utils;
    exports org.totallyspies.evosim.neuralnetwork;
}
