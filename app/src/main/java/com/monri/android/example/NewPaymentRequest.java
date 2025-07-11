package com.monri.android.example;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jasminsuljic on 2019-12-12.
 * MonriAndroid
 */
public class NewPaymentRequest {
    @JsonProperty("amount")
    private int amount;

    @JsonProperty("order_number")
    private String orderNumber;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("transaction_type")
    private String transactionType;

    @JsonProperty("order_info")
    private String orderInfo;

    @JsonProperty("scenario")
    private String scenario;

    public int getAmount() {
        return amount;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getCurrency() {
        return currency;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String getOrderInfo() {
        return orderInfo;
    }

    public String getScenario() {
        return scenario;
    }

    public NewPaymentRequest(final int amount, final String orderNumber, final String currency, final String transactionType, final String orderInfo, final String scenario) {
        this.amount = amount;
        this.orderNumber = orderNumber;
        this.currency = currency;
        this.scenario = scenario;
        this.transactionType = transactionType;
        this.orderInfo = orderInfo;
    }
}
