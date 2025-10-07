package com.monri.android.model;

import org.json.JSONException;
import org.json.JSONObject;

public class ScanDocRefreshAccessTokenRequest {
    private static final String REFRESH_TOKEN_KEY = "refresh_token";
    private final String refreshToken;

    public ScanDocRefreshAccessTokenRequest(final String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public JSONObject toJSONObject() throws JSONException {
        return new JSONObject().put(REFRESH_TOKEN_KEY, refreshToken);
    }
}
