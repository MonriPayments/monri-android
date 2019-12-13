package com.monri.android.example;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jasminsuljic on 2019-12-12.
 * MonriAndroid
 */
public class NewPaymentRequest {
    @JsonProperty("add_payment_method")
    boolean addPaymentMethod;

    public NewPaymentRequest(boolean addPaymentMethod) {
        this.addPaymentMethod = addPaymentMethod;
    }

    public NewPaymentRequest() {

    }
}
