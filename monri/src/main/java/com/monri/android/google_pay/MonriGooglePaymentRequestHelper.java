package com.monri.android.google_pay;

import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentDataRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MonriGooglePaymentRequestHelper {
    private int apiVersion;
    private int apiVersionMinor;
    private String totalPriceLabel;
    private JSONObject monriGooglePaySessionParameters;
    private static final String API_VERSION_KEY = "apiVersion";
    private static final String API_VERSION_MINOR_KEY = "apiVersionMinor";
    private static final String ALLOWED_PAYMENT_METHODS_KEY = "allowedPaymentMethods";
    private static final String TRANSACTION_INFO_KEY = "transactionInfo";
    private static final String MERCHANT_INFO_KEY = "merchantInfo";
    private static final String TOTAL_PRICE_LABEL_KEY = "totalPriceLabel";

    public MonriGooglePaymentRequestHelper(final int apiVersion, final int apiVersionMinor) {
        this.apiVersion = apiVersion;
        this.apiVersionMinor = apiVersionMinor;
    }

    public void setMonriGooglePaySessionParameters(final JSONObject monriGooglePaySessionParameters) {
        this.monriGooglePaySessionParameters = monriGooglePaySessionParameters;
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

    public PaymentDataRequest getPaymentDataRequest() throws JSONException {

        JSONObject paymentDataRequest = getBaseRequest()
                .put(ALLOWED_PAYMENT_METHODS_KEY, getAllowedPaymentMethods())
                .put(MERCHANT_INFO_KEY, monriGooglePaySessionParameters.getJSONObject(MERCHANT_INFO_KEY));

        JSONObject transactionInfoObject = monriGooglePaySessionParameters.getJSONObject(TRANSACTION_INFO_KEY);
        if (totalPriceLabel != null) transactionInfoObject.put(TOTAL_PRICE_LABEL_KEY, totalPriceLabel);

        paymentDataRequest.put(TRANSACTION_INFO_KEY, transactionInfoObject);

        return PaymentDataRequest.fromJson(paymentDataRequest.toString());
    }

    public JSONArray getAllowedPaymentMethods() throws JSONException {
        return new JSONArray().put(monriGooglePaySessionParameters.getJSONObject(ALLOWED_PAYMENT_METHODS_KEY));
    }
}
