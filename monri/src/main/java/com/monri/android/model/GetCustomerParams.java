package com.monri.android.model;

public class GetCustomerParams {
    final String accessToken;
    final String customerUuid;

    public GetCustomerParams(final String accessToken, final String customerUuid) {
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
