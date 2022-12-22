package com.monri.android.model;

public class CustomerRetrieveRequest {
    final String accessToken;
    final String customerUuid;

    public CustomerRetrieveRequest(final String accessToken, final String customerUuid) {
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
