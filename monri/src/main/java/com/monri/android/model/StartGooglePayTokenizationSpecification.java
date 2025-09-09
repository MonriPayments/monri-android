package com.monri.android.model;

import org.json.JSONException;
import org.json.JSONObject;

public class StartGooglePayTokenizationSpecification {

    private String type;
    private String gateway;
    private String gatewayMerchantId;

    private static final String TYPE_KEY = "type";
    private static final String PARAMETERS_KEY = "parameters";
    private static final String GATEWAY_KEY = "gateway";
    private static final String GATEWAY_MERCHANT_ID_KEY = "gatewayMerchantId";

    public StartGooglePayTokenizationSpecification(final String type, final String gateway, final String gatewayMerchantId) {
        this.type = type;
        this.gateway = gateway;
        this.gatewayMerchantId = gatewayMerchantId;
    }

    public static StartGooglePayTokenizationSpecification fromJSON(final JSONObject jsonObject) throws JSONException {
        final JSONObject parametersObject = jsonObject.getJSONObject(PARAMETERS_KEY);

        return new StartGooglePayTokenizationSpecification(
                jsonObject.getString(TYPE_KEY),
                parametersObject.getString(GATEWAY_KEY),
                parametersObject.getString(GATEWAY_MERCHANT_ID_KEY)
        );
    }
}
