package com.monri.android.google_pay;

import com.google.android.gms.wallet.IsReadyToPayRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GooglePaymentSessionManager {
    private final int apiVersion;
    private final int apiVersionMinor;
    private JSONObject googlePaySessionParameters;
    private static final String API_VERSION_KEY = "apiVersion";
    private static final String API_VERSION_MINOR_KEY = "apiVersionMinor";
    private static final String ALLOWED_PAYMENT_METHODS_KEY = "allowedPaymentMethods";
    private static final String TRANSACTION_INFO_KEY = "transactionInfo";
    private static final String MERCHANT_INFO_KEY = "merchantInfo";

    public GooglePaymentSessionManager(final int apiVersion, final int apiVersionMinor) {
        this.apiVersion = apiVersion;
        this.apiVersionMinor = apiVersionMinor;
    }

    public void setGooglePaySessionParameters(final JSONObject googlePaySessionParameters) {
        this.googlePaySessionParameters = googlePaySessionParameters;
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
        return getBaseRequest()
                .put(ALLOWED_PAYMENT_METHODS_KEY, getAllowedPaymentMethods())
                .put(TRANSACTION_INFO_KEY, googlePaySessionParameters.getJSONObject(TRANSACTION_INFO_KEY))
                .put(MERCHANT_INFO_KEY, googlePaySessionParameters.getJSONObject(MERCHANT_INFO_KEY));
    }

    public JSONArray getAllowedPaymentMethods() throws JSONException {
        return new JSONArray().put(googlePaySessionParameters.getJSONObject(ALLOWED_PAYMENT_METHODS_KEY));
    }
}
