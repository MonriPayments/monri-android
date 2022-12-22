package com.monri.android.model;

public class CustomerPaymentMethodRequest {
    private final String monriCustomerUuid;
    private final long limit;
    private final long offset;
    private final String accessToken;

    public CustomerPaymentMethodRequest(final String monriCustomerUuid, final long limit, final long offset, final String accessToken) {
        this.monriCustomerUuid = monriCustomerUuid;
        this.limit = limit;
        this.offset = offset;
        this.accessToken = accessToken;
    }

    public String getMonriCustomerUuid() {
        return monriCustomerUuid;
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
