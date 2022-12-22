package com.monri.android.model;

public class CustomerCreateRequest {
    private CustomerRequestBody customerRequestBody;
    private String accessToken;

    public CustomerCreateRequest(final CustomerRequestBody customerRequestBody, final String accessToken) {
        this.customerRequestBody = customerRequestBody;
        this.accessToken = accessToken;
    }

    public CustomerRequestBody getCustomer() {
        return customerRequestBody;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public CustomerCreateRequest setCustomer(final CustomerRequestBody customerRequestBody) {
        this.customerRequestBody = customerRequestBody;
        return this;
    }

    public CustomerCreateRequest setAccessToken(final String accessToken) {
        this.accessToken = accessToken;
        return this;
    }
}
