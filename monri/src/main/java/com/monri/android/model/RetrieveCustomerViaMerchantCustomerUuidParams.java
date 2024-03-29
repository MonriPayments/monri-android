package com.monri.android.model;

public class RetrieveCustomerViaMerchantCustomerUuidParams {
    final String accessToken;
    final String merchantCustomerUuid;

    public RetrieveCustomerViaMerchantCustomerUuidParams(final String accessToken, final String merchantCustomerUuid) {
        this.accessToken = accessToken;
        this.merchantCustomerUuid = merchantCustomerUuid;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getMerchantCustomerUuid() {
        return merchantCustomerUuid;
    }
}
