package org.totallyspies.evosim.utils;

public class EvosimException extends Exception {

    /**
     * If an known error occurs with a message.
     * @param message
     * @param cause
     */
    public EvosimException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * A message for the user that an error occurred.
     * @param message
     */
    public EvosimException(final String message) {
        super(message);
    }
}
