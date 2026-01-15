package com.monri.android;

import com.monri.android.model.AccessToken;
import com.monri.android.model.ScanDocApiOptions;
import com.monri.android.model.ScanDocExtractRequest;
import com.monri.android.model.ScanDocValidateRequest;
import com.monri.android.model.ScanDocValidateResponse;
import com.monri.android.model.ScanDocExtractResponse;
import org.json.JSONException;
import java.util.Map;

public class ScanDocHttpApiImpl {
    private static final String AUTHORIZATION_KEY = "Authorization";
    private final ScanDocUrlProvider scanDocUrlProvider;
    private final HttpsClientProxyImpl httpsClientProxy;
    private final ScanDocAuthenticationManagerImpl authenticationManager;

    public ScanDocHttpApiImpl(final ScanDocApiOptions scanDocApiOptions) {
        this.scanDocUrlProvider = new ScanDocUrlProvider(scanDocApiOptions.getScanDocApiUrl());
        this.httpsClientProxy = new HttpsClientProxyImpl();
        this.authenticationManager = new ScanDocAuthenticationManagerImpl(
                scanDocApiOptions.getUserKey(),
                scanDocApiOptions.getSubClient(),
                this.httpsClientProxy,
                scanDocUrlProvider
        );
    }

    public MonriHttpResult<ScanDocValidateResponse> validateScannedCard(final ScanDocValidateRequest scanDocValidateRequest) {
        try {
            return httpsClientProxy.doPostRequest(
                    scanDocUrlProvider.getValidationUrl(),
                    createAuthorizationHeader(),
                    scanDocValidateRequest.toJSONObject().toString(),
                    false,
                    ScanDocValidateResponse::fromJSON
            );

        } catch (final JSONException e) {
            return MonriHttpResult.failed(MonriHttpException.create(e, MonriHttpExceptionCode.REQUEST_FAILED));
        }
    }

    public MonriHttpResult<ScanDocExtractResponse> extractDataFromScannedCard(final ScanDocExtractRequest scanDocExtractRequest) {
        try {
            return httpsClientProxy.doPostRequest(
                    scanDocUrlProvider.getExtractionUrl(),
                    createAuthorizationHeader(),
                    scanDocExtractRequest.toJSONObject().toString(),
                    false,
                    ScanDocExtractResponse::fromJSON
            );
        } catch (final JSONException e) {
            return MonriHttpResult.failed(MonriHttpException.create(e, MonriHttpExceptionCode.REQUEST_FAILED));
        }
    }

    private Map<String, String> createAuthorizationHeader() throws JSONException {
        final AccessToken accessToken = authenticationManager.authenticate();

        return Map.of(AUTHORIZATION_KEY, accessToken.getValue());
    }
}
