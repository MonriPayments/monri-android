package com.monri.android.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StartGooglePaySuccessDetails {

    private StartGooglePayAllowedPaymentMethods allowedPaymentMethods;
    private StartGooglePayTransactionInfo transactionInfo;
    private StartGooglePayMerchantInfo merchantInfo;
    private List<String> callbackIntents;

    private final static String ALLOWED_PAYMENT_METHODS_KEY = "allowedPaymentMethods";
    private final static String TRANSACTION_INFO_KEY = "transactionInfo";
    private final static String MERCHANT_INFO_KEY = "merchantInfo";
    private final static String CALLBACK_INTENTS_KEY = "callbackIntents";

    public StartGooglePaySuccessDetails(
            final StartGooglePayAllowedPaymentMethods allowedPaymentMethods,
            final StartGooglePayMerchantInfo merchantInfo,
            final StartGooglePayTransactionInfo transactionInfo,
            final List<String> callbackIntents
    ) {
        this.allowedPaymentMethods = allowedPaymentMethods;
        this.merchantInfo = merchantInfo;
        this.transactionInfo = transactionInfo;
        this.callbackIntents = callbackIntents;
    }

    public static StartGooglePaySuccessDetails fromJSON(final JSONObject jsonObject) throws JSONException {
        final StartGooglePayTransactionInfo transactionInfo = StartGooglePayTransactionInfo.fromJSON(jsonObject.getJSONObject(TRANSACTION_INFO_KEY));
        final StartGooglePayMerchantInfo merchantInfo = StartGooglePayMerchantInfo.fromJSON(jsonObject.getJSONObject(MERCHANT_INFO_KEY));
        final StartGooglePayAllowedPaymentMethods allowedPaymentMethods = StartGooglePayAllowedPaymentMethods.fromJSON(jsonObject.getJSONObject(ALLOWED_PAYMENT_METHODS_KEY));

        return new StartGooglePaySuccessDetails(allowedPaymentMethods, merchantInfo, transactionInfo, getCallbackIntents(jsonObject));
    }

    private static List<String> getCallbackIntents(final JSONObject jsonObject) throws JSONException {
        List<String> callbackIntents = new ArrayList<>();
        final JSONArray jsonArray = jsonObject.getJSONArray(CALLBACK_INTENTS_KEY);

        for (int i = 0; i < jsonArray.length(); i++) {
            callbackIntents.add(jsonArray.getString(i));
        }

        return callbackIntents;
    }
}
