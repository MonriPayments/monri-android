package com.monri.android;


import java.util.List;

/**
 * Created by jasminsuljic on 2019-09-26.
 * MonriAndroidSDK
 */
public class ApiException extends Exception {

    private List<String> errors;

    public ApiException() {
    }

    public ApiException(List<String> errors) {
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
