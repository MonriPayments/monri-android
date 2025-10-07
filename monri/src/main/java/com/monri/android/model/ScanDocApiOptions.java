package com.monri.android.model;

public class ScanDocApiOptions {
    private final String scanDocApiUrl;
    private final String userKey;
    private final String subClient;

    public ScanDocApiOptions(final String scanDocApiUrl, final String userKey, final String subClient) {
        this.scanDocApiUrl = scanDocApiUrl;
        this.userKey = userKey;
        this.subClient = subClient;
    }

    public String getScanDocApiUrl() {
        return scanDocApiUrl;
    }

    public String getSubClient() {
        return subClient;
    }

    public String getUserKey() {
        return userKey;
    }
}
