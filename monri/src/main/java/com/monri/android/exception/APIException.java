package com.monri.android.exception;

/**
 * An {@link Exception} that represents an internal problem with Monri's servers.
 */
public class APIException extends MonriException {

    public APIException(String message, String requestId, Integer statusCode, Throwable e) {
        super(message, requestId, statusCode, e);
    }
}
