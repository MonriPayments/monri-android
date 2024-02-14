package com.monri.android.model;

/**
 * Created by jasminsuljic on 2019-12-05.
 * MonriAndroid
 */
public enum PaymentStatus {

    APPROVED("approved"),
    EXECUTED("executed"),
    DECLINED("declined"),
    ACTION_REQUIRED("action_required"), // pending
    PAYMENT_METHOD_REQUIRED("payment_method_required");

    private final String status;

    PaymentStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static PaymentStatus forValue(final String value) {
        if (value == null) {
            return null;
        }

        final PaymentStatus[] paymentStatusValues = values();
        for (final PaymentStatus paymentStatusCandidate : paymentStatusValues) {
            if (paymentStatusCandidate.getStatus().equals(value)) {
                return paymentStatusCandidate;
            }
        }

        return null;
    }
}
