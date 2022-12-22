package com.monri.android.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CustomerDeleteResponse {
    private String status;
    private String id;
    private boolean deleted;

    public CustomerDeleteResponse(final String status, final String id, final boolean deleted) {
        this.status = status;
        this.id = id;
        this.deleted = deleted;
    }

    public static CustomerDeleteResponse fromJSON(JSONObject jsonObject) throws JSONException {
        Map<String, String> metadata = new HashMap<>();
        JSONObject metaJSONObject = jsonObject.getJSONObject("metadata");

        return new CustomerDeleteResponse(
                jsonObject.getString("status"),
                jsonObject.getString("id"),
                jsonObject.getBoolean("deleted")
        );
    }

    public String getStatus() {
        return status;
    }

    public String getId() {
        return id;
    }

    public boolean isDeleted() {
        return deleted;
    }
}
