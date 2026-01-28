package com.monri.android;

import androidx.annotation.NonNull;
import com.monri.android.model.AccessToken;
import com.monri.android.model.ScanDocAuthenticateRequest;
import com.monri.android.model.ScanDocAuthenticateResponse;
import com.monri.android.model.ScanDocRefreshAccessTokenRequest;
import org.json.JSONException;
import java.util.Map;

class ScanDocAuthenticationManagerImpl implements ScanDocAuthenticationManager {
    private final ScanDocUrlProvider scanDocUrlProvider;
    private final HttpsClientProxy httpsClientProxy;
    private final byte[] userKey;
    private final String subClient;
    private AccessToken accessToken;
    private AccessToken refreshToken;

    protected ScanDocAuthenticationManagerImpl(final byte[] userKey, final String subClient, final HttpsClientProxy httpsClientProxy, final ScanDocUrlProvider scanDocUrlProvider) {
        this.userKey = userKey;
        this.subClient = subClient;
        this.httpsClientProxy = httpsClientProxy;
        this.scanDocUrlProvider = scanDocUrlProvider;
    }

    @NonNull
    @Override
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
        final MonriHttpResult<ScanDocAuthenticateResponse> response = httpsClientProxy.doPostRequest(
                scanDocUrlProvider.getAuthenticationUrl(),
                Map.of(),
                new ScanDocAuthenticateRequest(userKey, subClient).toJSONObject().toString(),
                false,
                ScanDocAuthenticateResponse::fromJSON
        );

        accessToken = new AccessToken(response.getResult().getAccessToken(), false);
        refreshToken = new AccessToken(response.getResult().getRefreshToken(), true);

        return accessToken;
    }

    private AccessToken fetchRefreshedAccessToken() throws JSONException {
        final MonriHttpResult<ScanDocAuthenticateResponse> response = httpsClientProxy.doPostRequest(
                scanDocUrlProvider.getAuthenticateRefreshUrl(),
                Map.of(),
                new ScanDocRefreshAccessTokenRequest(refreshToken.getValue()).toJSONObject().toString(),
                false,
                ScanDocAuthenticateResponse::fromJSON
        );

        return new AccessToken(response.getResult().getAccessToken(), false);
    }
}
