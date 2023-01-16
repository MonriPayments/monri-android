package com.monri.android.model;

public class UpdateCustomerParams {
    private final CustomerData customerData;
    private final String customerUuid;
    private final String accessToken;

    public UpdateCustomerParams(final CustomerData customerData, final String customerUuid, final String accessToken) {
        this.customerData = customerData;
        this.customerUuid = customerUuid;
        this.accessToken = accessToken;
    }

    public CustomerData getCustomer() {
        return customerData;
    }

    public String getCustomerUuid() {
        return customerUuid;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
