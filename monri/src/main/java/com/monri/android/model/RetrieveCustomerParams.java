package com.monri.android.model;

public class RetrieveCustomerParams {
    final String accessToken;
    final String customerUuid;

    public RetrieveCustomerParams(final String accessToken, final String customerUuid) {
        this.accessToken = accessToken;
        this.customerUuid = customerUuid;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getCustomerUuid() {
        return customerUuid;
    }
}
