package com.monri.android.model;

import org.json.JSONException;
import org.json.JSONObject;

public class StartGooglePayMerchantInfo {
    private String merchantId;

    private String merchantName;

    private static final String MERCHANT_ID_KEY = "merchantId";
    private static final String MERCHANT_NAME_KEY = "merchantName";

    public StartGooglePayMerchantInfo(String merchantId, String merchantName) {
        this.merchantId = merchantId;
        this.merchantName = merchantName;
    }

    public static StartGooglePayMerchantInfo fromJSON(final JSONObject jsonObject) throws JSONException {
        return new StartGooglePayMerchantInfo(
                jsonObject.getString(MERCHANT_ID_KEY),
                jsonObject.getString(MERCHANT_NAME_KEY)
        );
    }
}
