package com.monri.android.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jasminsuljic on 2019-12-05.
 * MonriAndroid
 */
public enum PaymentStatus {

    APPROVED("approved"),
    EXECUTED("executed"),
    DECLINED("declined"),
    ACTION_REQUIRED("action_required"); // pending

    private final String status;

    private static Map<String, PaymentStatus> namesMap = new HashMap<String, PaymentStatus>(3);

    static {
        namesMap.put("approved", APPROVED);
        namesMap.put("executed", EXECUTED);
        namesMap.put("declined", DECLINED);
        namesMap.put("action_required", ACTION_REQUIRED);
    }

    PaymentStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    @JsonCreator
    public static PaymentStatus forValue(String value) {
        if (value == null) {
            return null;
        }
        return namesMap.get(value.toLowerCase());
    }

    @JsonValue
    public String toValue() {
        for (Map.Entry<String, PaymentStatus> entry : namesMap.entrySet()) {
            if (entry.getValue() == this)
                return entry.getKey();
        }

        return null; // or fail
    }


}
