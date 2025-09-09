package com.monri.android.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StartGooglePayResponse {

    private StartGooglePaySuccessDetails successDetails;
    private boolean isSuccessful;
    private List<String> errors;

    private final static String ERRORS_KEY = "errors";

    public StartGooglePayResponse(final StartGooglePaySuccessDetails successDetails, final boolean isSuccessful) {
        this.successDetails = successDetails;
        this.isSuccessful = isSuccessful;
    }

    public StartGooglePayResponse(final boolean isSuccessful, final List<String> errors) {
        this.isSuccessful = isSuccessful;
        this.errors = errors;
    }

    public static StartGooglePayResponse fromJSON(final JSONObject jsonObject) throws JSONException {
        if (jsonObject.has(ERRORS_KEY)) return new StartGooglePayResponse(false, createErrorsList(jsonObject.getJSONArray(ERRORS_KEY)));

        return new StartGooglePayResponse(
                StartGooglePaySuccessDetails.fromJSON(jsonObject),
                true
        );
    }

    private static List<String> createErrorsList(final JSONArray array) throws JSONException {
        List<String> list = new ArrayList<>();

        for (int i=0; i < array.length(); i++) {
            list.add(array.getString(i));
        }
        return list;
    }
}
