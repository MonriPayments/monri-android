package com.monri.android;

import com.monri.android.model.AccessToken;
import com.monri.android.model.ScanDocAuthenticateRequest;
import com.monri.android.model.ScanDocAuthenticateResponse;
import com.monri.android.model.ScanDocRefreshAccessTokenRequest;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Map;

class ScanDocAuthenticationManager {
    private final ScanDocUrlProvider scanDocUrlProvider;
    private final HttpsClient httpsClient;
    private final String userKey;
    private final String subClient;
    private AccessToken accessToken;
    private AccessToken refreshToken;

    protected ScanDocAuthenticationManager(final String userKey, final String subClient, final HttpsClient httpsClient, final ScanDocUrlProvider scanDocUrlProvider) {
        this.userKey = userKey;
        this.subClient = subClient;
        this.httpsClient = httpsClient;
        this.scanDocUrlProvider = scanDocUrlProvider;
    }

    public AccessToken authenticate() throws JSONException {
        if (accessToken == null || !accessToken.isValid()) {
            if (refreshToken != null && refreshToken.isValid()) {
                accessToken = fetchRefreshedAccessToken();
            } else {
                accessToken = fetchAccessToken();
            }
        }

        return accessToken;
    }

    private AccessToken fetchAccessToken() throws JSONException {
        final MonriHttpResult<JSONObject> response = httpsClient.httpsPOST(
                scanDocUrlProvider.getAuthenticationUrl(),
                new ScanDocAuthenticateRequest(userKey, subClient).toJSONObject(),
                Map.of(),
                false
        );

        if (response.getCause() == null) {
            final ScanDocAuthenticateResponse authenticateResponse = ScanDocAuthenticateResponse.fromJSON(response.getResult());

            accessToken = new AccessToken(authenticateResponse.getAccessToken(), false);
            refreshToken = new AccessToken(authenticateResponse.getRefreshToken(), true);

            return accessToken;
        } else {
            throw MonriHttpException.create(response.getCause(), MonriHttpExceptionCode.REQUEST_FAILED);
        }
    }

    private AccessToken fetchRefreshedAccessToken() throws JSONException {
        final MonriHttpResult<JSONObject> response = httpsClient.httpsPOST(
                scanDocUrlProvider.getAuthenticateRefreshUrl(),
                new ScanDocRefreshAccessTokenRequest(refreshToken.getValue()).toJSONObject(),
                Map.of(),
                false
        );

        if (response.getCause() == null) {
            accessToken = new AccessToken(ScanDocAuthenticateResponse.fromJSON(response.getResult()).getAccessToken(), false);
            return accessToken;
        } else {
            throw MonriHttpException.create(response.getCause(), MonriHttpExceptionCode.REQUEST_FAILED);
        }
    }
}
