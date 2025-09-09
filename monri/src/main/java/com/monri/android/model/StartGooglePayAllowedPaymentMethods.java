package com.monri.android.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StartGooglePayAllowedPaymentMethods {

    private String type;

    private List<GooglePayAllowedAuthMethods> allowedAuthMethods;

    private List<GooglePayAllowedCardNetworks> allowedCardNetworks;

    private StartGooglePayTokenizationSpecification tokenizationSpecification;

    private static final String TYPE_KEY = "type";
    private static final String PARAMETERS_KEY = "parameters";
    private static final String ALLOWED_AUTH_METHODS_KEY = "allowedAuthMethods";
    private static final String ALLOWED_CARD_NETWORKS_KEY = "allowedCardNetworks";
    private static final String TOKENIZATION_SPECIFICATION_KEY = "tokenizationSpecification";

    public StartGooglePayAllowedPaymentMethods(
            final String type, List<GooglePayAllowedAuthMethods> allowedAuthMethods,
            final List<GooglePayAllowedCardNetworks> allowedCardNetworks,
            final StartGooglePayTokenizationSpecification tokenizationSpecification
    ) {
        this.type = type;
        this.allowedAuthMethods = allowedAuthMethods;
        this.allowedCardNetworks = allowedCardNetworks;
        this.tokenizationSpecification = tokenizationSpecification;
    }

    public static StartGooglePayAllowedPaymentMethods fromJSON(final JSONObject jsonObject) throws JSONException {
        final JSONObject parametersObject = jsonObject.getJSONObject(PARAMETERS_KEY);

        final StartGooglePayTokenizationSpecification tokenizationSpecification = StartGooglePayTokenizationSpecification.fromJSON(jsonObject.getJSONObject(TOKENIZATION_SPECIFICATION_KEY));

        return new StartGooglePayAllowedPaymentMethods(
                jsonObject.getString(TYPE_KEY),
                getAllowedAuthMethods(parametersObject.getJSONArray(ALLOWED_AUTH_METHODS_KEY)),
                getAllowedCardNetworks(parametersObject.getJSONArray(ALLOWED_CARD_NETWORKS_KEY)),
                tokenizationSpecification
        );
    }

    private static List<GooglePayAllowedAuthMethods> getAllowedAuthMethods(final JSONArray array) throws JSONException {
        ArrayList<GooglePayAllowedAuthMethods> list = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            final String currentAuthMethodString = array.getString(i);
            list.add(GooglePayAllowedAuthMethods.valueOf(currentAuthMethodString));
        }

        return list;
    }

    private static List<GooglePayAllowedCardNetworks> getAllowedCardNetworks(final JSONArray array) throws JSONException {

        ArrayList<GooglePayAllowedCardNetworks> list = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            final String currentCardNetworkString = array.getString(i);
            list.add(GooglePayAllowedCardNetworks.valueOf(currentCardNetworkString));
        }

        return list;
    }
}
