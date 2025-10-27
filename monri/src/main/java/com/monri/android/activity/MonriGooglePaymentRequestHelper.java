package com.monri.android.activity;

import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MonriGooglePaymentRequestHelper {
    private static final int API_VERSION = 2;
    private static final int API_VERSION_MINOR = 0;
    private String totalPriceLabel;
    private JSONObject monriGooglePaySessionParameters;
    private static final String API_VERSION_KEY = "apiVersion";
    private static final String API_VERSION_MINOR_KEY = "apiVersionMinor";
    private static final String ALLOWED_PAYMENT_METHODS_KEY = "allowedPaymentMethods";
    private static final String TRANSACTION_INFO_KEY = "transactionInfo";
    private static final String MERCHANT_INFO_KEY = "merchantInfo";
    private static final String TOTAL_PRICE_LABEL_KEY = "totalPriceLabel";
    private static final String PAYMENT_METHOD_DATA_KEY = "paymentMethodData";

    public void setMonriGooglePaySessionParameters(final JSONObject monriGooglePaySessionParameters) {
        this.monriGooglePaySessionParameters = monriGooglePaySessionParameters;
    }

    public void setTotalPriceLabel(final String totalPriceLabel) {
        this.totalPriceLabel = totalPriceLabel;
    }

    private JSONObject getBaseRequest() throws JSONException {
        return new JSONObject()
                .put(API_VERSION_KEY, API_VERSION)
                .put(API_VERSION_MINOR_KEY, API_VERSION_MINOR);
    }

    public IsReadyToPayRequest getIsReadyToPayRequest() throws JSONException {
        final JSONObject isReadyToPayRequestJson = getBaseRequest().put(ALLOWED_PAYMENT_METHODS_KEY, getAllowedPaymentMethods());

        return IsReadyToPayRequest.fromJson(isReadyToPayRequestJson.toString());
    }

    public PaymentDataRequest getPaymentDataRequest() throws JSONException {

        final JSONObject paymentDataRequest = getBaseRequest()
                .put(ALLOWED_PAYMENT_METHODS_KEY, getAllowedPaymentMethods())
                .put(MERCHANT_INFO_KEY, monriGooglePaySessionParameters.getJSONObject(MERCHANT_INFO_KEY));

        final JSONObject transactionInfoObject = monriGooglePaySessionParameters.getJSONObject(TRANSACTION_INFO_KEY);
        if (totalPriceLabel != null) transactionInfoObject.put(TOTAL_PRICE_LABEL_KEY, totalPriceLabel);

        paymentDataRequest.put(TRANSACTION_INFO_KEY, transactionInfoObject);

        return PaymentDataRequest.fromJson(paymentDataRequest.toString());
    }

    public JSONArray getAllowedPaymentMethods() throws JSONException {
        return new JSONArray().put(monriGooglePaySessionParameters.getJSONObject(ALLOWED_PAYMENT_METHODS_KEY));
    }

    public JSONObject getPaymentMethodDataFromPaymentData(final PaymentData paymentData) throws JSONException {
        final String paymentInfo = paymentData.toJson();

        return new JSONObject(paymentInfo).getJSONObject(PAYMENT_METHOD_DATA_KEY);
    }
}
