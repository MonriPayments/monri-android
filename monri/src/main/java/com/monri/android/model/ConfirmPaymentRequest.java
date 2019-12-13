package com.monri.android.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jasminsuljic on 2019-12-05.
 * MonriAndroid
 */
public class ConfirmPaymentRequest {

    @JsonProperty("authenticity_token")
    String authenticityToken;
    @JsonProperty("payment_method") private
    PaymentMethodParams paymentMethod;

    @JsonProperty("transaction") private
    TransactionParams transaction;

    public ConfirmPaymentRequest(String authenticityToken, PaymentMethodParams paymentMethod, TransactionParams transaction) {
        this.authenticityToken = authenticityToken;
        this.paymentMethod = paymentMethod;
        this.transaction = transaction;
    }

    public ConfirmPaymentRequest() {
    }

    public String getAuthenticityToken() {
        return authenticityToken;
    }

    public PaymentMethodParams getPaymentMethod() {
        return paymentMethod;
    }

    public TransactionParams getTransaction() {
        return transaction;
    }
}
