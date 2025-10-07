package com.monri.android;

import com.monri.android.model.AccessToken;
import com.monri.android.model.ScanDocApiOptions;
import com.monri.android.model.ScanDocExtractRequest;
import com.monri.android.model.ScanDocValidateRequest;
import com.monri.android.model.ScanDocValidateResponse;
import com.monri.android.model.ScanDocExtractResponse;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Map;

public class ScanDocHttpApiImpl {
    private static final String AUTHORIZATION_KEY = "Authorization";
    private final ScanDocUrlProvider scanDocUrlProvider;
    private final HttpsClient httpsClient;
    private final ScanDocAuthenticationManager authenticationManager;

    public ScanDocHttpApiImpl(final ScanDocApiOptions scanDocApiOptions) {
        this.scanDocUrlProvider = new ScanDocUrlProvider(scanDocApiOptions.getScanDocApiUrl());
        this.httpsClient = new HttpsClient();
        this.authenticationManager = new ScanDocAuthenticationManager(
                scanDocApiOptions.getUserKey(),
                scanDocApiOptions.getSubClient(),
                httpsClient,
                scanDocUrlProvider
        );
    }

    public MonriHttpResult<ScanDocValidateResponse> validateScannedCard(final ScanDocValidateRequest scanDocValidateRequest) {
        try {
            final MonriHttpResult<JSONObject> response = httpsClient.httpsPOST(
                    scanDocUrlProvider.getValidationUrl(),
                    scanDocValidateRequest.toJSONObject(),
                    createAuthorizationHeader(),
                    false
            );

            if (response.getCause() == null) {
                return MonriHttpResult.success(ScanDocValidateResponse.fromJSON(response.getResult()), response.getResponseCode());
            } else {
                return MonriHttpResult.failed(response.getCause());
            }

        } catch (final JSONException e) {
            return MonriHttpResult.failed(MonriHttpException.create(e, MonriHttpExceptionCode.REQUEST_FAILED));
        }
    }

    public MonriHttpResult<ScanDocExtractResponse> extractDataFromScannedCard(final ScanDocExtractRequest scanDocExtractRequest) {
        try {
            final MonriHttpResult<JSONObject> response = httpsClient.httpsPOST(
                    scanDocUrlProvider.getExtractionUrl(),
                    scanDocExtractRequest.toJSONObject(),
                    createAuthorizationHeader(),
                    false
            );

            if (response.getCause() == null) {
                return MonriHttpResult.success(ScanDocExtractResponse.fromJSON(response.getResult()), response.getResponseCode());
            } else {
                return MonriHttpResult.failed(response.getCause());
            }

        } catch (final JSONException e) {
            return MonriHttpResult.failed(MonriHttpException.create(e, MonriHttpExceptionCode.REQUEST_FAILED));
        }
    }

    private Map<String, String> createAuthorizationHeader() throws JSONException {
        final AccessToken accessToken = authenticationManager.authenticate();

        return Map.of(AUTHORIZATION_KEY, accessToken.getValue());
    }
}
