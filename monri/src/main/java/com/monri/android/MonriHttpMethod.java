package com.monri.android;

public enum MonriHttpMethod {
    POST("POST"), GET("GET");


    private final String value;

    MonriHttpMethod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
