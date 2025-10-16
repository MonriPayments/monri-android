package com.monri.android.example;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jasminsuljic on 2019-12-05.
 * MonriAndroid
 */
class NewPaymentResponse {
    private String clientSecret;
    private Status status;
    private String id;

    public NewPaymentResponse(
            @JsonProperty("status") Status status,
            @JsonProperty("id") String id,
            @JsonProperty("client_secret") String clientSecret
    ) {
        this.clientSecret = clientSecret;
        this.id = id;
        this.status = status;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public Status getStatus() {
        return status;
    }

    public String getId() {
        return id;
    }
}
