package com.monri.android;

/**
 * Created by jasminsuljic on 2019-08-21.
 * MonriAndroidSDK
 */
public class TokenRequest {
    private final String token;
    private final String digest;
    private final String timestamp;

    public TokenRequest(String token, String digest, String timestamp) {
        this.token = token;
        this.digest = digest;
        this.timestamp = timestamp;
    }

    public String getToken() {
        return token;
    }

    public String getDigest() {
        return digest;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
