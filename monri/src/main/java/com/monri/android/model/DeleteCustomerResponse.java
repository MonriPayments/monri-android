package com.monri.android.model;

import org.json.JSONException;
import org.json.JSONObject;

public class DeleteCustomerResponse {
    private final String status;
    private final String uuid;
    private final boolean deleted;

    public DeleteCustomerResponse(final String status, final String uuid, final boolean deleted) {
        this.status = status;
        this.uuid = uuid;
        this.deleted = deleted;
    }

    public static DeleteCustomerResponse fromJSON(JSONObject jsonObject) throws JSONException {
        return new DeleteCustomerResponse(
                jsonObject.getString("status"),
                jsonObject.getString("uuid"),
                jsonObject.getBoolean("deleted")
        );
    }

    public String getStatus() {
        return status;
    }

    public String getUuid() {
        return uuid;
    }

    public boolean isDeleted() {
        return deleted;
    }
}
