package com.monri.android.model;

import org.json.JSONException;
import org.json.JSONObject;

public class StartGooglePayTransactionInfo {
    private String countryCode;

    private String currencyCode;

    private String totalPriceStatus;

    private String totalPrice;

    private final static String COUNTRY_CODE_KEY = "countryCode";
    private final static String CURRENCY_CODE_KEY = "currencyCode";
    private final static String TOTAL_PRICE_STATUS = "totalPriceStatus";
    private final static String TOTAL_PRICE = "totalPrice";

    public StartGooglePayTransactionInfo(String totalPrice, String totalPriceStatus, String currencyCode, String countryCode) {
        this.totalPrice = totalPrice;
        this.totalPriceStatus = totalPriceStatus;
        this.currencyCode = currencyCode;
        this.countryCode = countryCode;
    }

    public static StartGooglePayTransactionInfo fromJSON(final JSONObject jsonObject) throws JSONException {
        return new StartGooglePayTransactionInfo(
                jsonObject.getString(COUNTRY_CODE_KEY),
                jsonObject.getString(CURRENCY_CODE_KEY),
                jsonObject.getString(TOTAL_PRICE_STATUS),
                jsonObject.getString(TOTAL_PRICE)
        );
    }
}
