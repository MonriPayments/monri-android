package com.monri.android.example;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jasminsuljic on 2019-12-05.
 * MonriAndroid
 */
class NewPaymentResponse {
    private String clientSecret;
    private String status;
    private String id;

    public NewPaymentResponse(
            @JsonProperty("status") String status,
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

    public String getStatus() {
        return status;
    }

    public String getId() {
        return id;
    }
}
