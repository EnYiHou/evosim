package org.totallyspies.evosim.utils;

public class ConfigurationException extends Exception {

    /**
     * If an unknown error occurs.
     */
    public ConfigurationException() {
        super("JSON File Invalid, Try some other JSON File.");
    }

    /**
     * If an known error occurs.
     * @param message
     */
    public ConfigurationException(final String message) {
        super(message);
    }
}
