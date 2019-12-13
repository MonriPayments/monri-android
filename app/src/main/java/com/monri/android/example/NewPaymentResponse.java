package com.monri.android.example;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jasminsuljic on 2019-12-05.
 * MonriAndroid
 */
class NewPaymentResponse {
    @JsonProperty("client_secret")
    String clientSecret;

    @JsonProperty("status")
    String status;

    public NewPaymentResponse(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public NewPaymentResponse() {
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getStatus() {
        return status;
    }
}
