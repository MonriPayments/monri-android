package com.monri.android;

import android.annotation.SuppressLint;

import com.monri.android.model.Card;

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
    private final String pan;
    private final String expirationDate;
    private final String cvv;
    private final String digest;
    private final String token;
    private final boolean tokenizePan;

    private CreateTokenRequest(String authenticityToken, String tempCardId, String timestamp, String pan, String expirationDate, String cvv, String digest, String token, boolean tokenizePan) {
        this.authenticityToken = authenticityToken;
        this.tempCardId = tempCardId;
        this.timestamp = timestamp;
        this.pan = pan;
        this.expirationDate = expirationDate;
        this.cvv = cvv;
        this.digest = digest;
        this.token = token;
        this.tokenizePan = tokenizePan;
    }

    @SuppressLint("DefaultLocale")
    static CreateTokenRequest create(Card card, TokenRequest tokenRequest, String authenticityToken) {

        return new CreateTokenRequest(
                authenticityToken,
                nullIfBlank(tokenRequest.getToken()),
                nullIfBlank(tokenRequest.getTimestamp()),
                nullIfBlank(card.getNumber()),
                String.format("%d%02d", card.getExpYear() - 2000, card.getExpMonth()),
                nullIfBlank(card.getCVC()),
                tokenRequest.getDigest(),
                tokenRequest.getToken(),
                card.isTokenizePan()
        );
    }

    Map<String, Object> toJson() {
        Map<String, Object> params = new HashMap<>();

        params.put("authenticity_token", authenticityToken);
        params.put("temp_card_id", tempCardId);
        params.put("timestamp", timestamp);
        params.put("pan", pan);
        params.put("expiration_date", expirationDate);
        params.put("cvv", cvv);
        params.put("digest", digest);
        params.put("token", token);
        params.put("tokenize_pan", tokenizePan);

        // Remove all null values; they cause validation errors
        removeNullAndEmptyParams(params);
        return params;
    }
}
