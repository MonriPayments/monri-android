package com.monri.android;

import android.annotation.SuppressLint;

import com.monri.android.model.PaymentMethod;

import java.util.HashMap;
import java.util.Map;

import static com.monri.android.MonriNetworkUtils.removeNullAndEmptyParams;
import static com.monri.android.MonriTextUtils.nullIfBlank;

/**
 * Created by jasminsuljic on 2019-11-05.
 * MonriAndroid
 */
final class CreateTokenRequest {
    private final String authenticityToken;
    private final String tempCardId;
    private final String timestamp;
    private final String digest;
    private final String token;
    private final PaymentMethod paymentMethod;

    private CreateTokenRequest(String authenticityToken, String tempCardId, String timestamp, String digest, String token, PaymentMethod paymentMethod) {
        this.authenticityToken = authenticityToken;
        this.tempCardId = tempCardId;
        this.timestamp = timestamp;
        this.digest = digest;
        this.token = token;
        this.paymentMethod = paymentMethod;
    }


    @SuppressLint("DefaultLocale")
    static CreateTokenRequest create(PaymentMethod paymentMethod, TokenRequest tokenRequest, String authenticityToken) {

        return new CreateTokenRequest(
                authenticityToken,
                nullIfBlank(tokenRequest.getToken()),
                nullIfBlank(tokenRequest.getTimestamp()),
                tokenRequest.getDigest(),
                tokenRequest.getToken(),
                paymentMethod);
    }

    Map<String, Object> toJson() {
        Map<String, Object> params = new HashMap<>();

        params.put("authenticity_token", authenticityToken);
        params.put("temp_card_id", tempCardId);
        params.put("timestamp", timestamp);
        params.put("digest", digest);
        params.put("token", token);
        params.put("type", paymentMethod.paymentMethodType());

        params.putAll(paymentMethod.data());

        // Remove all null values; they cause validation errors
        removeNullAndEmptyParams(params);
        return params;
    }
}
