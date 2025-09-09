package com.monri.android.example;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;

public class GoogleApiRequestBuilderUtil {

    private static final String MERCHANT_ID = "webPayMerchant3083";

    public static JSONObject getIsReadyToPayRequest() throws JSONException {
        return getBaseRequest()
                .put("allowedPaymentMethods", new JSONArray(List.of(getBaseCardPaymentMethod())));
    }

    public static JSONObject getPaymentDataRequest() {
        try {
            return getBaseRequest()
                    .put("allowedPaymentMethods", getAllowedPaymentMethods())
                    .put("transactionInfo", getTransactionInfo("1"))
                    .put("merchantInfo", getMerchantInfo());

        } catch (JSONException e) {
            return null;
        }
    }

    private static JSONObject getTransactionInfo(final String price) throws JSONException {
        return new JSONObject()
                .put("totalPrice", price)
                .put("totalPriceStatus", "FINAL")
                .put("countryCode", "HR")
                .put("currencyCode", "EUR")
                .put("checkoutOption", "COMPLETE_IMMEDIATE_PURCHASE");
    }

    private static JSONObject getMerchantInfo() throws JSONException {
        return new JSONObject().put("merchantName", "Karolina Skunca");
    }

    private static JSONObject getBaseRequest() throws JSONException {
        return new JSONObject()
                .put("apiVersion", 2)
                .put("apiVersionMinor", 0);
    }

    private static JSONObject getBaseCardPaymentMethod() throws JSONException {
        return new JSONObject()
                .put("type", "CARD")
                .put("parameters", new JSONObject()
                        .put("allowedAuthMethods", getAllowedCardAuthMethods())
                        .put("allowedCardNetworks", getAllowedCardNetworks())
                ).put("tokenizationSpecification", getTokenizationSpecification());
    }

    private static JSONArray getAllowedCardNetworks() {
        return new JSONArray()
                .put("VISA")
                .put("MASTERCARD");
    }

    private static JSONArray getAllowedCardAuthMethods() {
        return new JSONArray()
                .put("PAN_ONLY")
                .put("CRYPTOGRAM_3DS");
    }

    public static JSONArray getAllowedPaymentMethods() throws JSONException {
        return new JSONArray().put(getCardPaymentMethod());
    }

    private static JSONObject getCardPaymentMethod() throws JSONException {
        return getBaseCardPaymentMethod()
                .put("tokenizationSpecification", getTokenizationSpecification());
    }

    private static JSONObject getTokenizationSpecification() throws JSONException {
        return new JSONObject()
                .put("type", "PAYMENT_GATEWAY")
                .put("parameters", new JSONObject()
                        .put("gateway", "monripayments")
                        .put("gatewayMerchantId", MERCHANT_ID)
                );

    }
}
