package com.monri.android.model;

public class CustomerUpdateRequest {
    private final CustomerRequestBody customerRequestBody;
    private final String customerUuid;
    private final String accessToken;

    public CustomerUpdateRequest(final CustomerRequestBody customerRequestBody, final String customerUuid, final String accessToken) {
        this.customerRequestBody = customerRequestBody;
        this.customerUuid = customerUuid;
        this.accessToken = accessToken;
    }

    public CustomerRequestBody getCustomer() {
        return customerRequestBody;
    }

    public String getCustomerUuid() {
        return customerUuid;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
