package com.monri.android;

/**
 * Created by jasminsuljic on 2019-08-21.
 * MonriAndroidSDK
 */
public class TokenRequest {
    final String token;
    final String digest;
    final String timestamp;

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
