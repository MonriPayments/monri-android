package com.monri.android.model;

import java.util.Map;

/**
 * Created by jasminsuljic on 2019-11-12.
 * MonriAndroid
 */
public abstract class PaymentMethod {

    public static final String TYPE_CARD = "card";
    public static final String TYPE_SAVED_CARD = "saved_card";
    public static final String TYPE_DIRECT_PAYMENT = "direct_payment";

    public abstract String paymentMethodType();

    public abstract Map<String, String> data();

    public PaymentMethodParams toPaymentMethodParams() {
        return new PaymentMethodParams(paymentMethodType(), data());
    }
}
