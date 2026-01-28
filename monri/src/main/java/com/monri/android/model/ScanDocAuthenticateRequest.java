package com.monri.android.model;

import org.json.JSONException;
import org.json.JSONObject;
import java.nio.charset.StandardCharsets;

public class ScanDocAuthenticateRequest {
    private static final String USER_KEY_KEY = "user_key";
    private static final String SUB_CLIENT_KEY = "sub_client";
    private final byte[] userKey;
    private final String subClient;

    public ScanDocAuthenticateRequest(final byte[] userKey, final String subClient) {
        this.userKey = userKey;
        this.subClient = subClient;
    }

    public JSONObject toJSONObject() throws JSONException {
        return new JSONObject().put(USER_KEY_KEY, new String(userKey, StandardCharsets.UTF_8))
                               .put(SUB_CLIENT_KEY, subClient);
    }
}
