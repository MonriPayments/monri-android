package com.monri.android.example;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jasminsuljic on 2019-12-12.
 * MonriAndroid
 */
public class NewPaymentRequest {
    @JsonProperty("amount")
    int amount;

    @JsonProperty("order_number")
    String orderNumber;

    @JsonProperty("currency")
    String currency;

    @JsonProperty("transaction_type")
    String transactionType;

    @JsonProperty("order_info")
    String orderInfo;

    @JsonProperty("scenario")
    String scenario;

    public NewPaymentRequest(int amount, String orderNumber, String currency, String transactionType, String orderInfo, String scenario) {
        this.amount = amount;
        this.orderNumber = orderNumber;
        this.currency = currency;
        this.scenario = scenario;
        this.transactionType = transactionType;
        this.orderInfo = orderInfo;
    }
}
