package org.totallyspies.evosim.utils;

public class EvosimException extends Exception {

    /**
     * If an unknown error occurs.
     */
    public EvosimException() {
        super("JSON File Invalid, Try some other JSON File.");
    }

    /**
     * If an known error occurs.
     * @param message
     */
    public EvosimException(final String message) {
        super(message);
    }
}
