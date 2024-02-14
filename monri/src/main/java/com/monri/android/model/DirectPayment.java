package com.monri.android.model;

import java.util.HashMap;
import java.util.Map;

public class DirectPayment extends PaymentMethod {

    @Override
    public String paymentMethodType() {
        return PaymentMethod.TYPE_DIRECT_PAYMENT;
    }

    @Override
    public Map<String, String> data() {
        return new HashMap<>();
    }
}
