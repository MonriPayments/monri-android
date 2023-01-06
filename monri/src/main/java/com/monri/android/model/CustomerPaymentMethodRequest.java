package com.monri.android.model;

public class CustomerPaymentMethodRequest {
    private final String customerUuid;
    private final long limit;
    private final long offset;
    private final String accessToken;

    public CustomerPaymentMethodRequest(final String customerUuid, final long limit, final long offset, final String accessToken) {
        this.customerUuid = customerUuid;
        this.limit = limit;
        this.offset = offset;
        this.accessToken = accessToken;
    }

    public String getCustomerUuid() {
        return customerUuid;
    }

    public long getLimit() {
        return limit;
    }

    public long getOffset() {
        return offset;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
