package com.monri.android.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jasminsuljic on 2019-11-12.
 * MonriAndroid
 */
public abstract class PaymentMethod {

    public static final String TYPE_CARD = "card";
    public static final String TYPE_SAVED_CARD = "card";


    public PaymentMethod() {
    }

    public static PaymentMethod savedCard(String panToken, String cvv) {
        return new SavedCard(panToken, cvv);
    }

    public final Map<String, Object> toJson() {
        final HashMap<String, Object> result = new HashMap<>();

        result.put("type", paymentMethodType());
        result.put("data", data());

        return result;
    }

    public abstract String paymentMethodType();

    public abstract Map<String, Object> data();


}
