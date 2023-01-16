package com.monri.android.example;

import com.fasterxml.jackson.annotation.JsonProperty;

class AccessTokenResponse {
    @JsonProperty("access_token")
    String accessToken;
    @JsonProperty("token_type")
    String tokenType;
    @JsonProperty("expires_in")
    String expiresIn;
    @JsonProperty("status")
    String status;

    public AccessTokenResponse(final String accessToken, final String tokenType, final String expiresIn, final String status) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.status = status;
    }

    public AccessTokenResponse() {
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public String getStatus() {
        return status;
    }
}
