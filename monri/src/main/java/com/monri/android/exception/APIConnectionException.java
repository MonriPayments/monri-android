package com.monri.android.exception;

/**
 * An {@link Exception} that represents a failure to connect to Monri's API.
 */
public class APIConnectionException extends MonriException {

    public APIConnectionException(String message) {
        super(message, null, 0);
    }

    public APIConnectionException(String message, Throwable e) {
        super(message, null, 0, e);
    }

}
