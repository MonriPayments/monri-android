package com.monri.android.model;

import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class GooglePayPayment extends PaymentMethod {

    private final Provider provider;

    final private JSONObject googlePaymentMethodData;

    public final static String GOOGLE_PAYMENT_METHOD_DATA_KEY = "payment-method-data";

    public GooglePayPayment(final Provider provider) {
        this(provider, null);
    }

    public GooglePayPayment(final Provider provider, final JSONObject googlePaymentMethodData) {
        this.provider = provider;
        this.googlePaymentMethodData = googlePaymentMethodData;
    }

    @Override
    public String paymentMethodType() {
        return provider.paymentMethod;
    }

    @Override
    public Map<String, String> data() {
        final Map<String, String> data = new HashMap<>();

        if (googlePaymentMethodData != null) data.put(GOOGLE_PAYMENT_METHOD_DATA_KEY, googlePaymentMethodData.toString());

        return data;
    }

    public enum Provider {
        GOOGLE_PAY(PaymentMethod.TYPE_GOOGLE_PAY);

        public final String paymentMethod;

        Provider(final String paymentMethod) {
            this.paymentMethod = paymentMethod;
        }
    }
}
