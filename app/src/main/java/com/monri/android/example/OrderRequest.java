package com.monri.android.example;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jasminsuljic on 2019-10-30.
 * MonriAndroid
 */
class OrderRequest {
    @JsonProperty("monriToken")
    String monriToken;
    @JsonProperty("order_number")
    String orderNumber;

    public OrderRequest(String monriToken, String orderNumber) {
        this.monriToken = monriToken;
        this.orderNumber = orderNumber;
    }

    public String getMonriToken() {
        return monriToken;
    }

    public String getOrderNumber() {
        return orderNumber;
    }
}
