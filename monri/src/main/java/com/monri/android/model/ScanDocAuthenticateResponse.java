package com.monri.android.model;

import androidx.annotation.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

public class ScanDocAuthenticateResponse {
    private static final String ACCESS_TOKEN_KEY = "access_token";
    private static final String REFRESH_TOKEN_KEY = "refresh_token";
    private final String accessToken;
    @Nullable
    private final String refreshToken;

    private ScanDocAuthenticateResponse(final String accessToken, @Nullable final String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public static ScanDocAuthenticateResponse fromJSON(final JSONObject response) throws JSONException {
        final String refreshToken = (response.has(REFRESH_TOKEN_KEY)) ? response.getString(REFRESH_TOKEN_KEY) : null;

        return new ScanDocAuthenticateResponse(response.getString(ACCESS_TOKEN_KEY), refreshToken);
    }

    public String getAccessToken() {
        return accessToken;
    }

    @Nullable
    public String getRefreshToken() {
        return refreshToken;
    }
}
