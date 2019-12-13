package com.monri.android.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jasminsuljic on 2019-11-12.
 * MonriAndroid
 */
public final class SavedCard extends PaymentMethod {

    private String panToken;
    private String cvv;

    public SavedCard(String panToken, String cvv) {
        this.panToken = panToken;
        this.cvv = cvv;
    }

    @Override
    public String paymentMethodType() {
        return PaymentMethod.TYPE_SAVED_CARD;
    }

    public SavedCard setPanToken(String panToken) {
        this.panToken = panToken;
        return this;
    }

    public SavedCard setCvv(String cvv) {
        this.cvv = cvv;
        return this;
    }

    @Override
    public Map<String, String> data() {
        final HashMap<String, String> data = new HashMap<>();
        data.put("cvv", cvv);
        data.put("pan_token", panToken);
        return data;
    }
}
