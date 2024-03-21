package com.monri.android.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by jasminsuljic on 2019-11-12.
 * MonriAndroid
 */
public abstract class PaymentMethod {

    public static final String TYPE_CARD = "card";
    public static final String TYPE_SAVED_CARD = "saved_card";
    public static final String TYPE_PAY_CEK_HR = "pay_cek_hr";

    public static final List<String> DIRECT_PAYMENT_METHODS = Collections.singletonList(TYPE_PAY_CEK_HR);

    public abstract String paymentMethodType();

    public abstract Map<String, String> data();

    public PaymentMethodParams toPaymentMethodParams() {
        return new PaymentMethodParams(paymentMethodType(), data());
    }
}
