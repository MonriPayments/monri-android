package com.monri.android.model;

import java.util.HashMap;
import java.util.Map;

public class DirectPayment extends PaymentMethod {

    private final Provider paymentProvider;

    public DirectPayment(final Provider paymentProvider) {
        this.paymentProvider = paymentProvider;
    }

    @Override
    public String paymentMethodType() {
        return paymentProvider.paymentMethod;
    }

    @Override
    public Map<String, String> data() {
        return new HashMap<>();
    }

    public enum Provider {
        PAY_CEK_HR(PaymentMethod.TYPE_PAY_CEK_HR);

        public final String paymentMethod;

        Provider(final String paymentMethod) {
            this.paymentMethod = paymentMethod;
        }
    }
}
