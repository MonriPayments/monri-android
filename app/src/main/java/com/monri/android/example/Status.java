package com.monri.android.example;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum Status {
    @JsonProperty
    APPROVED("approved"),
    @JsonProperty
    DECLINED("declined");

    private final String key;

    Status(final String key) {
        this.key = key;
    }

    @JsonCreator
    public static Status fromString(final String value) {
        if (APPROVED.key.equals(value)) return APPROVED;
        if (DECLINED.key.equals(value)) return DECLINED;
        throw new IllegalArgumentException("Unknown value for status");
    }
}
