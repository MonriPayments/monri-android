package com.monri.android.exception;

/**
 * An {@link Exception} that represents a failure to authenticate yourself to the server.
 */
public class AuthenticationException extends MonriException {

    public AuthenticationException(String message, String requestId, Integer statusCode) {
        super(message, requestId, statusCode);
    }
}
