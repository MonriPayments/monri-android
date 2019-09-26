package com.monri.android.model;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;


public class Token {

    private static final String FIELD_ID = "id";
    private static final String FIELD_STATUS = "status";
    private String id;

    /**
     * Constructor that should not be invoked in your code.  This is used by Monri to
     * create tokens using a Monri API response.
     */
    private Token(String id) {
        this.id = id;
    }

    /**
     * @return the {@link #id} of this token
     */
    public String getId() {
        return id;
    }

    @Nullable
    public static Token fromString(@Nullable String jsonString) {
        if (jsonString == null) {
            return null;
        }
        try {
            JSONObject tokenObject = new JSONObject(jsonString);
            return fromJson(tokenObject);
        } catch (JSONException exception) {
            return null;
        }
    }

    @Nullable
    public static Token fromJson(@Nullable JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }
        String status = MonriJsonUtils.optString(jsonObject, FIELD_STATUS);

        if (!"approved".equals(status)) {
            return null;
        }

        String tokenId = MonriJsonUtils.optString(jsonObject, FIELD_ID);

        if (tokenId == null) {
            return null;
        }

        return new Token(tokenId);
    }

}
