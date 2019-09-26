package com.monri.android;

/**
 * Created by jasminsuljic on 2019-09-26.
 * MonriAndroidSDK
 */
public class ValidationException extends Exception {

    int validationCode;

    private ValidationException(String message, int validationCode) {
        super(message);
        this.validationCode = validationCode;
    }

    public int getValidationCode() {
        return validationCode;
    }

    public static ValidationException create(@ValidationCode int validationCode) {
        // TODO: fix message, maybe R.string.something?
        return new ValidationException("", validationCode);
    }

}
