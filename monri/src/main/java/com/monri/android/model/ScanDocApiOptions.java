package com.monri.android.model;

public class ScanDocApiOptions {
    private final String scanDocApiUrl;
    private final byte[] userKey;
    private final String subClient;

    public ScanDocApiOptions(final String scanDocApiBaseUrl, final String userKey, final String subClient) {
        this.scanDocApiUrl = scanDocApiBaseUrl;
        this.userKey = userKey.getBytes();
        this.subClient = subClient;
    }

    public String getScanDocApiUrl() {
        return scanDocApiUrl;
    }

    public String getSubClient() {
        return subClient;
    }

    public byte[] getUserKey() {
        return userKey;
    }
}
