package com.monri.android.google_pay;

import com.google.android.gms.wallet.IsReadyToPayRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MonriGooglePaymentRequestHelper {
    private final int apiVersion;
    private final int apiVersionMinor;
    private String totalPriceLabel;
    private boolean shippingAddressRequired;
    private JSONObject googlePaySessionParameters;
    private static final String API_VERSION_KEY = "apiVersion";
    private static final String API_VERSION_MINOR_KEY = "apiVersionMinor";
    private static final String ALLOWED_PAYMENT_METHODS_KEY = "allowedPaymentMethods";
    private static final String TRANSACTION_INFO_KEY = "transactionInfo";
    private static final String MERCHANT_INFO_KEY = "merchantInfo";
    private static final String TOTAL_PRICE_LABEL_KEY = "totalPriceLabel";
    private static final String SHIPPING_ADDRESS_KEY = "SHIPPING_ADDRESS";
    private static final String SHIPPING_OPTION_KEY = "SHIPPING_OPTION";
    private static final String CALLBACK_INTENTS_KEY = "callbackIntents";

    public MonriGooglePaymentRequestHelper(final int apiVersion, final int apiVersionMinor) {
        this.apiVersion = apiVersion;
        this.apiVersionMinor = apiVersionMinor;
    }

    public void setGooglePaySessionParameters(final JSONObject googlePaySessionParameters) {
        this.googlePaySessionParameters = googlePaySessionParameters;
    }

    public void setTotalPriceLabel(final String totalPriceLabel) {
        this.totalPriceLabel = totalPriceLabel;
    }

    private JSONObject getBaseRequest() throws JSONException {
        return new JSONObject()
                .put(API_VERSION_KEY, apiVersion)
                .put(API_VERSION_MINOR_KEY, apiVersionMinor);
    }

    public IsReadyToPayRequest getIsReadyToPayRequest() throws JSONException {
        final JSONObject isReadyToPayRequestJson = getBaseRequest().put(ALLOWED_PAYMENT_METHODS_KEY, getAllowedPaymentMethods());

        return IsReadyToPayRequest.fromJson(isReadyToPayRequestJson.toString());
    }

    public JSONObject getPaymentDataRequest() throws JSONException {

        JSONObject paymentDataRequest = getBaseRequest()
                .put(ALLOWED_PAYMENT_METHODS_KEY, getAllowedPaymentMethods())
                .put(MERCHANT_INFO_KEY, googlePaySessionParameters.getJSONObject(MERCHANT_INFO_KEY));
//
//        if (isShippingRequiredByMerchant()) {
//            paymentDataRequest.put("shippingAddressRequired", shippingAddressRequired);
//            paymentDataRequest.put("shippingAddressParameters", );
//        }

        JSONObject transactionInfoObject = googlePaySessionParameters.getJSONObject(TRANSACTION_INFO_KEY);

        if (totalPriceLabel != null) transactionInfoObject.put(TOTAL_PRICE_LABEL_KEY, totalPriceLabel);

        paymentDataRequest.put(TRANSACTION_INFO_KEY, transactionInfoObject);

        return paymentDataRequest;
    }

    private boolean isShippingRequiredByMerchant() throws JSONException {
        final JSONArray callbackIntents = googlePaySessionParameters.getJSONArray(CALLBACK_INTENTS_KEY);

        for (int i = 0; i < callbackIntents.length(); i++) {
            if (callbackIntents.get(i).equals(SHIPPING_ADDRESS_KEY) | callbackIntents.get(i).equals(SHIPPING_OPTION_KEY)) {
                return true;
            }
        }
        return false;
    }

    public JSONArray getAllowedPaymentMethods() throws JSONException {
        return new JSONArray().put(googlePaySessionParameters.getJSONObject(ALLOWED_PAYMENT_METHODS_KEY));
    }
}
