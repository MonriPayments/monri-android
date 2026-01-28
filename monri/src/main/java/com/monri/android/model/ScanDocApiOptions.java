package com.monri.android.model;

public class ScanDocApiOptions {
    private final boolean acceptTermsAndConditions;
    private final String scanDocApiUrl;
    private final byte[] userKey;
    private final String subClient;

    public ScanDocApiOptions(final String scanDocApiBaseUrl, final String userKey, final String subClient, final boolean acceptTermsAndConditions) {
        this.scanDocApiUrl = scanDocApiBaseUrl;
        this.userKey = userKey.getBytes();
        this.subClient = subClient;
        this.acceptTermsAndConditions = acceptTermsAndConditions;
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

    public boolean areTermsAndConditionsAccepted() {
        return acceptTermsAndConditions;
    }
}
