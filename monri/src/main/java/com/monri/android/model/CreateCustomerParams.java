package com.monri.android.model;

public class CreateCustomerParams {
    private CustomerData customerData;
    private String accessToken;

    public CreateCustomerParams(final CustomerData customerData, final String accessToken) {
        this.customerData = customerData;
        this.accessToken = accessToken;
    }

    public CustomerData getCustomer() {
        return customerData;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public CreateCustomerParams setCustomer(final CustomerData customerData) {
        this.customerData = customerData;
        return this;
    }

    public CreateCustomerParams setAccessToken(final String accessToken) {
        this.accessToken = accessToken;
        return this;
    }
}
