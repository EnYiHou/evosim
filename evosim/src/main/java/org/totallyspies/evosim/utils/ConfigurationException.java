package org.totallyspies.evosim.utils;

import javafx.scene.control.Alert;

public class ConfigurationException extends Exception {

    private static final String invalid = "JSON File Invalid, Try some other JSON File.";
    public ConfigurationException(){
        super(invalid);
    }

    public ConfigurationException(String message) {
        super(message);
    }
}
